package com.petmanagement.petserve.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

import com.petmanagement.petserve.common.ApiResponse;
import com.petmanagement.petserve.dto.plan.PlanDto;
import com.petmanagement.petserve.dto.plan.PlanRequest;
import com.petmanagement.petserve.exception.BusinessException;
import com.petmanagement.petserve.service.PlanService;
import com.petmanagement.petserve.service.TokenService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/plans")
public class PlanController {

    private final PlanService planService;
    private final TokenService tokenService;

    @Autowired
    public PlanController(PlanService planService, TokenService tokenService) {
        this.planService = planService;
        this.tokenService = tokenService;
    }

    @GetMapping
    public ApiResponse<List<PlanDto>> list(@RequestParam Integer petId,
                                           @RequestHeader(value = "Authorization", required = false) String authorization) {
        Integer userId = resolveUser(authorization);
        return ApiResponse.success(planService.listPlans(petId, userId));
    }

    @PostMapping
    public ApiResponse<PlanDto> create(@Valid @RequestBody PlanRequest request,
                                       @RequestHeader(value = "Authorization", required = false) String authorization) {
        Integer userId = resolveUser(authorization);
        return ApiResponse.success(planService.create(request, userId));
    }

    @PutMapping("/{id}")
    public ApiResponse<PlanDto> update(@PathVariable("id") Integer planId,
                                       @Valid @RequestBody PlanRequest request,
                                       @RequestHeader(value = "Authorization", required = false) String authorization) {
        Integer userId = resolveUser(authorization);
        return ApiResponse.success(planService.update(planId, request, userId));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable("id") Integer planId,
                                    @RequestHeader(value = "Authorization", required = false) String authorization) {
        Integer userId = resolveUser(authorization);
        planService.delete(planId, userId);
        return ApiResponse.success();
    }

    private Integer resolveUser(String authorization) {
        if (!StringUtils.hasText(authorization)) {
            throw new BusinessException(401, "未登录或登录已失效");
        }
        return tokenService.resolve(authorization.replace("Bearer", "").trim())
                .orElseThrow(() -> new BusinessException(401, "未登录或登录已失效"));
    }
}
