package com.score.admin.controller;

import com.score.admin.common.ApiResponse;
import com.score.admin.common.BusinessException;
import com.score.admin.dto.AuthResponse;
import com.score.admin.security.JwtUtil;
import com.score.admin.service.RefreshTokenService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
public class TokenController {
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    public TokenController(JwtUtil jwtUtil, RefreshTokenService refreshTokenService) {
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
    }

    // 内部类添加public修饰符
    public static class RefreshTokenRequest {
        private String refreshToken;

        public String getRefreshToken() { return refreshToken; }
        public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    }

    @PostMapping("/refresh-token")
    public ApiResponse<AuthResponse> refresh(@RequestBody RefreshTokenRequest request) {
        // 方法内容保持不变
        try {
            var claims = jwtUtil.parse(request.getRefreshToken());
            Object type = claims.get("type");
            if (type == null || !"refresh".equals(type.toString())) {
                throw new BusinessException(401, "无效的刷新令牌");
            }
            String jti = claims.getId();
            if (refreshTokenService.isRevoked(jti)) {
                throw new BusinessException(401, "刷新令牌已失效");
            }
            String username = claims.getSubject();
            Map<String, Object> newClaims = new HashMap<>();
            newClaims.put("roles", claims.get("roles"));
            String accessToken = jwtUtil.generateToken(username, newClaims);
            String refreshToken = jwtUtil.generateRefreshToken(username, new HashMap<>(newClaims));
            refreshTokenService.revoke(jti);
            var newRefreshClaims = jwtUtil.parse(refreshToken);
            String newJti = newRefreshClaims.getId();
            long newRefreshExpiresAt = newRefreshClaims.getExpiration().getTime();
            refreshTokenService.saveToken(newJti, username, newRefreshExpiresAt);
            long expiresAt = System.currentTimeMillis() + jwtUtil.getExpirationMs();
            String expiresStr = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date(expiresAt));

            AuthResponse resp = new AuthResponse();
            resp.setAccessToken(accessToken);
            resp.setRefreshToken(refreshToken);
            resp.setExpires(expiresStr);
            return ApiResponse.ok(resp);
        } catch (Exception e) {
            throw new BusinessException(401, "刷新令牌失败");
        }
    }
}