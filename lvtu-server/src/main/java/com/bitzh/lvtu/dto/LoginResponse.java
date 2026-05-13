package com.bitzh.lvtu.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private Long userId;
    private String username;
    private String email;
    private String avatarUrl;
    private String phone;
    private Integer userType;
    private String region;
}
