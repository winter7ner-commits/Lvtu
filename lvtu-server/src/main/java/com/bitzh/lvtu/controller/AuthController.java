package com.bitzh.lvtu.controller;

import com.bitzh.lvtu.common.ApiResponse;
import com.bitzh.lvtu.dto.LoginRequest;
import com.bitzh.lvtu.dto.RegisterRequest;
import com.bitzh.lvtu.dto.UserDTO;
import com.bitzh.lvtu.entity.User;
import com.bitzh.lvtu.exception.BusinessException;
import com.bitzh.lvtu.service.AdminPermissionService;
import com.bitzh.lvtu.service.UserService;
import com.bitzh.lvtu.util.JwtUtil;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import com.bitzh.lvtu.dto.AdminRegisterRequest;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Resource
    private UserService userService;

    @Resource
    private AdminPermissionService adminPermissionService;

    @PostMapping("/register")
    public ApiResponse<UserDTO> register(@RequestBody RegisterRequest request) {
        try {
            User user = userService.register(request);
            return ApiResponse.success(UserDTO.from(user));
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(400, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.fail(500, "注册失败");
        }
    }

    @PostMapping("/admin/register")
    public ApiResponse<UserDTO> adminRegister(@RequestBody AdminRegisterRequest request, @RequestHeader(value = "Authorization", required = false) String authorization) {
        try {
            adminPermissionService.requireAdmin(authorization, "SUPER_ADMIN");

            User user = userService.adminRegister(request);
            return ApiResponse.success(UserDTO.from(user));
        } catch (BusinessException e) {
            return ApiResponse.fail(e.getCode(), e.getMessage());
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(400, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.fail(500, "管理员注册失败");
        }
    }

    @PostMapping("/login")
    public ApiResponse<Map<String, Object>> login(@RequestBody LoginRequest request) {
        try {
            User user = userService.login(request);
            if (user == null) {
                return ApiResponse.fail(401, "用户名或密码错误");
            }
            String token = JwtUtil.generateToken(user.getUserId(), user.getUsername());
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("user", UserDTO.from(user));
            return ApiResponse.success(data);
        } catch (Exception e) {
            return ApiResponse.fail(500, "登录失败");
        }
    }

    @PostMapping("/admin/login")
    public ApiResponse<Map<String, Object>> adminLogin(@RequestBody LoginRequest request) {
        try {
            User user = userService.adminLogin(request);
            if (user == null) {
                return ApiResponse.fail(401, "管理员用户名或密码错误，或非管理员账户");
            }
            String token = JwtUtil.generateToken(user.getUserId(), user.getUsername());
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("user", UserDTO.from(user));
            return ApiResponse.success(data);
        } catch (Exception e) {
            return ApiResponse.fail(500, "管理员登录失败");
        }
    }

    @PostMapping("/logout")
    public ApiResponse<String> logout() {
        return ApiResponse.success("退出成功", null);
    }

    @PostMapping("/change-password")
    public ApiResponse<String> changePassword(@RequestBody Map<String, String> params) {
        try {
            String username = params.get("username");
            String oldPassword = params.get("oldPassword");
            String newPassword = params.get("newPassword");
            userService.changePassword(username, oldPassword, newPassword);
            return ApiResponse.success("密码修改成功", null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(400, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.fail(500, "修改失败");
        }
    }



    @PostMapping("/forgot-password")
    public ApiResponse<String> forgotPassword(@RequestBody RegisterRequest request) {
        try {
            userService.resetPassword(request);
            return ApiResponse.success("密码已重置", null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(400, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.fail(500, "重置失败");
        }
    }

    @GetMapping("/me")
    public ApiResponse<UserDTO> me(@RequestHeader(value = "Authorization", required = false) String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return ApiResponse.fail(401, "未登录");
        }
        String token = authorization.substring(7);
        Long userId = JwtUtil.getUserId(token);
        if (userId == null) {
            return ApiResponse.fail(401, "无效 token");
        }
        User user = userService.findById(userId);
        if (user == null) {
            return ApiResponse.fail(401, "用户不存在");
        }
        return ApiResponse.success(UserDTO.from(user));
    }

    /**
     * 根据手机号查询已注销账号列表（第一步：身份验证后展示可恢复的账号）
     */
    @PostMapping("/restore/query")
    public ApiResponse<java.util.List<java.util.Map<String, Object>>> queryDeactivatedAccounts(@RequestBody Map<String, String> body) {
        try {
            String phone = body.get("phone");
            if (phone == null || phone.trim().isEmpty()) {
                return ApiResponse.fail(400, "手机号不能为空");
            }

            java.util.List<User> deactivatedUsers = userService.findDeactivatedUsersByPhone(phone);
            if (deactivatedUsers == null || deactivatedUsers.isEmpty()) {
                return ApiResponse.fail(404, "该手机号未关联已注销账号");
            }

            // 只返回必要信息：userId、username、createdTime
            java.util.List<java.util.Map<String, Object>> result = new java.util.ArrayList<>();
            for (User user : deactivatedUsers) {
                java.util.Map<String, Object> info = new java.util.HashMap<>();
                info.put("userId", user.getUserId());
                info.put("username", user.getUsername());
                info.put("createdTime", user.getCreatedTime());
                result.add(info);
            }
            return ApiResponse.success(result);
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(400, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.fail(500, "查询失败");
        }
    }

    /**
     * 检查手机号冲突情况（第二步：选择账号后检查冲突）
     */
    @PostMapping("/restore/check-conflict")
    public ApiResponse<java.util.Map<String, Object>> checkPhoneConflict(@RequestBody Map<String, String> body) {
        try {
            String phone = body.get("phone");
            if (phone == null || phone.trim().isEmpty()) {
                return ApiResponse.fail(400, "手机号不能为空");
            }

            User activeUser = userService.findActiveUserByPhone(phone);
            java.util.Map<String, Object> result = new java.util.HashMap<>();
            result.put("hasConflict", activeUser != null);
            if (activeUser != null) {
                result.put("conflictUsername", activeUser.getUsername());
                result.put("conflictUserId", activeUser.getUserId());
            }
            return ApiResponse.success(result);
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(400, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.fail(500, "检查失败");
        }
    }

    /**
     * 执行自助恢复账号（第三步：确认恢复）
     */
    @PostMapping("/restore/execute")
    public ApiResponse<java.util.Map<String, Object>> executeRestore(@RequestBody Map<String, Object> body) {
        try {
            Object userIdObj = body.get("userId");
            if (userIdObj == null) {
                return ApiResponse.fail(400, "用户ID不能为空");
            }
            Long userId;
            if (userIdObj instanceof Integer) {
                userId = ((Integer) userIdObj).longValue();
            } else if (userIdObj instanceof Long) {
                userId = (Long) userIdObj;
            } else {
                userId = Long.parseLong(userIdObj.toString());
            }

            Boolean confirmPhoneTransfer = (Boolean) body.get("confirmPhoneTransfer");
            if (confirmPhoneTransfer == null) {
                confirmPhoneTransfer = false;
            }

            User restoredUser = userService.selfRestoreAccount(userId, confirmPhoneTransfer);

            // 生成新token，让用户直接登录
            String token = JwtUtil.generateToken(restoredUser.getUserId(), restoredUser.getUsername());

            java.util.Map<String, Object> data = new java.util.HashMap<>();
            data.put("token", token);
            data.put("user", UserDTO.from(restoredUser));
            return ApiResponse.success("账号恢复成功", data);
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(400, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.fail(500, "账号恢复失败");
        }
    }
}
