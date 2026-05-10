package com.bitzh.lvtu.service.impl;

import com.bitzh.lvtu.entity.LegalCategory;
import com.bitzh.lvtu.mapper.LegalCategoryMapper;
import com.bitzh.lvtu.service.LegalCategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LegalCategoryServiceImpl implements LegalCategoryService {

    private final LegalCategoryMapper categoryMapper;

    public LegalCategoryServiceImpl(LegalCategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    @Override
    public List<LegalCategory> getAllCategories() {
        return categoryMapper.findAll();
    }

    @Override
    public LegalCategory getCategoryById(Long id) {
        return categoryMapper.findById(id);
    }

    @Override
    public LegalCategory createCategory(LegalCategory category) {
        if (category.getStatus() == null) {
            category.setStatus(1);
        }
        if (category.getSortOrder() == null) {
            category.setSortOrder(0);
        }
        categoryMapper.insert(category);
        return category;
    }

    @Override
    public LegalCategory updateCategory(Long id, LegalCategory category) {
        category.setId(id);
        categoryMapper.update(category);
        return category;
    }

    @Override
    public void deleteCategory(Long id) {
        categoryMapper.deleteById(id);
    }
}