package com.bitzh.lvtu.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class LegalCategory {
    private Long id;
    private String name;
    private Long parentId;
    private Integer sortOrder;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}