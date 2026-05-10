package com.bitzh.lvtu.service;

import com.bitzh.lvtu.dto.LoginRequest;
import com.bitzh.lvtu.dto.RegisterRequest;
import com.bitzh.lvtu.entity.User;

<<<<<<< HEAD
public interface UserService {
    User login(LoginRequest request);
    User adminLogin(LoginRequest request);
    User register(RegisterRequest request);
    User findById(Long userId);
    User findByUsername(String username);
}
=======
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
>>>>>>> 2c7bb808696ce5aba4b1bc1a4e70731964c3986b
