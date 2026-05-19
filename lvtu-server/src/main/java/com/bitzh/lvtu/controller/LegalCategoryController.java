package com.bitzh.lvtu.controller;

import com.bitzh.lvtu.common.ApiResponse;
import com.bitzh.lvtu.entity.LegalCategory;
import com.bitzh.lvtu.service.LegalCategoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class LegalCategoryController {

    private final LegalCategoryService categoryService;

    public LegalCategoryController(LegalCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ApiResponse<List<LegalCategory>> getAllCategories() {
        try {
            List<LegalCategory> categories = categoryService.getAllCategories();
            return ApiResponse.success(categories);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.fail(500, "获取分类列表失败: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ApiResponse<LegalCategory> getCategoryById(@PathVariable Long id) {
        try {
            LegalCategory category = categoryService.getCategoryById(id);
            if (category == null) {
                return ApiResponse.fail(404, "分类不存在");
            }
            return ApiResponse.success(category);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.fail(500, "获取分类失败: " + e.getMessage());
        }
    }

    @PostMapping
    public ApiResponse<LegalCategory> createCategory(@RequestBody LegalCategory category) {
        try {
            LegalCategory created = categoryService.createCategory(category);
            return ApiResponse.success(created);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.fail(500, "创建分类失败: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ApiResponse<LegalCategory> updateCategory(@PathVariable Long id, @RequestBody LegalCategory category) {
        try {
            LegalCategory updated = categoryService.updateCategory(id, category);
            return ApiResponse.success(updated);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.fail(500, "更新分类失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteCategory(@PathVariable Long id) {
        try {
            categoryService.deleteCategory(id);
            return ApiResponse.success(null);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.fail(500, "删除分类失败: " + e.getMessage());
        }
    }
}