package com.bitzh.lvtu.mapper;

import com.bitzh.lvtu.entity.User;
import com.bitzh.lvtu.dto.UserVerificationDTO;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {

    User selectByUsername(@Param("username") String username);

    User selectByEmail(@Param("email") String email);

    User selectByPhone(@Param("phone") String phone);

    User selectById(@Param("userId") Long userId);

    UserVerificationDTO selectVerificationByUserId(@Param("userId") Long userId);

    int insertUser(User user);

    int updatePassword(@Param("userId") Long userId, @Param("passwordHash") String passwordHash);

    int updateStatus(@Param("userId") Long userId, @Param("status") Integer status);

    int updateAuthStatus(@Param("userId") Long userId, @Param("authStatus") Integer authStatus);

    int updateUserType(@Param("userId") Long userId, @Param("userType") Integer userType);
}
