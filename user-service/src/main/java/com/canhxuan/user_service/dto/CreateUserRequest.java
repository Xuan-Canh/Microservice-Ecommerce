package com.canhxuan.user_service.dto;

import lombok.Data;

import java.util.Date;

@Data
public class CreateUserRequest {
    private Long id;
    private String fullName;
    private String username;
    private Date dateOfBirth;
    private String phoneNumber;
    private String password;
    private String email;
    private AddressRequest address;
    private String role;
    private int status;
}
