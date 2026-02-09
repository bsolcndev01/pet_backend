package com.petmanagement.petserve.controller;

import com.petmanagement.petserve.common.ApiResponse;
import com.petmanagement.petserve.common.PageResponse;
import com.petmanagement.petserve.dto.appointment.AppointmentRequest;
import com.petmanagement.petserve.dto.appointment.AppointmentResponse;
import com.petmanagement.petserve.exception.BusinessException;
import com.petmanagement.petserve.service.AppointmentService;
import com.petmanagement.petserve.service.TokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/appointment")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final TokenService tokenService;

    @Autowired
    public AppointmentController(AppointmentService appointmentService, TokenService tokenService) {
        this.appointmentService = appointmentService;
        this.tokenService = tokenService;
    }

    @GetMapping("/upcoming")
    public ApiResponse<PageResponse<AppointmentResponse>> upcoming(@RequestParam(value = "page", defaultValue = "1") int page,
                                                                   @RequestParam(value = "size", defaultValue = "10") int size,
                                                                   @RequestParam(value = "vetUserId", required = false) Integer vetUserId,
                                                                   @RequestParam(value = "userId", required = false) Integer userId,
                                                                   @RequestHeader(value = "Authorization", required = false) String authorization) {
        Integer resolvedUser = resolveUserId(authorization, userId);
        return ApiResponse.success(appointmentService.listUpcoming(resolvedUser, vetUserId, page, size));
    }

    @GetMapping("/list")
    public ApiResponse<PageResponse<AppointmentResponse>> list(@RequestParam(value = "page", defaultValue = "1") int page,
                                                               @RequestParam(value = "size", defaultValue = "10") int size,
                                                               @RequestParam(value = "vetUserId", required = false) Integer vetUserId,
                                                               @RequestParam(value = "userId", required = false) Integer userId,
                                                               @RequestHeader(value = "Authorization", required = false) String authorization) {
        Integer resolvedUser = resolveUserId(authorization, userId);
        return ApiResponse.success(appointmentService.listAll(resolvedUser, vetUserId, page, size));
    }

    @PostMapping("/add")
    public ApiResponse<AppointmentResponse> add(@Valid @RequestBody AppointmentRequest request,
                                                @RequestParam(value = "userId", required = false) Integer userId,
                                                @RequestHeader(value = "Authorization", required = false) String authorization) {
        Integer resolvedUser = resolveUserId(authorization, userId);
        return ApiResponse.success(appointmentService.create(request, resolvedUser));
    }

    @PutMapping("/edit/{id}")
    public ApiResponse<AppointmentResponse> edit(@PathVariable("id") Integer appointmentId,
                                                 @Valid @RequestBody AppointmentRequest request,
                                                 @RequestParam(value = "userId", required = false) Integer userId,
                                                 @RequestHeader(value = "Authorization", required = false) String authorization) {
        Integer resolvedUser = resolveUserId(authorization, userId);
        return ApiResponse.success(appointmentService.update(appointmentId, request, resolvedUser));
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse<Void> delete(@PathVariable("id") Integer appointmentId,
                                    @RequestParam(value = "userId", required = false) Integer userId,
                                    @RequestHeader(value = "Authorization", required = false) String authorization) {
        Integer resolvedUser = resolveUserId(authorization, userId);
        appointmentService.delete(appointmentId, resolvedUser);
        return ApiResponse.success();
    }

    @PutMapping("/confirm/{id}")
    public ApiResponse<AppointmentResponse> confirm(@PathVariable("id") Integer appointmentId,
                                                    @RequestHeader(value = "Authorization", required = false) String authorization) {
        Integer resolvedUser = resolveUserId(authorization, null);
        return ApiResponse.success(appointmentService.confirm(appointmentId, resolvedUser));
    }

    private Integer resolveUserId(String authorization, Integer userId) {
        if (userId != null) {
            return userId;
        }
        String token = extractToken(authorization);
        if (!StringUtils.hasText(token)) {
            throw new BusinessException(401, "未登录或登录已失效");
        }
        return tokenService.resolve(token)
                .orElseThrow(() -> new BusinessException(401, "未登录或登录已失效"));
    }

    private String extractToken(String authorization) {
        if (!StringUtils.hasText(authorization)) {
            return null;
        }
        return authorization.replace("Bearer", "").trim();
    }
}
