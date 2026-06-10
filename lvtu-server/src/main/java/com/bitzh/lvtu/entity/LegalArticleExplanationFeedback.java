package com.bitzh.lvtu.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LegalArticleExplanationFeedback {
    private Long id;
    private Long articleId;
    private Long explanationId;
    private Long userId;
    private Boolean helpful;
    private String reason;
    private String content;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime handledAt;

    private String articleNumber;
    private String articleTitle;
    private String documentName;
    private String username;
    private String phone;
}
