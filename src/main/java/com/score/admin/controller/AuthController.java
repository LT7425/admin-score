package com.score.admin.controller;

import com.score.admin.common.ApiResponse;
import com.score.admin.domain.User;
import com.score.admin.dto.AuthResponse;
import com.score.admin.dto.RegisterRequest;
import com.score.admin.security.JwtUtil;
import com.score.admin.service.UserService;
import com.score.admin.service.RefreshTokenService;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    public AuthController(UserService userService, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, RefreshTokenService refreshTokenService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/register")
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        User user = userService.register(request.getUsername(), request.getPassword());
        java.util.List<String> roleCodes = userService.getUserRoleCodes(user.getUsername());
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roleCodes);
        String accessToken = jwtUtil.generateToken(user.getUsername(), claims);
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername(), new HashMap<>(claims));
        long expiresAt = System.currentTimeMillis() + jwtUtil.getExpirationMs();
        String expiresStr = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new java.util.Date(expiresAt));
        var refreshClaims = jwtUtil.parse(refreshToken);
        String jti = refreshClaims.getId();
        long refreshExpiresAt = refreshClaims.getExpiration().getTime();
        refreshTokenService.saveToken(jti, user.getUsername(), refreshExpiresAt);

        AuthResponse resp = new AuthResponse();
        resp.setAvatar("");
        resp.setUsername(user.getUsername());
        resp.setNickname(user.getUsername());
        resp.setRoles(roleCodes);
        resp.setPermissions(userService.getUserPermissions(user.getUsername()));
        resp.setAccessToken(accessToken);
        resp.setRefreshToken(refreshToken);
        resp.setExpires(expiresStr);
        return ApiResponse.ok(resp);
    }
}
