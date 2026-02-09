package com.score.admin.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String auth = request.getHeader("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            String token = auth.substring(7);
            try {
                Claims claims = jwtUtil.parse(token);
                String email = claims.getSubject();
                Object rolesObj = claims.get("roles");
                java.util.List<String> roleList;
                if (rolesObj instanceof java.util.List<?> list) {
                    roleList = list.stream().filter(java.util.Objects::nonNull).map(Object::toString).collect(Collectors.toList());
                } else {
                    String roles = rolesObj == null ? "" : rolesObj.toString();
                    roleList = Arrays.stream(roles.split(",")).filter(s -> !s.isBlank()).map(String::trim).collect(Collectors.toList());
                }
                var authorities = roleList.stream()
                        .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                        .collect(Collectors.toList());
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(email, null, authorities);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception ignored) {
                // 无效令牌，继续链路，最终由权限判断拒绝
            }
        }
        filterChain.doFilter(request, response);
    }
}

