package com.bitzh.lvtu.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LegalArticleComment {
    private Long id;
    private Long articleId;
    private Long userId;
    private String username;
    private String avatarUrl;
    private String content;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
