package com.bitzh.lvtu.controller;

import com.bitzh.lvtu.common.ApiResponse;
import com.bitzh.lvtu.service.AdminPermissionService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminDataController {

    private final JdbcTemplate jdbcTemplate;
    private final AdminPermissionService adminPermissionService;

    public AdminDataController(JdbcTemplate jdbcTemplate, AdminPermissionService adminPermissionService) {
        this.jdbcTemplate = jdbcTemplate;
        this.adminPermissionService = adminPermissionService;
    }

    @GetMapping("/users")
    public ApiResponse<List<Map<String, Object>>> users(@RequestHeader(value = "Authorization", required = false) String authorization) {
        adminPermissionService.requireAdmin(authorization, "SUPER_ADMIN", "OPERATOR");
        String sql = """
                SELECT user_id, username, phone, email, user_type, admin_role, status, is_verified,
                       auth_status, region, created_time, updated_time
                FROM users
                ORDER BY created_time DESC, user_id DESC
                """;
        return ApiResponse.success(jdbcTemplate.queryForList(sql));
    }

    @GetMapping("/auth-applications")
    public ApiResponse<List<Map<String, Object>>> authApplications(@RequestHeader(value = "Authorization", required = false) String authorization) {
        adminPermissionService.requireAdmin(authorization, "SUPER_ADMIN", "CERT_AUDITOR");
        String sql = """
                SELECT a.application_id, a.user_id, u.username, u.phone, u.email,
                       a.license_no, a.license_url, a.status, a.audit_time,
                       a.reject_reason, a.create_time, a.apply_type,
                       a.law_firm, a.practice_start_year
                FROM application a
                LEFT JOIN users u ON a.user_id = u.user_id
                ORDER BY a.create_time DESC, a.application_id DESC
                """;
        return ApiResponse.success(jdbcTemplate.queryForList(sql));
    }

    @GetMapping("/orders")
    public ApiResponse<List<Map<String, Object>>> orders(@RequestHeader(value = "Authorization", required = false) String authorization) {
        adminPermissionService.requireAdmin(authorization, "SUPER_ADMIN", "OPERATOR", "CUSTOMER_SERVICE");
        String sql = """
                SELECT o.order_id, o.user_id, u.username AS user_name, u.phone AS user_phone,
                       o.lawyer_id, lu.username AS lawyer_name, l.law_firm,
                       o.service_type_id, st.name AS service_name, st.description AS service_description,
                       o.total_amount, o.status AS order_status, o.created_time, o.updated_time,
                       p.payment_id, p.amount AS payment_amount, p.status AS payment_status,
                       p.create_time AS payment_create_time, p.payment_time,
                       CAST(osd.form_data AS CHAR) AS form_data,
                       e.evaluation_id, e.professional_score, e.responsiveness_score,
                       e.attitude_score, e.total_score, e.content AS evaluation_content,
                       e.status AS evaluation_status, e.report_count, e.is_featured,
                       e.created_time AS evaluation_created_time
                FROM `order` o
                LEFT JOIN users u ON o.user_id = u.user_id
                LEFT JOIN lawyer l ON o.lawyer_id = l.lawyer_id
                LEFT JOIN users lu ON l.user_id = lu.user_id
                LEFT JOIN service_type st ON o.service_type_id = st.service_type_id
                LEFT JOIN payment p ON o.order_id = p.order_id
                LEFT JOIN order_service_detail osd ON o.order_id = osd.order_id
                LEFT JOIN evaluation e ON o.order_id = e.order_id
                ORDER BY o.created_time DESC, o.order_id DESC
                """;
        return ApiResponse.success(jdbcTemplate.queryForList(sql));
    }
}
