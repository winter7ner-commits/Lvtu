package com.bitzh.lvtu.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LegalArticleExplanation {
    private Long id;
    private Long articleId;
    private String content;
    private String source;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
