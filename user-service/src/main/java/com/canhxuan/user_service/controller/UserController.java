package com.canhxuan.user_service.controller;

import com.canhxuan.user_service.dto.CreateUserRequest;
import com.canhxuan.user_service.dto.UpdateUserRequest;
import com.canhxuan.user_service.entity.UserProfile;
import com.canhxuan.user_service.service.UserService;
import dto.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/canhxuan/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserProfile>> getById (@PathVariable Long id) {
        return ResponseEntity.ok(userService.getById(id));
    }

    @GetMapping("/")
    public ResponseEntity<ApiResponse<List<UserProfile>>> getAll () {
        return ResponseEntity.ok(userService.getAll());
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<UserProfile>> create (@RequestBody CreateUserRequest request) {
        return ResponseEntity.ok(userService.create(request));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<UserProfile>> update (@RequestBody UpdateUserRequest request, @PathVariable Long id) {
        return ResponseEntity.ok(userService.update(id, request));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<String>> delete (@PathVariable Long id) {
        return ResponseEntity.ok(userService.delete(id));
    }
}
