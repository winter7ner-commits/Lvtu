package com.bitzh.lvtu.entity;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class LegalDocument {
    private Long id;
    private String name;
    private Long categoryId;
    private LocalDate publishDate;
    private String lawLevel;
    private String effectiveStatus;
    private String sourceName;
    private String sourceUrl;
    private String summary;
    private Integer sortOrder;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
