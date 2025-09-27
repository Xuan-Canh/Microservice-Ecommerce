package com.canhxuan.identity_service.controller;

import com.canhxuan.identity_service.dto.request.ForgotPasswordRequest;
import com.canhxuan.identity_service.dto.request.LoginRequest;
import com.canhxuan.identity_service.dto.request.ResetPasswordRequest;
import com.canhxuan.identity_service.dto.response.LoginResponse;
import com.canhxuan.identity_service.dto.request.RegisterRequest;
import com.canhxuan.identity_service.repository.IdentityRepository;
import com.canhxuan.identity_service.service.IdentityService;
import dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/canhxuan/auth")
@RequiredArgsConstructor
public class IdentityController {
    private final IdentityRepository identityRepository;
    private final IdentityService identityService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(identityService.login(loginRequest));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.ok(identityService.register(registerRequest));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        return ResponseEntity.ok(identityService.logout(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        identityRepository.deleteById(id);
        return ResponseEntity.ok("Delete success");
    }

    @GetMapping("/verify-email")
    public ResponseEntity<ApiResponse<String>> verifyEmail(@RequestParam String token) {
        ApiResponse<String> response = identityService.verifyEmail(token);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        return ResponseEntity.ok(identityService.forgotPassword(forgotPasswordRequest.getEmail()));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
        return ResponseEntity.ok(identityService.resetPassword(resetPasswordRequest));
    }

    @GetMapping("/check-username")
    public ResponseEntity<Boolean> checkUsernameExists(@RequestParam String username) {
        boolean isExist = identityService.checkUsernameExists(username);
        return ResponseEntity.ok(isExist);
    }
}
