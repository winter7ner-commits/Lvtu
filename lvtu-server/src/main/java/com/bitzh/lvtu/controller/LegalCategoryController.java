
package com.bitzh.lvtu.controller;

import com.bitzh.lvtu.dto.ResponseDTO;
import com.bitzh.lvtu.entity.LegalCategory;
import com.bitzh.lvtu.service.LegalCategoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 法律分类控制器
 */
@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
public class LegalCategoryController {

    private final LegalCategoryService categoryService;

    public LegalCategoryController(LegalCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseDTO&lt;List&lt;LegalCategory&gt;&gt; getAllCategories() {
        return ResponseDTO.success(categoryService.getAllCategories());
    }

    @GetMapping("/parent/{parentId}")
    public ResponseDTO&lt;List&lt;LegalCategory&gt;&gt; getCategoriesByParent(@PathVariable Long parentId) {
        return ResponseDTO.success(categoryService.getCategoriesByParentId(parentId));
    }

    @GetMapping("/{id}")
    public ResponseDTO&lt;LegalCategory&gt; getCategoryById(@PathVariable Long id) {
        LegalCategory category = categoryService.getCategoryById(id);
        if (category == null) {
            return ResponseDTO.error("分类不存在");
        }
        return ResponseDTO.success(category);
    }

    @PostMapping
    public ResponseDTO&lt;LegalCategory&gt; createCategory(@RequestBody LegalCategory category) {
        return ResponseDTO.success(categoryService.createCategory(category));
    }

    @PutMapping("/{id}")
    public ResponseDTO&lt;LegalCategory&gt; updateCategory(@PathVariable Long id, @RequestBody LegalCategory category) {
        return ResponseDTO.success(categoryService.updateCategory(id, category));
    }

    @DeleteMapping("/{id}")
    public ResponseDTO&lt;Void&gt; deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseDTO.success();
    }
}
