package com.score.admin.controller;

import com.score.admin.common.ApiResponse;
import com.score.admin.common.BusinessException;
import com.score.admin.domain.User;
import com.score.admin.dto.AuthResponse;
import com.score.admin.dto.LoginRequest;
import com.score.admin.dto.RegisterRequest;
import com.score.admin.security.JwtUtil;
import com.score.admin.service.UserService;
import com.score.admin.service.RefreshTokenService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.*;

// 根路径别名，兼容前端调用 /login 与 /register
@RestController
public class OpenAuthController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private static final Logger logger = LoggerFactory.getLogger(OpenAuthController.class);

    public OpenAuthController(UserService userService, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, RefreshTokenService refreshTokenService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        User user = userService.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException(401, "用户名或密码错误"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(401, "用户名或密码错误");
        }
        List<String> roleCodes = userService.getUserRoleCodes(user.getEmail());
        logger.info("开始处理登录请求，用户名: {}", request.getEmail());

        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roleCodes);
        String accessToken = jwtUtil.generateToken(user.getEmail(), claims);
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail(), new HashMap<>(claims));
        long expiresAt = System.currentTimeMillis() + jwtUtil.getExpirationMs();
        String expiresStr = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date(expiresAt));
        var refreshClaims = jwtUtil.parse(refreshToken);
        String jti = refreshClaims.getId();
        long refreshExpiresAt = refreshClaims.getExpiration().getTime();
        refreshTokenService.saveToken(jti, user.getEmail(), refreshExpiresAt);

        AuthResponse resp = new AuthResponse();
        resp.setAvatar("");
        resp.setEmail(user.getEmail());
        resp.setUsername(user.getUsername());
        resp.setRoles(roleCodes);
        resp.setPermissions(userService.getUserPermissions(user.getEmail()));
        resp.setAccessToken(accessToken);
        resp.setRefreshToken(refreshToken);
        resp.setExpires(expiresStr);
        return ApiResponse.ok(resp);
    }

    @PostMapping("/register")
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        User user = userService.register(request.getEmail(), request.getUsername(), request.getPassword());
        List<String> roleCodes = userService.getUserRoleCodes(user.getEmail());
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roleCodes);
        String accessToken = jwtUtil.generateToken(user.getUsername(), claims);
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername(), new HashMap<>(claims));
        long expiresAt = System.currentTimeMillis() + jwtUtil.getExpirationMs();
        String expiresStr = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date(expiresAt));
        var refreshClaims = jwtUtil.parse(refreshToken);
        String jti = refreshClaims.getId();
        long refreshExpiresAt = refreshClaims.getExpiration().getTime();
        refreshTokenService.saveToken(jti, user.getUsername(), refreshExpiresAt);

        AuthResponse resp = new AuthResponse();
        resp.setAvatar("");
        resp.setUsername(user.getUsername());
        resp.setEmail(user.getEmail());
        resp.setRoles(roleCodes);
        resp.setPermissions(userService.getUserPermissions(user.getEmail()));
        resp.setAccessToken(accessToken);
        resp.setRefreshToken(refreshToken);
        resp.setExpires(expiresStr);
        return ApiResponse.ok(resp);
    }
}

