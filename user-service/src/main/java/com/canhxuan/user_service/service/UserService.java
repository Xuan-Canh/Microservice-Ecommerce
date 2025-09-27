package com.canhxuan.user_service.service;

import com.canhxuan.user_service.dto.CreateUserRequest;
import com.canhxuan.user_service.dto.UpdateUserRequest;
import com.canhxuan.user_service.entity.UserProfile;
import dto.ApiResponse;

import java.util.List;

public interface UserService {
    ApiResponse<UserProfile> getById(Long id);

    ApiResponse<List<UserProfile>> getAll();

    ApiResponse<UserProfile> create(CreateUserRequest request);

    ApiResponse<UserProfile> update(Long id, UpdateUserRequest request);

    ApiResponse<String> delete(Long id);
}
