package com.bitzh.lvtu.controller;

import com.bitzh.lvtu.common.ApiResponse;
import com.bitzh.lvtu.entity.UserNotification;
import com.bitzh.lvtu.service.NotificationService;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
@RequestMapping("/api/notifications")
public class NotificationController {

    @Resource
    private NotificationService notificationService;

    @GetMapping
    public ApiResponse<List<UserNotification>> list(@RequestParam @NotNull Long userId) {
        return ApiResponse.success(notificationService.listByUserId(userId));
    }

    @GetMapping("/unread-count")
    public ApiResponse<Integer> unreadCount(@RequestParam @NotNull Long userId) {
        return ApiResponse.success(notificationService.countUnread(userId));
    }

    @PutMapping("/{id}/read")
    public ApiResponse<Void> markAsRead(@PathVariable @NotNull Long id,
                                        @RequestParam @NotNull Long userId) {
        notificationService.markAsRead(id, userId);
        return ApiResponse.success(null);
    }

    @PutMapping("/read-all")
    public ApiResponse<Void> markAllAsRead(@RequestParam @NotNull Long userId) {
        notificationService.markAllAsRead(userId);
        return ApiResponse.success(null);
    }
}
