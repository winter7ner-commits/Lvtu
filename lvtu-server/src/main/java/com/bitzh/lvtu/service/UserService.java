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

    void changePassword(String username, String oldPassword, String newPassword);

    User updateUserProfile(Long userId, String username, String phone, String email, String region);

    void updateUser(User user);

    void deactivateUser(Long userId);

    User restoreUser(Long userId);

    // 根据手机号查询已注销账号列表
    java.util.List<User> findDeactivatedUsersByPhone(String phone);

    // 根据手机号查询活跃账号
    User findActiveUserByPhone(String phone);

    // 自助恢复已注销账号（处理手机号冲突）
    User selfRestoreAccount(Long userId, boolean confirmPhoneTransfer);
}