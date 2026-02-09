package com.petmanagement.petserve.controller;

import com.petmanagement.petserve.common.ApiResponse;
import com.petmanagement.petserve.common.PageResponse;
import com.petmanagement.petserve.dto.finance.FinanceRecordRequest;
import com.petmanagement.petserve.dto.finance.FinanceRecordResponse;
import com.petmanagement.petserve.exception.BusinessException;
import com.petmanagement.petserve.service.FinanceService;
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
@RequestMapping("/api/finance")
public class FinanceController {

    private final FinanceService financeService;
    private final TokenService tokenService;

    @Autowired
    public FinanceController(FinanceService financeService, TokenService tokenService) {
        this.financeService = financeService;
        this.tokenService = tokenService;
    }

    @GetMapping("/list")
    public ApiResponse<PageResponse<FinanceRecordResponse>> list(@RequestParam(value = "page", defaultValue = "1") int page,
                                                                 @RequestParam(value = "size", defaultValue = "10") int size,
                                                                 @RequestParam(value = "userId", required = false) Integer userId,
                                                                 @RequestHeader(value = "Authorization", required = false) String authorization) {
        Integer resolvedUser = resolveUserId(authorization, userId);
        return ApiResponse.success(financeService.list(resolvedUser, page, size));
    }

    @PostMapping("/add")
    public ApiResponse<FinanceRecordResponse> add(@Valid @RequestBody FinanceRecordRequest request,
                                                  @RequestHeader(value = "Authorization", required = false) String authorization) {
        Integer resolvedUser = resolveUserId(authorization, null);
        return ApiResponse.success(financeService.create(request, resolvedUser));
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
