
package com.bitzh.lvtu.service.impl;

import com.bitzh.lvtu.entity.LegalCategory;
import com.bitzh.lvtu.mapper.LegalCategoryMapper;
import com.bitzh.lvtu.service.LegalCategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 法律分类业务逻辑实现
 */
@Service
public class LegalCategoryServiceImpl implements LegalCategoryService {

    private final LegalCategoryMapper categoryMapper;

    public LegalCategoryServiceImpl(LegalCategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    @Override
    public List&lt;LegalCategory&gt; getAllCategories() {
        return categoryMapper.selectByStatus(1);
    }

    @Override
    public List&lt;LegalCategory&gt; getCategoriesByParentId(Long parentId) {
        return categoryMapper.selectByParentIdAndStatus(parentId, 1);
    }

    @Override
    public LegalCategory getCategoryById(Long id) {
        return categoryMapper.selectById(id);
    }

    @Override
    @Transactional
    public LegalCategory createCategory(LegalCategory category) {
        categoryMapper.insert(category);
        return category;
    }

    @Override
    @Transactional
    public LegalCategory updateCategory(Long id, LegalCategory category) {
        category.setId(id);
        categoryMapper.update(category);
        return categoryMapper.selectById(id);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        categoryMapper.deleteById(id);
    }
}
