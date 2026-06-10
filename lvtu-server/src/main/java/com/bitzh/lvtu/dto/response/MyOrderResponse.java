package com.bitzh.lvtu.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MyOrderResponse {

    private Long orderId;
    private Long targetLawyerId;
    private String assignmentType;
    private Integer serviceTypeId;
    private BigDecimal totalAmount;
    private String status;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
    private String formData;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getTargetLawyerId() {
        return targetLawyerId;
    }

    public void setTargetLawyerId(Long targetLawyerId) {
        this.targetLawyerId = targetLawyerId;
    }

    public String getAssignmentType() {
        return assignmentType;
    }

    public void setAssignmentType(String assignmentType) {
        this.assignmentType = assignmentType;
    }

    public Integer getServiceTypeId() {
        return serviceTypeId;
    }

    public void setServiceTypeId(Integer serviceTypeId) {
        this.serviceTypeId = serviceTypeId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getFormData() {
        return formData;
    }

    public void setFormData(String formData) {
        this.formData = formData;
    }
}
