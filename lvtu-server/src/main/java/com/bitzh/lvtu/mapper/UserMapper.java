package com.bitzh.lvtu.mapper;

import com.bitzh.lvtu.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    User findByUsername(@Param("username") String username);
    User findById(@Param("userId") Long userId);
    int insert(User user);
    int update(User user);
}