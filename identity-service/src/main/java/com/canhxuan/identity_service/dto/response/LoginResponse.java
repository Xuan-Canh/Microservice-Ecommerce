package com.canhxuan.identity_service.dto.response;

import lombok.Data;

@Data
public class LoginResponse {
    private String message;
    private int code;
    private LoginData data;

    @Data
    public static class LoginData {
        private String access_token;
        private String refresh_token;
        private long expires_in;
        private String token_type;
    }
}
