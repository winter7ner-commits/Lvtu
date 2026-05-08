package com.bitzh.lvtu.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "legal_article")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LegalArticle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "document_id")
    private Integer documentId;

    @Column(name = "article_no", nullable = false)
    private String articleNo;

    @Column(name = "chapter_title")
    private String chapterTitle;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "sort_order")
    private Integer sortOrder;
}
