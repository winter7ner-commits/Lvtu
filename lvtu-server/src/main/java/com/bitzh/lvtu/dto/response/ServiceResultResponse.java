package com.bitzh.lvtu.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public class ServiceResultResponse {

    private Long id;
    private Long orderId;
    private Long lawyerId;
    private String title;
    private String content;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<AttachmentResponse> attachments;
    private List<ResultRevisionResponse> revisionRequests;
    private List<ServiceResultSubmissionResponse> submissions;
    private Integer revisionRequestCount;
    private Integer maxRevisionRequestCount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getLawyerId() {
        return lawyerId;
    }

    public void setLawyerId(Long lawyerId) {
        this.lawyerId = lawyerId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<AttachmentResponse> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<AttachmentResponse> attachments) {
        this.attachments = attachments;
    }

    public List<ResultRevisionResponse> getRevisionRequests() {
        return revisionRequests;
    }

    public void setRevisionRequests(List<ResultRevisionResponse> revisionRequests) {
        this.revisionRequests = revisionRequests;
    }

    public List<ServiceResultSubmissionResponse> getSubmissions() {
        return submissions;
    }

    public void setSubmissions(List<ServiceResultSubmissionResponse> submissions) {
        this.submissions = submissions;
    }

    public Integer getRevisionRequestCount() {
        return revisionRequestCount;
    }

    public void setRevisionRequestCount(Integer revisionRequestCount) {
        this.revisionRequestCount = revisionRequestCount;
    }

    public Integer getMaxRevisionRequestCount() {
        return maxRevisionRequestCount;
    }

    public void setMaxRevisionRequestCount(Integer maxRevisionRequestCount) {
        this.maxRevisionRequestCount = maxRevisionRequestCount;
    }
}
