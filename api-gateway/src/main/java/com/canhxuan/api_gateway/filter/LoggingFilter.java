package com.canhxuan.api_gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class LoggingFilter implements GlobalFilter {
    private static final Logger log = LoggerFactory.getLogger(LoggingFilter.class);
    private static final String REQUEST_LOG = "Incoming request: {} {} Headers: {} Query: {}";
    private static final String RESPONSE_LOG = "Response status: {} (Processed in {} ms)";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        long startTime = System.currentTimeMillis();
        log.info(REQUEST_LOG,
                exchange.getRequest().getMethod(),
                exchange.getRequest().getURI(),
                exchange.getRequest().getHeaders(),
                exchange.getRequest().getQueryParams());

        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            long duration = System.currentTimeMillis() - startTime;
            log.info(RESPONSE_LOG,
                    exchange.getResponse().getStatusCode() != null ? exchange.getResponse().getStatusCode() : "UNKNOWN",
                    duration);
        }));
    }
}
