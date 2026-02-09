package com.petmanagement.petserve.dto.auth;

public class UserProfileResponse {
    private Integer id;
    private String username;
    private String nickname;
    private String roleName;
    private String phone;
    private String email;
    private String avatarUrl;
    private String address;

    public UserProfileResponse() {
    }

    public UserProfileResponse(Integer id, String username, String nickname, String roleName, String phone, String email, String avatarUrl, String address) {
        this.id = id;
        this.username = username;
        this.nickname = nickname;
        this.roleName = roleName;
        this.phone = phone;
        this.email = email;
        this.avatarUrl = avatarUrl;
        this.address = address;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
