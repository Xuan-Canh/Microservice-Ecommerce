package com.canhxuan.user_service.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.canhxuan.user_service.client.IdentityClientService;
import com.canhxuan.user_service.dto.CreateUserRequest;
import com.canhxuan.user_service.dto.UpdateUserRequest;
import com.canhxuan.user_service.entity.UserAddress;
import com.canhxuan.user_service.entity.UserProfile;
import com.canhxuan.user_service.repository.UserAddressRepository;
import com.canhxuan.user_service.repository.UserRepository;
import com.canhxuan.user_service.service.EventProducer;
import com.canhxuan.user_service.service.UserService;
import dto.ApiResponse;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EventProducer userEventPublisher;
    @Autowired
    private UserAddressRepository userAddressRepository;
    @Autowired
    private IdentityClientService identityClientService;

    @Override
    public ApiResponse<UserProfile> getById(Long id) {
        ApiResponse<UserProfile> response = new ApiResponse<>();
        response.setStatus(200);
        response.setMessage("Get user successfully");
        response.setData(userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found with id: " + id)));
        return response;
    }


    @Override
    public ApiResponse<List<UserProfile>> getAll() {
        ApiResponse<List<UserProfile>> response = new ApiResponse<>();
        response.setStatus(200);
        response.setMessage("Get all users successfully");
        response.setData(userRepository.findAll());
        return response;
    }

    @Override
    public ApiResponse<UserProfile> create(CreateUserRequest request) {
        ApiResponse<UserProfile> response = new ApiResponse<>();
        if (identityClientService.checkUsernameExists(request.getUsername())) {
            throw new RuntimeException(request.getUsername() + " already exists");
        }
        Snowflake snowflake = new Snowflake(1, 1);
        UserProfile user = new UserProfile();
        user.setId(snowflake.nextId());
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setDateOfBirth(request.getDateOfBirth());
        userRepository.save(user);
        if (request.getAddress() != null) {
            UserAddress userAddress = new UserAddress();
            userAddress.setUserProfile(user);
            userAddress.setName(request.getAddress().getName());
            userAddress.setCountry(request.getAddress().getCountry());
            userAddress.setCity(request.getAddress().getCity());
            userAddress.setDistrict(request.getAddress().getDistrict());
            userAddress.setStreet(request.getAddress().getStreet());
            userAddressRepository.save(userAddress);
        }
        request.setId(user.getId());
        userEventPublisher.publishUserCreated(request);
        response.setStatus(201);
        response.setMessage("Create user successfully");
        response.setData(user);
        log.info("Created user with user: " + user);
        return response;
    }

    @Override
    public ApiResponse<UserProfile> update(Long id, UpdateUserRequest request) {
        ApiResponse<UserProfile> response = new ApiResponse<>();
        UserProfile user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setDateOfBirth(request.getDateOfBirth());
        userRepository.save(user);
        if (request.getAddress() != null) {
            UserAddress userAddress = new UserAddress();
            userAddress.setUserProfile(user);
            userAddress.setName(request.getAddress().getName());
            userAddress.setCountry(request.getAddress().getCountry());
            userAddress.setCity(request.getAddress().getCity());
            userAddress.setDistrict(request.getAddress().getDistrict());
            userAddress.setStreet(request.getAddress().getStreet());
            userAddressRepository.save(userAddress);
        }
        request.setId(id);
        userEventPublisher.publishUserUpdated(request);
        log.info("Updated user with id: " + id);
        response.setStatus(200);
        response.setMessage("Update user successfully");
        response.setData(user);
        return response;
    }

    @Override
    public ApiResponse<String> delete(Long id) {
        userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        ApiResponse<String> response = new ApiResponse<>();
        response.setStatus(200);
        response.setMessage("Delete user successfully");
        userRepository.deleteById(id);
        return response;
    }
}
