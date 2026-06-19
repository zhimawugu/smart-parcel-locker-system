package com.smartparcel.locker.controller;

import com.smartparcel.locker.dto.LoginRequest;
import com.smartparcel.locker.dto.RegisterRequest;
import com.smartparcel.locker.service.UserService;
import com.smartparcel.locker.vo.ApiResponse;
import com.smartparcel.locker.vo.UserResponse;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST endpoints for user registration and login. All responses use the
 * unified {@link ApiResponse} envelope with HTTP 200.
 */
@RestController
@RequestMapping("/api/auth")
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/register")
    public ApiResponse<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.success(UserResponse.from(userService.register(request)));
    }

    @PostMapping("/login")
    public ApiResponse<UserResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(UserResponse.from(userService.login(request)));
    }
}
