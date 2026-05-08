package com.bitzh.lvtu.repository;

import com.bitzh.lvtu.entity.LegalDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LegalDocumentRepository extends JpaRepository<LegalDocument, Integer> {
    List<LegalDocument> findByCategoryId(Integer categoryId);
}
