package com.canhxuan.identity_service.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.canhxuan.identity_service.dto.EmailDTO;
import com.canhxuan.identity_service.dto.request.LoginRequest;
import com.canhxuan.identity_service.dto.request.ResetPasswordRequest;
import com.canhxuan.identity_service.dto.response.LoginResponse;
import com.canhxuan.identity_service.dto.request.RegisterRequest;
import com.canhxuan.identity_service.entity.Identity;
import com.canhxuan.identity_service.repository.IdentityRepository;
import com.canhxuan.identity_service.service.IdentityService;
import com.canhxuan.identity_service.util.EventProducer;
import com.canhxuan.identity_service.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class IdentityServiceImpl implements IdentityService {

    @Autowired
    private IdentityRepository identityRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private EventProducer eventProducer;



    @Override
    public LoginResponse login(LoginRequest loginRequest) throws BadCredentialsException {
        LoginResponse response = new LoginResponse();
        LoginResponse.LoginData loginData = new LoginResponse.LoginData();
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        String accessToken = jwtUtil.generateAccessToken(authentication);
        String refreshToken = jwtUtil.generateRefreshToken(authentication);
        response.setMessage("Login success");
        response.setCode(200);
        loginData.setAccess_token(accessToken);
        loginData.setRefresh_token(refreshToken);
        loginData.setExpires_in(JwtUtil.EXPIRATION);
        loginData.setToken_type("Bearer");
        response.setData(loginData);
        log.info("response: {}", response);
        redisTemplate.opsForValue().set("Access:" + accessToken, loginRequest.getUsername(), 15, TimeUnit.MINUTES);
        redisTemplate.opsForValue().set("Refresh:" + refreshToken, loginRequest.getUsername(), 300, TimeUnit.MINUTES);
        return response;

    }

    @Override
    public ApiResponse<String> register(RegisterRequest registerRequest) {
        if (identityRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
            throw new RuntimeException("Username is already existed");
        }
        ApiResponse<String> response = new ApiResponse<>();
        response.setStatus(201);
        response.setMessage("Register successfully");
        Snowflake snowflake = new Snowflake(1, 1);
        Identity identity = new Identity();
        identity.setId(snowflake.nextId());
        identity.setUsername(registerRequest.getUsername());
        identity.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        identity.setEmail(registerRequest.getEmail());
        identity.setRole("ADMIN");
        identityRepository.save(identity);
        eventProducer.publishRegisterEvent(identity);
        if (identity.getEmail() != null) {
            String verifyEmailToken = UUID.randomUUID().toString();
            redisTemplate.opsForValue().set(verifyEmailToken, identity.getEmail(), 15, TimeUnit.MINUTES);
            EmailDTO emailDTO = new EmailDTO();
            emailDTO.setTo(identity.getEmail());
            emailDTO.setSubject("Ecommerce's identity service - Verify your email");
            emailDTO.setContent("Click link below to verify your email!!! \nhttp://localhost:8080/canhxuan/auth/verify-email?token=" + verifyEmailToken);
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String emailMessage = objectMapper.writeValueAsString(emailDTO);
                eventProducer.sendEmailMessage(emailMessage);
                response.setMessage("Register successfully. Please check your email to verify email");
            } catch (Exception e) {
                log.error("Error while converting emailDTO to string", e);
                throw new RuntimeException(e);
            }
        }
        response.setData("Your account is register successfully with username: " + registerRequest.getUsername());
        return response;
    }

    @Override
    public ApiResponse<String> logout(HttpServletRequest request) {
        ApiResponse<String> response = new ApiResponse<>();
        response.setStatus(200);
        response.setMessage("Logout success");
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid authorization header");
        }
        String token = authHeader.substring(7);
        redisTemplate.opsForValue().set("Blacklist:" + token, "true", 15, TimeUnit.MINUTES);
        return response;
    }

    @Override
    public ApiResponse<String> verifyEmail(String token) {
        ApiResponse<String> response = new ApiResponse<>();
        String email = redisTemplate.opsForValue().get(token);
        if (email == null) {
            response.setStatus(400);
            response.setMessage("Token is invalid or expired");
            return response;
        }
        Identity identity = identityRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Email not found"));
        identity.setVerifiedEmail(true);
        identityRepository.save(identity);
        redisTemplate.delete(token);
        response.setStatus(200);
        response.setMessage("Email verified successfully");
        return response;
    }

    @Override
    public ApiResponse<?> forgotPassword(String email) {
        if (identityRepository.findByEmail(email).isEmpty()) {
            throw new RuntimeException("Email not found");
        }
        String resetPasswordToken = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(resetPasswordToken, email, 15, TimeUnit.MINUTES);
        EmailDTO emailDTO = new EmailDTO();
        emailDTO.setTo(email);
        emailDTO.setSubject("Ecommerce's identity service - Reset your password");
        emailDTO.setContent("Click link below to reset your password!!! \nhttp://localhost:8080/canhxuan/auth/reset-password?token=" + resetPasswordToken);
        ApiResponse<?> response = new ApiResponse<>();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String emailMessage = objectMapper.writeValueAsString(emailDTO);
            eventProducer.sendEmailMessage(emailMessage);
            log.info("Sent reset password email to {}", email);
            response.setStatus(200);
            response.setMessage("Please check your email to reset password");
        } catch (Exception e) {
            log.error("Error while converting emailDTO to string", e);
            response.setStatus(500);
            response.setMessage("Error while sending email");
        }
        return response;
    }

    @Override
    public ApiResponse<?> resetPassword(ResetPasswordRequest request) {
        ApiResponse<?> response = new ApiResponse<>();
        String email = redisTemplate.opsForValue().get(request.getToken());
        if (email == null) {
            response.setStatus(400);
            response.setMessage("Token is invalid or expired");
            return response;
        }
        Identity identity = identityRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email not found"));
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            response.setStatus(400);
            response.setMessage("New password and confirm password do not match");
            return response;
        }
        identity.setPassword(passwordEncoder.encode(request.getNewPassword()));
        identityRepository.save(identity);
        redisTemplate.delete(request.getToken());
        EmailDTO emailDTO = new EmailDTO();
        emailDTO.setTo(email);
        emailDTO.setSubject("Ecommerce's identity service - Your password has been changed");
        emailDTO.setContent("Your password has been changed successfully. If you did not perform this action, please contact support immediately.");
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String emailMessage = objectMapper.writeValueAsString(emailDTO);
            eventProducer.sendEmailMessage(emailMessage);
        } catch (Exception e) {
            log.error("Error while converting emailDTO to string", e);
        }
        response.setStatus(200);
        response.setMessage("Password reset successfully");
        return response;
    }


    @Override
    public boolean checkUsernameExists(String username) {
        return identityRepository.findByUsername(username).isPresent();
    }
}
