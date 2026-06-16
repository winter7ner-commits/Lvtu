package com.bitzh.lvtu.service;

import com.bitzh.lvtu.dto.LoginRequest;
import com.bitzh.lvtu.dto.RegisterRequest;
import com.bitzh.lvtu.dto.UserVerificationDTO;
import com.bitzh.lvtu.entity.User;

import com.bitzh.lvtu.dto.AdminRegisterRequest;

import java.util.List;

public interface UserService {

    User register(RegisterRequest request);

    User adminRegister(AdminRegisterRequest request);

    User login(LoginRequest request);

    User adminLogin(LoginRequest request);

    void resetPassword(RegisterRequest request);

    User findByUsername(String username);

    User findById(Long userId);

    void changePassword(String username, String oldPassword, String newPassword);

    User updateUserProfile(Long userId, String phone, String email, String region);

    void updateUser(User user);

    void deactivateUser(Long userId);

    void cancelDeactivation(Long userId);

    UserVerificationDTO getVerification(Long userId);

    void submitVerification(Long userId, String realName, String idCardNumber,
                            String phone, String idCardFrontUrl, String idCardBackUrl);

    List<UserVerificationDTO> listVerifications();

    void approveVerification(Long verificationId, Long reviewerId);

    void rejectVerification(Long verificationId, Long reviewerId, String rejectReason);
}
