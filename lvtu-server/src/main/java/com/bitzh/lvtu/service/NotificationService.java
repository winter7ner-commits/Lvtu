package com.bitzh.lvtu.service;

import com.bitzh.lvtu.entity.UserNotification;

import java.util.List;

public interface NotificationService {

    void create(Long userId, String type, String title, String content, Long relatedOrderId, String relatedPath);

    List<UserNotification> listByUserId(Long userId);

    int countUnread(Long userId);

    void markAsRead(Long id, Long userId);

    void markAllAsRead(Long userId);
}
