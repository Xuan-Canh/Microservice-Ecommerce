package com.canhxuan.identity_service.dto.request;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
