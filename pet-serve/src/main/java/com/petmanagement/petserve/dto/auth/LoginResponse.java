package com.petmanagement.petserve.dto.auth;

public class LoginResponse {
    private String token;
    private Integer userId;
    private String username;
    private String roleName;

    public LoginResponse() {
    }

    public LoginResponse(String token, Integer userId, String username, String roleName) {
        this.token = token;
        this.userId = userId;
        this.username = username;
        this.roleName = roleName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}
