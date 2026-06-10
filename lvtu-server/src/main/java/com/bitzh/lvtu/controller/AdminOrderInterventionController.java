package com.bitzh.lvtu.controller;

import com.bitzh.lvtu.common.ApiResponse;
import com.bitzh.lvtu.dto.request.InterventionActionRequest;
import com.bitzh.lvtu.dto.request.RevisionLimitConfigRequest;
import com.bitzh.lvtu.exception.BusinessException;
import com.bitzh.lvtu.entity.User;
import com.bitzh.lvtu.service.AdminPermissionService;
import com.bitzh.lvtu.service.NotificationService;
import com.bitzh.lvtu.service.SystemConfigService;
import jakarta.validation.Valid;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminOrderInterventionController {

    private final JdbcTemplate jdbcTemplate;
    private final SystemConfigService systemConfigService;
    private final NotificationService notificationService;
    private final AdminPermissionService adminPermissionService;

    public AdminOrderInterventionController(JdbcTemplate jdbcTemplate,
                                            SystemConfigService systemConfigService,
                                            NotificationService notificationService,
                                            AdminPermissionService adminPermissionService) {
        this.jdbcTemplate = jdbcTemplate;
        this.systemConfigService = systemConfigService;
        this.notificationService = notificationService;
        this.adminPermissionService = adminPermissionService;
    }

    @GetMapping("/settings/revision-limit")
    public ApiResponse<Map<String, Integer>> getRevisionLimit(@RequestHeader(value = "Authorization", required = false) String authorization) {
        adminPermissionService.requireAdmin(authorization, "SUPER_ADMIN", "CUSTOMER_SERVICE");
        Map<String, Integer> data = new HashMap<>();
        data.put("maxRevisionRequestCount", systemConfigService.getMaxRevisionRequestCount());
        return ApiResponse.success(data);
    }

    @PutMapping("/settings/revision-limit")
    public ApiResponse<Map<String, Integer>> updateRevisionLimit(@RequestHeader(value = "Authorization", required = false) String authorization,
                                                                 @Valid @RequestBody RevisionLimitConfigRequest request) {
        adminPermissionService.requireAdmin(authorization, "SUPER_ADMIN");
        systemConfigService.updateMaxRevisionRequestCount(request.getMaxRevisionRequestCount());
        Map<String, Integer> data = new HashMap<>();
        data.put("maxRevisionRequestCount", systemConfigService.getMaxRevisionRequestCount());
        return ApiResponse.success(data);
    }

    @GetMapping("/interventions")
    public ApiResponse<List<Map<String, Object>>> listInterventions(@RequestHeader(value = "Authorization", required = false) String authorization) {
        adminPermissionService.requireAdmin(authorization, "SUPER_ADMIN", "CUSTOMER_SERVICE");
        String sql = """
                SELECT o.order_id, o.user_id, u.username AS user_name, u.phone AS user_phone,
                       o.lawyer_id, lu.username AS lawyer_name, l.law_firm,
                       o.service_type_id, st.name AS service_name,
                       o.total_amount, o.status AS order_status, o.created_time, o.updated_time,
                       sr.id AS result_id, sr.title AS result_title, sr.content AS result_content,
                       sr.status AS result_status, sr.updated_at AS result_updated_at,
                       latest.revision_no AS latest_revision_no,
                       latest.content AS latest_revision_content,
                       latest.created_at AS latest_revision_created_at,
                       latest.status AS latest_revision_status,
                       (SELECT COUNT(*) FROM service_result_revision r WHERE r.order_id = o.order_id AND r.status IN (0, 1)) AS revision_count
                FROM `order` o
                LEFT JOIN users u ON o.user_id = u.user_id
                LEFT JOIN lawyer l ON o.lawyer_id = l.lawyer_id
                LEFT JOIN users lu ON l.user_id = lu.user_id
                LEFT JOIN service_type st ON o.service_type_id = st.service_type_id
                LEFT JOIN service_result sr ON sr.order_id = o.order_id
                LEFT JOIN service_result_revision latest
                    ON latest.id = (
                        SELECT r2.id
                        FROM service_result_revision r2
                        WHERE r2.order_id = o.order_id
                        ORDER BY r2.created_at DESC, r2.id DESC
                        LIMIT 1
                    )
                WHERE o.status = '平台介入'
                ORDER BY o.updated_time DESC, o.order_id DESC
                """;
        return ApiResponse.success(jdbcTemplate.queryForList(sql));
    }

    @GetMapping("/interventions/{orderId}/logs")
    public ApiResponse<List<Map<String, Object>>> listInterventionLogs(@RequestHeader(value = "Authorization", required = false) String authorization,
                                                                       @PathVariable Long orderId) {
        adminPermissionService.requireAdmin(authorization, "SUPER_ADMIN", "CUSTOMER_SERVICE");
        String sql = """
                SELECT l.id, l.order_id, l.admin_id, u.username AS admin_name,
                       l.action_type, l.reason, l.from_status, l.to_status, l.created_at
                FROM order_intervention_action l
                LEFT JOIN users u ON u.user_id = l.admin_id
                WHERE l.order_id = ?
                ORDER BY l.created_at DESC, l.id DESC
                """;
        return ApiResponse.success(jdbcTemplate.queryForList(sql, orderId));
    }

    @PostMapping("/interventions/{orderId}/actions")
    public ApiResponse<Void> handleIntervention(@RequestHeader(value = "Authorization", required = false) String authorization,
                                                @PathVariable Long orderId,
                                                @Valid @RequestBody InterventionActionRequest request) {
        User admin = adminPermissionService.requireAdmin(authorization, "SUPER_ADMIN", "CUSTOMER_SERVICE");
        Map<String, Object> order = getInterventionOrder(orderId);
        String actionType = request.getActionType();
        String toStatus;
        Integer resultStatus;

        switch (actionType) {
            case "REQUIRE_LAWYER_RESUBMIT" -> {
                toStatus = "处理中";
                resultStatus = 2;
            }
            case "RETURN_TO_CONFIRMATION" -> {
                toStatus = "待客户确认";
                resultStatus = 0;
            }
            case "MARK_PENDING_REVIEW" -> {
                toStatus = "待评价";
                resultStatus = 1;
            }
            default -> throw new BusinessException("不支持的处理动作");
        }

        int updated = jdbcTemplate.update(
                "UPDATE `order` SET status = ?, updated_time = CURRENT_TIMESTAMP WHERE order_id = ? AND status = '平台介入'",
                toStatus,
                orderId
        );
        if (updated == 0) {
            throw new BusinessException("订单状态已变化，请刷新后重试");
        }
        jdbcTemplate.update(
                "UPDATE service_result SET status = ?, updated_at = CURRENT_TIMESTAMP WHERE order_id = ?",
                resultStatus,
                orderId
        );
        jdbcTemplate.update("""
                INSERT INTO order_intervention_action
                    (order_id, admin_id, action_type, reason, from_status, to_status)
                VALUES (?, ?, ?, ?, '平台介入', ?)
                """, orderId, admin.getUserId(), actionType, request.getReason().trim(), toStatus);

        notifyInterventionHandled(order, orderId, actionType, toStatus, request.getReason().trim());
        return ApiResponse.success("平台介入处理完成", null);
    }

    private Map<String, Object> getInterventionOrder(Long orderId) {
        try {
            return jdbcTemplate.queryForMap("""
                    SELECT o.order_id, o.user_id, o.lawyer_id, l.user_id AS lawyer_user_id
                    FROM `order` o
                    LEFT JOIN lawyer l ON l.lawyer_id = o.lawyer_id
                    WHERE o.order_id = ? AND o.status = '平台介入'
                    """, orderId);
        } catch (Exception ex) {
            throw new BusinessException("平台介入订单不存在");
        }
    }

    private void notifyInterventionHandled(Map<String, Object> order,
                                           Long orderId,
                                           String actionType,
                                           String toStatus,
                                           String reason) {
        Long userId = ((Number) order.get("user_id")).longValue();
        notificationService.create(
                userId,
                "ORDER_INTERVENTION_HANDLED",
                "平台介入已有处理结果",
                "订单 #" + orderId + " 已由平台处理，当前状态：" + toStatus + "。处理说明：" + reason,
                orderId,
                "/orders/" + orderId
        );

        Object lawyerUserId = order.get("lawyer_user_id");
        if (lawyerUserId != null) {
            notificationService.create(
                    ((Number) lawyerUserId).longValue(),
                    "ORDER_INTERVENTION_HANDLED",
                    "平台介入已有处理结果",
                    "订单 #" + orderId + " 平台处理结果：" + actionType + "，当前状态：" + toStatus + "。",
                    orderId,
                    "/lawyer/orders/" + orderId
            );
        }
    }
}
