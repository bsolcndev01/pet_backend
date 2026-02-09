package com.petmanagement.petserve.service;

import com.petmanagement.petserve.dto.auth.ChangePasswordRequest;
import com.petmanagement.petserve.dto.auth.ForgotPasswordRequest;
import com.petmanagement.petserve.dto.auth.LoginRequest;
import com.petmanagement.petserve.dto.auth.LoginResponse;
import com.petmanagement.petserve.dto.auth.RegisterRequest;
import com.petmanagement.petserve.dto.auth.ResetPasswordRequest;
import com.petmanagement.petserve.dto.auth.UpdateProfileRequest;
import com.petmanagement.petserve.dto.auth.UserProfileResponse;
import com.petmanagement.petserve.entity.User;
import com.petmanagement.petserve.entity.UserRole;
import com.petmanagement.petserve.exception.BusinessException;
import com.petmanagement.petserve.repository.UserRepository;
import com.petmanagement.petserve.repository.UserRoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final PasswordResetService passwordResetService;
    private final JavaMailSender mailSender;
    @Value("${spring.mail.username:}")
    private String mailFrom;
    @Value("${spring.mail.username:}")
    private String mailUsername;

    @Autowired
    public AuthService(UserRepository userRepository,
                       UserRoleRepository userRoleRepository,
                       PasswordEncoder passwordEncoder,
                       TokenService tokenService,
                       PasswordResetService passwordResetService,
                       JavaMailSender mailSender) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
        this.passwordResetService = passwordResetService;
        this.mailSender = mailSender;
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BusinessException(401, "用户不存在"));

        if (Boolean.FALSE.equals(user.getActive())) {
            throw new BusinessException(403, "账号已被禁用");
        }

        if (!passwordMatches(request.getPassword(), user.getPasswordHash())) {
            throw new BusinessException(401, "用户名或密码错误");
        }

        // 记录最近登录时间
        user.setLastLogin(java.time.LocalDateTime.now());
        userRepository.save(user);

        String token = tokenService.issue(user.getUserId());

        return new LoginResponse(
                token,
                user.getUserId(),
                user.getUsername(),
                user.getRole() != null ? user.getRole().getRoleName() : "宠物主人"
        );
    }

    public void logout(String authorizationHeader) {
        extractToken(authorizationHeader).ifPresent(tokenService::revoke);
    }

    public UserProfileResponse getProfile(String authorizationHeader) {
        Integer userId = extractToken(authorizationHeader)
                .flatMap(tokenService::resolve)
                .orElseThrow(() -> new BusinessException(401, "未登录或登录已失效"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));

        String roleName = user.getRole() != null ? user.getRole().getRoleName() : "宠物主人";

        return new UserProfileResponse(
                user.getUserId(),
                user.getUsername(),
                user.getUsername(),
                roleName,
                user.getPhone(),
                user.getEmail(),
                user.getAvatarUrl(),
                user.getAddress()
        );
    }

    public LoginResponse register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new BusinessException(400, "用户名已存在");
        }
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BusinessException(400, "邮箱已存在");
        }

        UserRole role = userRoleRepository.findByRoleName("宠物主人")
                .orElseGet(() -> userRoleRepository.findById(1).orElse(null));

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setActive(true);
        user.setRole(role);
        user.setLastLogin(java.time.LocalDateTime.now());

        User saved = userRepository.save(user);
        String token = tokenService.issue(saved.getUserId());

        return new LoginResponse(
                token,
                saved.getUserId(),
                saved.getUsername(),
                role != null ? role.getRoleName() : "宠物主人"
        );
    }

    private boolean passwordMatches(String rawPassword, String stored) {
        if (!StringUtils.hasText(stored)) {
            return false;
        }
        if (stored.startsWith("$2")) {
            return passwordEncoder.matches(rawPassword, stored);
        }
        // demo数据中部分密码是明文
        return stored.equals(rawPassword);
    }

    private java.util.Optional<String> extractToken(String authorizationHeader) {
        if (!StringUtils.hasText(authorizationHeader)) {
            return java.util.Optional.empty();
        }
        String token = authorizationHeader.replace("Bearer", "").trim();
        return StringUtils.hasText(token) ? java.util.Optional.of(token) : java.util.Optional.empty();
    }

    public void sendResetCode(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException(404, "邮箱未注册"));
        String code = passwordResetService.generateCode(request.getEmail(), 10);

        SimpleMailMessage message = new SimpleMailMessage();
        String from = StringUtils.hasText(mailFrom) ? mailFrom : mailUsername;
        if (StringUtils.hasText(from)) {
            message.setFrom(from);
        }
        message.setTo(request.getEmail());
        message.setSubject("宠物健康系统-找回密码");
        message.setText(String.format("您的验证码是：%s，有效期10分钟。", code));
        try {
            mailSender.send(message);
        } catch (Exception ex) {
            String errMsg = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
            if (!StringUtils.hasText(errMsg)) {
                errMsg = ex.getClass().getSimpleName();
            }
            log.error("Failed to send reset email to {}: {}", request.getEmail(), errMsg, ex);
            throw new BusinessException(500, "验证码发送失败，请检查SMTP用户名/授权码/端口配置");
        }
        // 本地调试用日志，便于获取验证码（生产可调低日志级别）
        log.info("Reset code generated for {}: {}", request.getEmail(), code);
    }

    public void resetPassword(ResetPasswordRequest request) {
        if (!passwordResetService.verify(request.getEmail(), request.getCode())) {
            throw new BusinessException(400, "验证码错误或已过期");
        }
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException(404, "邮箱未注册"));
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        passwordResetService.consume(request.getEmail());
    }

    public UserProfileResponse updateProfile(UpdateProfileRequest request, String authorizationHeader) {
        Integer userId = extractToken(authorizationHeader)
                .flatMap(tokenService::resolve)
                .orElseThrow(() -> new BusinessException(401, "未登录"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));

        if (StringUtils.hasText(request.getPhone())) {
            user.setPhone(request.getPhone());
        }
        if (StringUtils.hasText(request.getEmail())) {
            user.setEmail(request.getEmail());
        }
        if (StringUtils.hasText(request.getAddress())) {
            user.setAddress(request.getAddress());
        }
        if (StringUtils.hasText(request.getAvatarUrl())) {
            user.setAvatarUrl(request.getAvatarUrl());
        }

        User saved = userRepository.save(user);
        String roleName = saved.getRole() != null ? saved.getRole().getRoleName() : "宠物主人";
        return new UserProfileResponse(
                saved.getUserId(),
                saved.getUsername(),
                saved.getUsername(),
                roleName,
                saved.getPhone(),
                saved.getEmail(),
                saved.getAvatarUrl(),
                saved.getAddress()
        );
    }

    public void changePassword(ChangePasswordRequest request, String authorizationHeader) {
        Integer userId = extractToken(authorizationHeader)
                .flatMap(tokenService::resolve)
                .orElseThrow(() -> new BusinessException(401, "未登录"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));

        if (!passwordMatches(request.getOldPassword(), user.getPasswordHash())) {
            throw new BusinessException(400, "原密码不正确");
        }
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}
