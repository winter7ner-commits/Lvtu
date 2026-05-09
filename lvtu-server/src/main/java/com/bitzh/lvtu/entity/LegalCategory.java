
package com.bitzh.lvtu.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 法律分类实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LegalCategory {
    private Long id;
    private String name;
    private Long parentId = 0L;
    private Integer sortOrder = 0;
    private Integer status = 1;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

