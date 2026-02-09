package com.petmanagement.petserve.controller;

import com.petmanagement.petserve.common.ApiResponse;
import com.petmanagement.petserve.common.PageResponse;
import com.petmanagement.petserve.dto.reminder.ReminderRequest;
import com.petmanagement.petserve.dto.reminder.ReminderResponse;
import com.petmanagement.petserve.exception.BusinessException;
import com.petmanagement.petserve.service.ReminderService;
import com.petmanagement.petserve.service.TokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reminder")
public class ReminderController {

    private final ReminderService reminderService;
    private final TokenService tokenService;

    @Autowired
    public ReminderController(ReminderService reminderService, TokenService tokenService) {
        this.reminderService = reminderService;
        this.tokenService = tokenService;
    }

    @GetMapping("/upcoming")
    public ApiResponse<PageResponse<ReminderResponse>> upcoming(@RequestParam(value = "page", defaultValue = "1") int page,
                                                                @RequestParam(value = "size", defaultValue = "10") int size,
                                                                @RequestParam(value = "userId", required = false) Integer userId,
                                                                @RequestHeader(value = "Authorization", required = false) String authorization) {
        Integer resolvedUser = resolveUserId(authorization, userId);
        return ApiResponse.success(reminderService.listUpcoming(resolvedUser, page, size));
    }

    @GetMapping("/list")
    public ApiResponse<PageResponse<ReminderResponse>> list(@RequestParam(value = "page", defaultValue = "1") int page,
                                                            @RequestParam(value = "size", defaultValue = "10") int size,
                                                            @RequestParam(value = "userId", required = false) Integer userId,
                                                            @RequestHeader(value = "Authorization", required = false) String authorization) {
        Integer resolvedUser = resolveUserId(authorization, userId);
        return ApiResponse.success(reminderService.listAll(resolvedUser, page, size));
    }

    @PostMapping("/add")
    public ApiResponse<ReminderResponse> add(@Valid @RequestBody ReminderRequest request) {
        return ApiResponse.success(reminderService.create(request));
    }

    private Integer resolveUserId(String authorization, Integer userId) {
        if (userId != null) {
            return userId;
        }
        String token = extractToken(authorization);
        if (StringUtils.hasText(token)) {
            return tokenService.resolve(token)
                    .orElseThrow(() -> new BusinessException(401, "未登录或登录已失效"));
        }
        throw new BusinessException(401, "未登录或登录已失效");
    }

    private String extractToken(String authorization) {
        if (!StringUtils.hasText(authorization)) {
            return null;
        }
        return authorization.replace("Bearer", "").trim();
    }
}
