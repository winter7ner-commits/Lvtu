package com.bitzh.lvtu.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LegalArticleFavorite {
    private Long id;
    private Long articleId;
    private Long userId;
    private LocalDateTime createdAt;
}
