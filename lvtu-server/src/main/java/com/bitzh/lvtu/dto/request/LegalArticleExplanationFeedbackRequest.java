package com.bitzh.lvtu.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LegalArticleExplanationFeedbackRequest {

    private Long explanationId;

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotNull(message = "反馈类型不能为空")
    private Boolean helpful;

    @Size(max = 50, message = "反馈原因不能超过50字")
    private String reason;

    @Size(max = 140, message = "反馈内容不能超过140字")
    private String content;
}
