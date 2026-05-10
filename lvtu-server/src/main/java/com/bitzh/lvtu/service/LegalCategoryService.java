package com.bitzh.lvtu.service;

import com.bitzh.lvtu.entity.LegalCategory;
import java.util.List;

public interface LegalCategoryService {
    List<LegalCategory> getAllCategories();
    LegalCategory getCategoryById(Long id);
    LegalCategory createCategory(LegalCategory category);
    LegalCategory updateCategory(Long id, LegalCategory category);
    void deleteCategory(Long id);
}