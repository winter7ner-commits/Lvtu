package com.bitzh.lvtu.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class InterventionActionRequest {

    @NotBlank(message = "处理动作不能为空")
    private String actionType;

    @NotBlank(message = "处理说明不能为空")
    @Size(max = 500, message = "处理说明不能超过500字")
    private String reason;

    @NotNull(message = "管理员ID不能为空")
    private Long adminId;

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }
}
