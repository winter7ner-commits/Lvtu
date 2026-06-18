package com.bitzh.lvtu.controller;

import com.bitzh.lvtu.common.ApiResponse;
import com.bitzh.lvtu.dto.UserDTO;
import com.bitzh.lvtu.dto.UserVerificationDTO;
import com.bitzh.lvtu.entity.User;
import com.bitzh.lvtu.exception.BusinessException;
import com.bitzh.lvtu.mapper.UserMapper;
import com.bitzh.lvtu.service.AdminPermissionService;
import com.bitzh.lvtu.service.UserService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/admin")
public class AdminDataController {

    private static final Set<Integer> USER_STATUSES = Set.of(0, 1, 2, 3, 4);
    private static final Set<String> ADMIN_ROLES = Set.of("SUPER_ADMIN", "CERT_AUDITOR", "OPERATOR", "CUSTOMER_SERVICE");

    private final JdbcTemplate jdbcTemplate;
    private final AdminPermissionService adminPermissionService;
    private final UserService userService;
    private final UserMapper userMapper;

    public AdminDataController(JdbcTemplate jdbcTemplate,
                               AdminPermissionService adminPermissionService,
                               UserService userService,
                               UserMapper userMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.adminPermissionService = adminPermissionService;
        this.userService = userService;
        this.userMapper = userMapper;
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

    @PutMapping("/users/{userId}")
    public ApiResponse<UserDTO> updateUser(@RequestHeader(value = "Authorization", required = false) String authorization,
                                           @PathVariable Long userId,
                                           @RequestBody Map<String, Object> body) {
        try {
            adminPermissionService.requireAdmin(authorization, "SUPER_ADMIN");
            User user = requireUser(userId);
            if (body == null) {
                return ApiResponse.fail(400, "请求参数不能为空");
            }

            if (body.containsKey("phone")) {
                user.setPhone(normalizeString(body.get("phone")));
            }
            if (body.containsKey("email")) {
                user.setEmail(normalizeString(body.get("email")));
            }
            if (body.containsKey("region")) {
                user.setRegion(normalizeString(body.get("region")));
            }
            if (body.containsKey("status")) {
                user.setStatus(parseStatus(body.get("status")));
            }
            if (body.containsKey("adminRole") && Integer.valueOf(3).equals(user.getUserType())) {
                user.setAdminRole(parseAdminRole(body.get("adminRole")));
            }

            userMapper.updateUser(user);
            return ApiResponse.success(UserDTO.from(userMapper.selectById(userId)));
        } catch (BusinessException e) {
            return ApiResponse.fail(e.getCode(), e.getMessage());
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(400, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.fail(500, "更新用户失败: " + e.getMessage());
        }
    }

    @PutMapping("/users/{userId}/status")
    public ApiResponse<UserDTO> updateUserStatus(@RequestHeader(value = "Authorization", required = false) String authorization,
                                                 @PathVariable Long userId,
                                                 @RequestBody Map<String, Object> body) {
        try {
            User admin = adminPermissionService.requireAdmin(authorization, "SUPER_ADMIN");
            if (admin.getUserId().equals(userId)) {
                return ApiResponse.fail(400, "不能修改当前登录管理员的状态");
            }
            Integer status = parseStatus(body == null ? null : body.get("status"));
            requireUser(userId);
            userMapper.updateStatus(userId, status);
            return ApiResponse.success(UserDTO.from(userMapper.selectById(userId)));
        } catch (BusinessException e) {
            return ApiResponse.fail(e.getCode(), e.getMessage());
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(400, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.fail(500, "更新用户状态失败: " + e.getMessage());
        }
    }

    @PutMapping("/users/{userId}/admin-role")
    public ApiResponse<UserDTO> updateUserAdminRole(@RequestHeader(value = "Authorization", required = false) String authorization,
                                                    @PathVariable Long userId,
                                                    @RequestBody Map<String, Object> body) {
        try {
            adminPermissionService.requireAdmin(authorization, "SUPER_ADMIN");
            User user = requireUser(userId);
            if (!Integer.valueOf(3).equals(user.getUserType())) {
                return ApiResponse.fail(400, "只有后台管理员可以设置后台角色");
            }
            user.setAdminRole(parseAdminRole(body == null ? null : body.get("adminRole")));
            userMapper.updateUser(user);
            return ApiResponse.success(UserDTO.from(userMapper.selectById(userId)));
        } catch (BusinessException e) {
            return ApiResponse.fail(e.getCode(), e.getMessage());
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(400, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.fail(500, "更新后台角色失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/users/{userId}")
    public ApiResponse<UserDTO> deleteUser(@RequestHeader(value = "Authorization", required = false) String authorization,
                                           @PathVariable Long userId) {
        try {
            User admin = adminPermissionService.requireAdmin(authorization, "SUPER_ADMIN");
            if (admin.getUserId().equals(userId)) {
                return ApiResponse.fail(400, "不能删除当前登录管理员");
            }
            requireUser(userId);
            userMapper.updateStatus(userId, 4);
            return ApiResponse.success("用户已标记为已注销", UserDTO.from(userMapper.selectById(userId)));
        } catch (BusinessException e) {
            return ApiResponse.fail(e.getCode(), e.getMessage());
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(400, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.fail(500, "删除用户失败: " + e.getMessage());
        }
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

    @GetMapping("/realname-verifications")
    public ApiResponse<List<UserVerificationDTO>> realnameVerifications(@RequestHeader(value = "Authorization", required = false) String authorization) {
        adminPermissionService.requireAdmin(authorization, "SUPER_ADMIN", "CERT_AUDITOR");
        return ApiResponse.success(userService.listVerifications());
    }

    @PostMapping("/realname-verifications/{verificationId}/approve")
    public ApiResponse<String> approveRealnameVerification(@RequestHeader(value = "Authorization", required = false) String authorization,
                                                          @PathVariable Long verificationId) {
        User admin = adminPermissionService.requireAdmin(authorization, "SUPER_ADMIN", "CERT_AUDITOR");
        userService.approveVerification(verificationId, admin.getUserId());
        return ApiResponse.success("实名认证审核通过", null);
    }

    @PostMapping("/realname-verifications/{verificationId}/reject")
    public ApiResponse<String> rejectRealnameVerification(@RequestHeader(value = "Authorization", required = false) String authorization,
                                                         @PathVariable Long verificationId,
                                                         @RequestBody Map<String, String> body) {
        User admin = adminPermissionService.requireAdmin(authorization, "SUPER_ADMIN", "CERT_AUDITOR");
        String reason = body == null ? null : body.get("rejectReason");
        userService.rejectVerification(verificationId, admin.getUserId(), reason);
        return ApiResponse.success("实名认证已驳回", null);
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

    private User requireUser(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        return user;
    }

    private String normalizeString(Object value) {
        if (value == null) {
            return null;
        }
        String text = String.valueOf(value).trim();
        return text.isEmpty() ? null : text;
    }

    private Integer parseStatus(Object value) {
        if (value == null) {
            throw new IllegalArgumentException("用户状态不能为空");
        }
        Integer status;
        if (value instanceof Number number) {
            status = number.intValue();
        } else {
            status = Integer.valueOf(String.valueOf(value));
        }
        if (!USER_STATUSES.contains(status)) {
            throw new IllegalArgumentException("用户状态无效");
        }
        return status;
    }

    private String parseAdminRole(Object value) {
        String role = normalizeString(value);
        if (role == null) {
            throw new IllegalArgumentException("后台角色不能为空");
        }
        role = role.toUpperCase();
        if (!ADMIN_ROLES.contains(role)) {
            throw new IllegalArgumentException("后台角色无效");
        }
        return role;
    }
}
