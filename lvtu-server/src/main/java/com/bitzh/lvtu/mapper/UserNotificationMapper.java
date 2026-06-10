package com.bitzh.lvtu.mapper;

import com.bitzh.lvtu.entity.UserNotification;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserNotificationMapper {

    int insert(UserNotification notification);

    List<UserNotification> selectByUserId(@Param("userId") Long userId);

    int countUnreadByUserId(@Param("userId") Long userId);

    int markAsRead(@Param("id") Long id, @Param("userId") Long userId);

    int markAllAsRead(@Param("userId") Long userId);
}
