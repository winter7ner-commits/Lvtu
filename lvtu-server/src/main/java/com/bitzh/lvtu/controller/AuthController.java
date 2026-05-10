package com.bitzh.lvtu.controller;

import com.bitzh.lvtu.common.ApiResponse;
import com.bitzh.lvtu.dto.LoginRequest;
import com.bitzh.lvtu.dto.RegisterRequest;
import com.bitzh.lvtu.dto.UserDTO;
import com.bitzh.lvtu.entity.User;
import com.bitzh.lvtu.service.UserService;
import com.bitzh.lvtu.util.JwtUtil;
<<<<<<< HEAD
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
=======
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
>>>>>>> 2c7bb808696ce5aba4b1bc1a4e70731964c3986b

import java.util.HashMap;
import java.util.Map;

<<<<<<< HEAD
=======
import com.bitzh.lvtu.dto.AdminRegisterRequest;

>>>>>>> 2c7bb808696ce5aba4b1bc1a4e70731964c3986b
@RestController
@RequestMapping("/api/auth")
public class AuthController {

<<<<<<< HEAD
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }
=======
    @Resource
    private UserService userService;
>>>>>>> 2c7bb808696ce5aba4b1bc1a4e70731964c3986b

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

<<<<<<< HEAD
=======
    @PostMapping("/admin/register")
    public ApiResponse<UserDTO> adminRegister(@RequestBody AdminRegisterRequest request, @RequestHeader(value = "Authorization", required = false) String authorization) {
        try {
            // 验证管理员权限
            if (authorization == null || !authorization.startsWith("Bearer ")) {
                return ApiResponse.fail(401, "需要管理员权限");
            }
            String token = authorization.substring(7);
            Long adminId = JwtUtil.getUserId(token);
            if (adminId == null) {
                return ApiResponse.fail(401, "无效token");
            }
            User admin = userService.findById(adminId);
            if (admin == null || admin.getUserType() != 3) {
                return ApiResponse.fail(403, "需要管理员权限");
            }

            User user = userService.adminRegister(request);
            return ApiResponse.success(UserDTO.from(user));
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(400, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.fail(500, "管理员注册失败");
        }
    }

>>>>>>> 2c7bb808696ce5aba4b1bc1a4e70731964c3986b
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
        } catch (Exception e) {
            return ApiResponse.fail(500, "管理员登录失败");
        }
    }

<<<<<<< HEAD
=======
    @PostMapping("/logout")
    public ApiResponse<String> logout() {
        return ApiResponse.success("退出成功", null);
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

>>>>>>> 2c7bb808696ce5aba4b1bc1a4e70731964c3986b
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
<<<<<<< HEAD

    @PostMapping("/logout")
    public ApiResponse<String> logout() {
        return ApiResponse.success("退出成功", null);
    }
}
=======
}
>>>>>>> 2c7bb808696ce5aba4b1bc1a4e70731964c3986b
