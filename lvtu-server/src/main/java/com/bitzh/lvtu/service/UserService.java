package com.bitzh.lvtu.service;

import com.bitzh.lvtu.dto.LoginRequest;
import com.bitzh.lvtu.dto.RegisterRequest;
import com.bitzh.lvtu.entity.User;

import com.bitzh.lvtu.dto.AdminRegisterRequest;

public interface UserService {

    User register(RegisterRequest request);

    User adminRegister(AdminRegisterRequest request);

    User login(LoginRequest request);

    User adminLogin(LoginRequest request);

    void resetPassword(RegisterRequest request);

    User findByUsername(String username);

    User findById(Long userId);
}
