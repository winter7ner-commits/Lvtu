package com.bitzh.lvtu.controller;

import com.bitzh.lvtu.common.ApiResponse;
import com.bitzh.lvtu.dto.LoginRequest;
import com.bitzh.lvtu.dto.RegisterRequest;
import com.bitzh.lvtu.dto.UserDTO;
import com.bitzh.lvtu.entity.User;
import com.bitzh.lvtu.exception.BusinessException;
import com.bitzh.lvtu.service.AdminPermissionService;
import com.bitzh.lvtu.service.UserService;
import com.bitzh.lvtu.util.JwtUtil;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import com.bitzh.lvtu.dto.AdminRegisterRequest;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Resource
    private UserService userService;

    @Resource
    private AdminPermissionService adminPermissionService;

    @PostMapping("/register")
    public ApiResponse<UserDTO> register(@RequestBody RegisterRequest request) {
        try {
            User user = userService.register(request);
            return ApiResponse.success(UserDTO.from(user));
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(400, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.fail(500, "注册失败");
        }
    }

    @PostMapping("/admin/register")
    public ApiResponse<UserDTO> adminRegister(@RequestBody AdminRegisterRequest request, @RequestHeader(value = "Authorization", required = false) String authorization) {
        try {
            adminPermissionService.requireAdmin(authorization, "SUPER_ADMIN");

            User user = userService.adminRegister(request);
            return ApiResponse.success(UserDTO.from(user));
        } catch (BusinessException e) {
            return ApiResponse.fail(e.getCode(), e.getMessage());
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(400, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.fail(500, "管理员注册失败");
        }
    }

    @PostMapping("/login")
    public ApiResponse<Map<String, Object>> login(@RequestBody LoginRequest request) {
        try {
            User user = userService.login(request);
            if (user == null) {
                return ApiResponse.fail(401, "用户名或密码错误");
            }
            String token = JwtUtil.generateToken(user.getUserId(), user.getUsername());
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("user", UserDTO.from(user));
            return ApiResponse.success(data);
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(401, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.fail(500, "登录失败");
        }
    }

    @PostMapping("/admin/login")
    public ApiResponse<Map<String, Object>> adminLogin(@RequestBody LoginRequest request) {
        try {
            User user = userService.adminLogin(request);
            if (user == null) {
                return ApiResponse.fail(401, "管理员用户名或密码错误，或非管理员账户");
            }
            String token = JwtUtil.generateToken(user.getUserId(), user.getUsername());
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("user", UserDTO.from(user));
            return ApiResponse.success(data);
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(401, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.fail(500, "管理员登录失败");
        }
    }

    @PostMapping("/logout")
    public ApiResponse<String> logout() {
        return ApiResponse.success("退出成功", null);
    }

    @PostMapping("/change-password")
    public ApiResponse<String> changePassword(@RequestBody Map<String, String> params) {
        try {
            String username = params.get("username");
            String oldPassword = params.get("oldPassword");
            String newPassword = params.get("newPassword");
            userService.changePassword(username, oldPassword, newPassword);
            return ApiResponse.success("密码修改成功", null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(400, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.fail(500, "修改失败");
        }
    }



    @PostMapping("/forgot-password")
    public ApiResponse<String> forgotPassword(@RequestBody RegisterRequest request) {
        try {
            userService.resetPassword(request);
            return ApiResponse.success("密码已重置", null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(400, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.fail(500, "重置失败");
        }
    }

    @GetMapping("/me")
    public ApiResponse<UserDTO> me(@RequestHeader(value = "Authorization", required = false) String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return ApiResponse.fail(401, "未登录");
        }
        String token = authorization.substring(7);
        Long userId = JwtUtil.getUserId(token);
        if (userId == null) {
            return ApiResponse.fail(401, "无效 token");
        }
        User user = userService.findById(userId);
        if (user == null) {
            return ApiResponse.fail(401, "用户不存在");
        }
        return ApiResponse.success(UserDTO.from(user));
    }
}
