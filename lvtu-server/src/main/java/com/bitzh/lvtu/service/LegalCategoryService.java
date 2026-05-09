
package com.bitzh.lvtu.service;

import com.bitzh.lvtu.entity.LegalCategory;
import java.util.List;

/**
 * 法律分类业务逻辑接口
 */
public interface LegalCategoryService {
    List&lt;LegalCategory&gt; getAllCategories();
    List&lt;LegalCategory&gt; getCategoriesByParentId(Long parentId);
    LegalCategory getCategoryById(Long id);
    LegalCategory createCategory(LegalCategory category);
    LegalCategory updateCategory(Long id, LegalCategory category);
    void deleteCategory(Long id);
}
