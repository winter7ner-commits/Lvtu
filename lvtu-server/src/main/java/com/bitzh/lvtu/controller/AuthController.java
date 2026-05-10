package com.bitzh.lvtu.controller;

import com.bitzh.lvtu.common.ApiResponse;
import com.bitzh.lvtu.dto.LoginRequest;
import com.bitzh.lvtu.dto.RegisterRequest;
import com.bitzh.lvtu.dto.UserDTO;
import com.bitzh.lvtu.entity.User;
import com.bitzh.lvtu.service.UserService;
import com.bitzh.lvtu.util.JwtUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

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

    @PostMapping("/logout")
    public ApiResponse<String> logout() {
        return ApiResponse.success("退出成功", null);
    }
}