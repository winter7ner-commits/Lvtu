package com.bitzh.lvtu.repository;

import com.bitzh.lvtu.entity.LegalCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LegalCategoryRepository extends JpaRepository<LegalCategory, Integer> {
    List<LegalCategory> findByStatus(Integer status);
}
