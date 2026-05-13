package com.bitzh.lvtu.dto.request;

import jakarta.validation.constraints.NotNull;

public class AcceptOrderRequest {

    @NotNull(message = "律师ID不能为空")
    private Long lawyerId;

    public Long getLawyerId() {
        return lawyerId;
    }

    public void setLawyerId(Long lawyerId) {
        this.lawyerId = lawyerId;
    }
}
