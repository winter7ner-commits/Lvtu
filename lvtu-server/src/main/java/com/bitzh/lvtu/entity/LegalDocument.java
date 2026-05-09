
package com.bitzh.lvtu.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 法律文件实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LegalDocument {
    private Long id;
    private String name;
    private Long categoryId;
    private LocalDateTime publishDate;
    private Integer sortOrder = 0;
    private Integer status = 1;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
