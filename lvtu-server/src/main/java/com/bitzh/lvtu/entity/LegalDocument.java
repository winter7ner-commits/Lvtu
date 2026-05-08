package com.bitzh.lvtu.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "legal_document")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LegalDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(name = "category_id")
    private Integer categoryId;

    @Column(columnDefinition = "TEXT")
    private String content;
}
