package com.canhxuan.identity_service.service;

import com.canhxuan.identity_service.dto.request.LoginRequest;
import com.canhxuan.identity_service.dto.request.ResetPasswordRequest;
import com.canhxuan.identity_service.dto.response.LoginResponse;
import com.canhxuan.identity_service.dto.request.RegisterRequest;
import dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface IdentityService {
    LoginResponse login(LoginRequest loginRequest);
    ApiResponse<String> register(RegisterRequest registerRequest);
    ApiResponse<String> logout(HttpServletRequest request);
    ApiResponse<String> verifyEmail(String token);
    ApiResponse<?> forgotPassword(String email);
    ApiResponse<?> resetPassword(ResetPasswordRequest resetPasswordRequest);
    boolean checkUsernameExists(String username);
}
