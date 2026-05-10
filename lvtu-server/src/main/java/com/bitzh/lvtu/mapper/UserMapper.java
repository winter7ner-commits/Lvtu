package com.bitzh.lvtu.mapper;

import com.bitzh.lvtu.entity.User;
<<<<<<< HEAD
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    User findByUsername(@Param("username") String username);
    User findById(@Param("userId") Long userId);
    int insert(User user);
    int update(User user);
}
=======
import org.apache.ibatis.annotations.Param;

public interface UserMapper {

    User selectByUsername(@Param("username") String username);

    User selectByEmail(@Param("email") String email);

    User selectByPhone(@Param("phone") String phone);

    User selectById(@Param("userId") Long userId);

    int insertUser(User user);

    int updatePassword(@Param("userId") Long userId, @Param("passwordHash") String passwordHash);

    int updateStatus(@Param("userId") Long userId, @Param("status") Integer status);
}
>>>>>>> 2c7bb808696ce5aba4b1bc1a4e70731964c3986b
