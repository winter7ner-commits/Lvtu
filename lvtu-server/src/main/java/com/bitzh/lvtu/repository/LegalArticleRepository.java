package com.bitzh.lvtu.repository;

import com.bitzh.lvtu.entity.LegalArticle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LegalArticleRepository extends JpaRepository<LegalArticle, Integer> {
    List<LegalArticle> findByDocumentIdOrderBySortOrder(Integer documentId);
}
