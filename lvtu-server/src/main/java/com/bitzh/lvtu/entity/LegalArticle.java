
package com.bitzh.lvtu.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 法律条文实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LegalArticle {
    private Long id;
    private Long documentId;
    private String articleNo;
    private String chapterTitle;
    private String content;
    private Integer sortOrder = 0;
    private Integer status = 1;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
