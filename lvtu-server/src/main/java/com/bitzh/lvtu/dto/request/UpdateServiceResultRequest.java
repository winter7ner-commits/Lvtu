package com.bitzh.lvtu.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public class UpdateServiceResultRequest {

    @NotNull(message = "律师ID不能为空")
    private Long lawyerId;
    @NotBlank(message = "结果标题不能为空")
    @Size(max = 100, message = "结果标题长度不能超过100")
    private String title;
    @Size(max = 5000, message = "结果说明长度不能超过5000")
    private String content;
    @Valid
    private List<AttachmentRequest> attachments;

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

    public List<AttachmentRequest> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<AttachmentRequest> attachments) {
        this.attachments = attachments;
    }
}
