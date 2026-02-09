package com.petmanagement.petserve.dto.auth;

import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String phone;
    private String email;
    private String address;
    private String avatarUrl;
}
