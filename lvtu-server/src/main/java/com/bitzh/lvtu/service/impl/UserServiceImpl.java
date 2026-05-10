package com.bitzh.lvtu.service.impl;

import com.bitzh.lvtu.dto.LoginRequest;
import com.bitzh.lvtu.dto.RegisterRequest;
import com.bitzh.lvtu.entity.User;
import com.bitzh.lvtu.mapper.UserMapper;
import com.bitzh.lvtu.service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public User login(LoginRequest request) {
        if (request.getUsername() == null || request.getPassword() == null) {
            return null;
        }
        User user = userMapper.findByUsername(request.getUsername());
        if (user == null || user.getStatus() != 1) {
            return null;
        }
        String storedPassword = user.getPassword();
        if (storedPassword == null) {
            return null;
        }
        if (passwordEncoder.matches(request.getPassword(), storedPassword)) {
            return user;
        }
        if (!storedPassword.startsWith("$2a$") && !storedPassword.startsWith("$2b$") && !storedPassword.startsWith("$2y$")) {
            if (request.getPassword().equals(storedPassword)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public User adminLogin(LoginRequest request) {
        User user = login(request);
        if (user != null && user.getUserType() != null && user.getUserType() == 3) {
            return user;
        }
        return null;
    }

    @Override
    public User register(RegisterRequest request) {
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("密码不能为空");
        }
        if (userMapper.findByUsername(request.getUsername()) != null) {
            throw new IllegalArgumentException("用户名已存在");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setUserType(1);
        user.setAvatarUrl(null);
        user.setStatus(1);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        int result = userMapper.insert(user);
        if (result != 1) {
            throw new IllegalStateException("创建用户失败");
        }
        return user;
    }

    @Override
    public User findById(Long userId) {
        return userMapper.findById(userId);
    }

    @Override
    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }
}