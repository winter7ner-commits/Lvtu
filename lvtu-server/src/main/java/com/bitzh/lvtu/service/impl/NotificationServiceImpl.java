package com.bitzh.lvtu.service.impl;

import com.bitzh.lvtu.entity.UserNotification;
import com.bitzh.lvtu.exception.BusinessException;
import com.bitzh.lvtu.mapper.UserNotificationMapper;
import com.bitzh.lvtu.service.NotificationService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Resource
    private UserNotificationMapper userNotificationMapper;

    @Override
    public void create(Long userId, String type, String title, String content, Long relatedOrderId, String relatedPath) {
        if (userId == null) {
            return;
        }
        UserNotification notification = new UserNotification();
        notification.setUserId(userId);
        notification.setType(type);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setRelatedOrderId(relatedOrderId);
        notification.setRelatedPath(relatedPath);
        userNotificationMapper.insert(notification);
    }

    @Override
    public List<UserNotification> listByUserId(Long userId) {
        validateUserId(userId);
        return userNotificationMapper.selectByUserId(userId);
    }

    @Override
    public int countUnread(Long userId) {
        validateUserId(userId);
        return userNotificationMapper.countUnreadByUserId(userId);
    }

    @Override
    public void markAsRead(Long id, Long userId) {
        validateUserId(userId);
        if (id == null) {
            throw new BusinessException("消息ID不能为空");
        }
        userNotificationMapper.markAsRead(id, userId);
    }

    @Override
    public void markAllAsRead(Long userId) {
        validateUserId(userId);
        userNotificationMapper.markAllAsRead(userId);
    }

    private void validateUserId(Long userId) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }
    }
}
