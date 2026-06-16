package com.bitzh.lvtu.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class LegalArticle {
    private Long id;
    private Long documentId;
    private String articleNumber;
    private String title;
    private String chapterTitle;
    private String sectionTitle;
    private String content;
    private String sourceUrl;
    private Integer sortOrder;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
