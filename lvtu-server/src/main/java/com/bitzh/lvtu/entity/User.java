package com.bitzh.lvtu.entity;

import java.time.LocalDateTime;

public class User {

    private Long userId;
    private String username;
    private String passwordHash;
    private String phone;
    private String email;
    private String avatarUrl;
    private Integer userType;
    private String adminRole;
    private Integer status;
    private Boolean isVerified;
    private Integer authStatus;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
    private String region;
    private LocalDateTime cancelRequestedAt;
    private LocalDateTime cancelEffectiveAt;
    private Integer cancelCoolingDays;

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

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
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

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
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
}
