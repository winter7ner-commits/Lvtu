package com.bitzh.lvtu.controller;

import com.bitzh.lvtu.dto.LoginRequest;
import com.bitzh.lvtu.dto.LoginResponse;
import com.bitzh.lvtu.entity.User;
import com.bitzh.lvtu.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:5174")
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest loginRequest) {
        User user = userMapper.findByUsername(loginRequest.getUsername());

        Map<String, Object> body = new HashMap<>();

        // 用户不存在
        if (user == null) {
            body.put("message", "用户名或密码错误");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
        }

        // 数据库密码为空（防止500）
        if (user.getPasswordHash() == null) {
            body.put("message", "该用户密码异常，请联系管理员");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
        }

        // 密码错误
        if (!user.getPasswordHash().equals(loginRequest.getPassword())) {
            body.put("message", "用户名或密码错误");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
        }

        // 登录成功
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setUserId(user.getUserId());
        loginResponse.setUsername(user.getUsername());
        loginResponse.setEmail(user.getEmail());
        loginResponse.setAvatarUrl(user.getAvatarUrl());
        loginResponse.setPhone(user.getPhone());
        loginResponse.setUserType(user.getUserType());
        loginResponse.setRegion(user.getRegion());

        Map<String, Object> result = new HashMap<>();
        result.put("data", loginResponse);
        result.put("message", "登录成功");

        return ResponseEntity.ok(result);
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser() {
        Map<String, Object> result = new HashMap<>();
        result.put("message", "获取成功");
        return ResponseEntity.ok(result);
    }
}
