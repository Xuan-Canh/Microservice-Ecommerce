package com.canhxuan.api_gateway.filter;

import com.canhxuan.api_gateway.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Set;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AuthenticationFilter implements GlobalFilter, Ordered {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String ERROR_UNAUTHORIZED = "{\"error\": \"Unauthorized\", \"message\": \"%s\"}";
    private static final Set<String> ADMIN_PATHS = Set.of(
            "/canhxuan/users/create",
            "/canhxuan/users/update",
            "/canhxuan/users/delete"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        String method = exchange.getRequest().getMethod().name();

        if (isPublicEndpoint(path, method)) {
            String header = exchange.getRequest().getHeaders().getFirst("Authorization");
            if (header != null && header.startsWith("Bearer ")) {
                String token = header.substring(7);
                if (jwtUtil.validateToken(token) && !redisTemplate.hasKey("Blacklist:" + token)) {
                    Claims claims = jwtUtil.getClaims(token);
                    exchange = exchange.mutate()
                            .request(exchange.getRequest().mutate()
                                    .header("X-User-Id", claims.get("userId").toString())
                                    .header("X-User-Name", claims.getSubject())
                                    .header("X-User-Role", claims.get("roles").toString())
                                    .build())
                            .build();
                }
            }
            return chain.filter(exchange);
        }

        String header = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            return sendErrorResponse(exchange, "Access denied because token is invalid or null");
        }
        String token = header.substring(7);

        if (!jwtUtil.validateToken(token)) {
            return sendErrorResponse(exchange, "Access denied because token is invalid");
        }

        if (redisTemplate.hasKey("Blacklist:" + token)) {
            return sendErrorResponse(exchange, "Access denied because token is revoked");
        }

        Claims claims = jwtUtil.getClaims(token);
        String role = claims.get("roles").toString();

        if (!checkRolePermission(role, path)) {
            return sendErrorResponse(exchange, "Access denied because role is not permitted");
        }

        exchange = exchange.mutate()
                .request(exchange.getRequest().mutate()
                        .header("X-User-Id", claims.get("userId").toString())
                        .header("X-User-Name", claims.getSubject())
                        .header("X-User-Role", role)
                        .build())
                .build();

        return chain.filter(exchange);
    }

    private boolean isPublicEndpoint(String path, String method) {
        return path.startsWith("/canhxuan/auth/") ||
                path.startsWith("/canhxuan/cart") ||
                path.startsWith("/canhxuan/orders") ||
                method.equals("GET");
    }

    private boolean checkRolePermission(String role, String path) {
        if (isAdminEndpoint(path)) {
            return role.contains("ADMIN");
        }
        if (isUserEndpoint(path)) {
            return role.contains("USER") || role.contains("ADMIN");
        }
        return true;
    }

    private boolean isAdminEndpoint(String path) {
        return ADMIN_PATHS.stream().anyMatch(path::startsWith);
    }

    private boolean isUserEndpoint(String path) {
        return path.startsWith("/canhxuan/users/profile");
    }

    private Mono<Void> sendErrorResponse(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String body = String.format(ERROR_UNAUTHORIZED, message);
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
