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

    int updateUser(User user);

    // 以下方法对应 UserMapper.xml 中已实现，但原接口遗漏的 SQL 操作
    
    int updateAuthStatus(@Param("userId") Long userId, @Param("authStatus") Integer authStatus);

    int updateUserType(@Param("userId") Long userId, @Param("userType") Integer userType);

    int updateUserProfile(@Param("userId") Long userId, @Param("phone") String phone, 
                          @Param("email") String email, @Param("region") String region);

    int deleteUser(@Param("userId") Long userId);

    // 查询手机号关联的所有已注销账号
    java.util.List<User> selectDeactivatedUsersByPhone(@Param("phone") String phone);

    // 查询手机号关联的正常状态账号
    User selectActiveUserByPhone(@Param("phone") String phone);

    // 解绑手机号
    int unbindPhone(@Param("userId") Long userId);
}
