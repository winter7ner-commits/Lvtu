package com.bitzh.lvtu.dto;

import com.bitzh.lvtu.entity.User;

import java.time.LocalDateTime;

public class UserDTO {

    private Long userId;
    private String username;
    private String phone;
    private String email;
    private String avatarUrl;
    private Integer userType;
    private String adminRole;
    private Integer status;
    private Boolean isVerified;
    private Integer authStatus;
    private String region;
    private LocalDateTime createdTime;
    private LocalDateTime cancelRequestedAt;
    private LocalDateTime cancelEffectiveAt;
    private Integer cancelCoolingDays;

    public static UserDTO from(User user) {
        if (user == null) {
            return null;
        }
        UserDTO dto = new UserDTO();
        dto.setUserId(user.getUserId());
        dto.setUsername(user.getUsername());
        dto.setPhone(user.getPhone());
        dto.setEmail(user.getEmail());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setUserType(user.getUserType());
        dto.setAdminRole(normalizeAdminRole(user));
        dto.setStatus(user.getStatus());
        dto.setIsVerified(user.getIsVerified());
        dto.setAuthStatus(user.getAuthStatus());
        dto.setRegion(user.getRegion());
        dto.setCreatedTime(user.getCreatedTime());
        dto.setCancelRequestedAt(user.getCancelRequestedAt());
        dto.setCancelEffectiveAt(user.getCancelEffectiveAt());
        dto.setCancelCoolingDays(user.getCancelCoolingDays());
        return dto;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
    }

    public String getAdminRole() {
        return adminRole;
    }

    public void setAdminRole(String adminRole) {
        this.adminRole = adminRole;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Boolean getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
    }

    public Integer getAuthStatus() {
        return authStatus;
    }

    public void setAuthStatus(Integer authStatus) {
        this.authStatus = authStatus;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public LocalDateTime getCancelRequestedAt() {
        return cancelRequestedAt;
    }

    public void setCancelRequestedAt(LocalDateTime cancelRequestedAt) {
        this.cancelRequestedAt = cancelRequestedAt;
    }

    public LocalDateTime getCancelEffectiveAt() {
        return cancelEffectiveAt;
    }

    public void setCancelEffectiveAt(LocalDateTime cancelEffectiveAt) {
        this.cancelEffectiveAt = cancelEffectiveAt;
    }

    public Integer getCancelCoolingDays() {
        return cancelCoolingDays;
    }

    public void setCancelCoolingDays(Integer cancelCoolingDays) {
        this.cancelCoolingDays = cancelCoolingDays;
    }

    private static String normalizeAdminRole(User user) {
        if (user.getUserType() == null || user.getUserType() != 3) {
            return null;
        }
        String role = user.getAdminRole();
        if (role == null || role.trim().isEmpty()) {
            return "SUPER_ADMIN";
        }
        return role;
    }
}
