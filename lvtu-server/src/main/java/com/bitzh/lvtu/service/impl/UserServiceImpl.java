package com.bitzh.lvtu.service.impl;

import com.bitzh.lvtu.dto.LoginRequest;
import com.bitzh.lvtu.dto.RegisterRequest;
import com.bitzh.lvtu.entity.User;
import com.bitzh.lvtu.mapper.UserMapper;
import com.bitzh.lvtu.service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

<<<<<<< HEAD
import java.time.LocalDateTime;
=======
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import com.bitzh.lvtu.dto.AdminRegisterRequest;
>>>>>>> 2c7bb808696ce5aba4b1bc1a4e70731964c3986b

@Service
public class UserServiceImpl implements UserService {

<<<<<<< HEAD
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
        this.passwordEncoder = new BCryptPasswordEncoder();
=======
    @Resource
    private UserMapper userMapper;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public User register(RegisterRequest request) {
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("密码不能为空");
        }
        if (userMapper.selectByUsername(request.getUsername()) != null) {
            throw new IllegalArgumentException("用户名已存在");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setAvatarUrl(null);
        user.setUserType(1);
        user.setStatus(1);
        user.setIsVerified(false);
        user.setAuthStatus(0);
        user.setRegion(null);
        user.setCreatedTime(LocalDateTime.now());
        user.setUpdatedTime(LocalDateTime.now());

        int result = userMapper.insertUser(user);
        if (result != 1) {
            throw new IllegalStateException("创建用户失败");
        }
        return user;
    }

    @Override
    public User adminRegister(AdminRegisterRequest request) {
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("密码不能为空");
        }
        if (request.getUserType() == null || request.getUserType() < 1 || request.getUserType() > 3) {
            throw new IllegalArgumentException("用户类型无效");
        }
        if (userMapper.selectByUsername(request.getUsername()) != null) {
            throw new IllegalArgumentException("用户名已存在");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setAvatarUrl(null);
        user.setUserType(request.getUserType());
        user.setStatus(1);
        user.setIsVerified(false);
        user.setAuthStatus(0);
        user.setRegion(null);
        user.setCreatedTime(LocalDateTime.now());
        user.setUpdatedTime(LocalDateTime.now());

        int result = userMapper.insertUser(user);
        if (result != 1) {
            throw new IllegalStateException("创建用户失败");
        }
        return user;
>>>>>>> 2c7bb808696ce5aba4b1bc1a4e70731964c3986b
    }

    @Override
    public User login(LoginRequest request) {
        if (request.getUsername() == null || request.getPassword() == null) {
            return null;
        }
<<<<<<< HEAD
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
=======
        User user = userMapper.selectByUsername(request.getUsername());
        if (user == null) {
            return null;
        }
        String storedPassword = user.getPasswordHash();
        if (storedPassword == null) {
            return null;
        }

        if (passwordEncoder.matches(request.getPassword(), storedPassword)) {
            return user;
        }

        // 兼容旧数据：如果数据库里仍然保存的是明文密码，则直接比较
        if (!storedPassword.startsWith("$2a$")
                && !storedPassword.startsWith("$2b$")
                && !storedPassword.startsWith("$2y$")) {
>>>>>>> 2c7bb808696ce5aba4b1bc1a4e70731964c3986b
            if (request.getPassword().equals(storedPassword)) {
                return user;
            }
        }
<<<<<<< HEAD
=======

>>>>>>> 2c7bb808696ce5aba4b1bc1a4e70731964c3986b
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
<<<<<<< HEAD
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
=======
    public void resetPassword(RegisterRequest request) {
        if (request.getUsername() == null || request.getPassword() == null) {
            throw new IllegalArgumentException("用户名和新密码不能为空");
        }
        User user = userMapper.selectByUsername(request.getUsername());
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        String newHash = passwordEncoder.encode(request.getPassword());
        userMapper.updatePassword(user.getUserId(), newHash);
        userMapper.updateStatus(user.getUserId(), 1);
>>>>>>> 2c7bb808696ce5aba4b1bc1a4e70731964c3986b
    }

    @Override
    public User findByUsername(String username) {
<<<<<<< HEAD
        return userMapper.findByUsername(username);
=======
        return userMapper.selectByUsername(username);
    }

    @Override
    public User findById(Long userId) {
        return userMapper.selectById(userId);
>>>>>>> 2c7bb808696ce5aba4b1bc1a4e70731964c3986b
    }
}