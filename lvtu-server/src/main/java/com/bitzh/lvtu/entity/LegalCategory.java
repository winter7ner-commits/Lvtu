package com.bitzh.lvtu.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "legal_category")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LegalCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(columnDefinition = "INT DEFAULT 1")
    private Integer status;
}
