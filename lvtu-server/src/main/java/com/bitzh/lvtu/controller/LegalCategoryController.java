package com.bitzh.lvtu.controller;

import com.bitzh.lvtu.common.ApiResponse;
import com.bitzh.lvtu.entity.LegalCategory;
import com.bitzh.lvtu.service.AdminPermissionService;
import com.bitzh.lvtu.service.LegalCategoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class LegalCategoryController {

    private final LegalCategoryService categoryService;
    private final AdminPermissionService adminPermissionService;

    public LegalCategoryController(LegalCategoryService categoryService,
                                   AdminPermissionService adminPermissionService) {
        this.categoryService = categoryService;
        this.adminPermissionService = adminPermissionService;
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
    public ApiResponse<LegalCategory> createCategory(@RequestHeader(value = "Authorization", required = false) String authorization,
                                                     @RequestBody LegalCategory category) {
        adminPermissionService.requireAdmin(authorization, "SUPER_ADMIN", "OPERATOR");
        try {
            LegalCategory created = categoryService.createCategory(category);
            return ApiResponse.success(created);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.fail(500, "创建分类失败: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ApiResponse<LegalCategory> updateCategory(@RequestHeader(value = "Authorization", required = false) String authorization,
                                                     @PathVariable Long id,
                                                     @RequestBody LegalCategory category) {
        adminPermissionService.requireAdmin(authorization, "SUPER_ADMIN", "OPERATOR");
        try {
            LegalCategory updated = categoryService.updateCategory(id, category);
            return ApiResponse.success(updated);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.fail(500, "更新分类失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteCategory(@RequestHeader(value = "Authorization", required = false) String authorization,
                                            @PathVariable Long id) {
        adminPermissionService.requireAdmin(authorization, "SUPER_ADMIN", "OPERATOR");
        try {
            categoryService.deleteCategory(id);
            return ApiResponse.success(null);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.fail(500, "删除分类失败: " + e.getMessage());
        }
    }
}
