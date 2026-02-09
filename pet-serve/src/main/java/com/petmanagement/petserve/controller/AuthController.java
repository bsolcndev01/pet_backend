package com.petmanagement.petserve.controller;

import com.petmanagement.petserve.common.ApiResponse;
import com.petmanagement.petserve.dto.auth.ForgotPasswordRequest;
import com.petmanagement.petserve.dto.auth.LoginRequest;
import com.petmanagement.petserve.dto.auth.LoginResponse;
import com.petmanagement.petserve.dto.auth.RegisterRequest;
import com.petmanagement.petserve.dto.auth.ResetPasswordRequest;
import com.petmanagement.petserve.dto.auth.UserProfileResponse;
import com.petmanagement.petserve.dto.auth.UpdateProfileRequest;
import com.petmanagement.petserve.dto.auth.ChangePasswordRequest;
import com.petmanagement.petserve.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(authService.login(request));
    }

    @PostMapping("/register")
    public ApiResponse<LoginResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.success(authService.register(request));
    }

    @PostMapping("/forgot-password")
    public ApiResponse<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.sendResetCode(request);
        return ApiResponse.success();
    }

    @PostMapping("/reset-password")
    public ApiResponse<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ApiResponse.success();
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestHeader(value = "Authorization", required = false) String authorization) {
        authService.logout(authorization);
        return ApiResponse.success();
    }

    @GetMapping("/me")
    public ApiResponse<UserProfileResponse> currentUser(@RequestHeader(value = "Authorization", required = false) String authorization) {
        return ApiResponse.success(authService.getProfile(authorization));
    }

    @PutMapping("/profile")
    public ApiResponse<UserProfileResponse> updateProfile(@Valid @RequestBody UpdateProfileRequest request,
                                                          @RequestHeader(value = "Authorization", required = false) String authorization) {
        return ApiResponse.success(authService.updateProfile(request, authorization));
    }

    @PutMapping("/change-password")
    public ApiResponse<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request,
                                            @RequestHeader(value = "Authorization", required = false) String authorization) {
        authService.changePassword(request, authorization);
        return ApiResponse.success();
    }
}
