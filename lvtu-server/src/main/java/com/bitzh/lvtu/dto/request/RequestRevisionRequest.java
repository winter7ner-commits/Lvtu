package com.bitzh.lvtu.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class RequestRevisionRequest {

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotBlank(message = "修改意见不能为空")
    @Size(max = 1000, message = "修改意见长度不能超过1000")
    private String content;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
