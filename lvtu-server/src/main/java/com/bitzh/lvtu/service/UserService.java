package com.bitzh.lvtu.service;

import com.bitzh.lvtu.dto.LoginRequest;
import com.bitzh.lvtu.dto.RegisterRequest;
import com.bitzh.lvtu.entity.User;

public interface UserService {
    User login(LoginRequest request);
    User adminLogin(LoginRequest request);
    User register(RegisterRequest request);
    User findById(Long userId);
    User findByUsername(String username);
}