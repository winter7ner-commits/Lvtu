package com.bitzh.lvtu.dto.request;

import jakarta.validation.constraints.NotNull;

public class ConfirmOrderRequest {

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
