package com.bitzh.lvtu.service.impl;

import com.bitzh.lvtu.entity.User;
import com.bitzh.lvtu.exception.BusinessException;
import com.bitzh.lvtu.service.AdminPermissionService;
import com.bitzh.lvtu.service.UserService;
import com.bitzh.lvtu.util.JwtUtil;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class AdminPermissionServiceImpl implements AdminPermissionService {

    private final UserService userService;

    public AdminPermissionServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public User requireAdmin(String authorization, String... allowedRoles) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new BusinessException(401, "请先登录后台账号");
        }
        Long userId = JwtUtil.getUserId(authorization.substring(7));
        if (userId == null) {
            throw new BusinessException(401, "后台登录已失效");
        }
        User user = userService.findById(userId);
        if (user == null || user.getUserType() == null || user.getUserType() != 3) {
            throw new BusinessException(403, "需要后台管理员权限");
        }
        String role = normalizeRole(user);
        if (allowedRoles != null && allowedRoles.length > 0) {
            boolean allowed = Arrays.stream(allowedRoles).anyMatch(role::equals);
            if (!allowed) {
                throw new BusinessException(403, "当前后台角色无权访问该功能");
            }
        }
        user.setAdminRole(role);
        return user;
    }

    private String normalizeRole(User user) {
        String role = user.getAdminRole();
        if (role == null || role.trim().isEmpty()) {
            return "SUPER_ADMIN";
        }
        return role.trim().toUpperCase();
    }
}

