package com.bitzh.lvtu.entity;

import lombok.Data;
import java.util.Date;

@Data
public class User {
    private Long userId;
    private String username;
    private String passwordHash;
    private String phone;
    private String email;
    private String avatarUrl;
    private Integer userType;
    private Integer status;
    private Boolean isVerified;
    private Integer authStatus;
    private Date createdTime;
    private Date updatedTime;
    private String region;
}
