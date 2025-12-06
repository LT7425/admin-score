package com.score.admin.dto;

import java.util.List;

public class AuthResponse {
    private String avatar;
    private String username;
    private String email;
    private List<String> roles;
    private List<String> permissions;
    private String accessToken;
    private String refreshToken;
    private String expires; // e.g. "YYYY/MM/DD HH:mm:ss"

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }
    public List<String> getPermissions() { return permissions; }
    public void setPermissions(List<String> permissions) { this.permissions = permissions; }
    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    public String getExpires() { return expires; }
    public void setExpires(String expires) { this.expires = expires; }
}

