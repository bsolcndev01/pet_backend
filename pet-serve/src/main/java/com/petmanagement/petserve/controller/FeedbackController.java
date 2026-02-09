package com.petmanagement.petserve.controller;

import com.petmanagement.petserve.common.ApiResponse;
import com.petmanagement.petserve.entity.UserFeedback;
import com.petmanagement.petserve.exception.BusinessException;
import com.petmanagement.petserve.repository.UserFeedbackRepository;
import com.petmanagement.petserve.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    private final UserFeedbackRepository userFeedbackRepository;
    private final TokenService tokenService;

    @Autowired
    public FeedbackController(UserFeedbackRepository userFeedbackRepository, TokenService tokenService) {
        this.userFeedbackRepository = userFeedbackRepository;
        this.tokenService = tokenService;
    }

    @GetMapping("/my")
    public ApiResponse<List<UserFeedback>> myFeedback(@RequestParam(value = "userId", required = false) Integer userId,
                                                      @RequestHeader(value = "Authorization", required = false) String authorization) {
        Integer resolvedUserId = resolveUserId(userId, authorization);
        if (resolvedUserId == null) {
            throw new BusinessException(401, "用户未登录");
        }
        return ApiResponse.success(userFeedbackRepository.findByUserIdOrderByCreatedAtDesc(resolvedUserId));
    }

    private Integer resolveUserId(Integer userId, String authorization) {
        if (userId != null) {
            return userId;
        }
        String token = extractToken(authorization);
        if (StringUtils.hasText(token)) {
            return tokenService.resolve(token).orElse(null);
        }
        return null;
    }

    private String extractToken(String authorization) {
        if (!StringUtils.hasText(authorization)) {
            return null;
        }
        return authorization.replace("Bearer", "").trim();
    }
}
