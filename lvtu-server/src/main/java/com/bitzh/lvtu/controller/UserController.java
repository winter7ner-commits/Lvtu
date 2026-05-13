package com.bitzh.lvtu.controller;

import com.bitzh.lvtu.common.ApiResponse;
import com.bitzh.lvtu.dto.UserDTO;
import com.bitzh.lvtu.entity.User;
import com.bitzh.lvtu.service.UserService;
import com.bitzh.lvtu.util.JwtUtil;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Resource
    private UserService userService;

    // 从配置文件读取头像存储路径
    @Value("${web.upload-path:uploads/avatar/}")
    private String uploadPath;

    /**
     * 更新用户基本资料（手机号、邮箱、地区）
     */
    @PutMapping("/profile")
    public ApiResponse<UserDTO> updateProfile(
            @RequestHeader("Authorization") String authorization,
            @RequestBody Map<String, String> body) {
        try {
            if (authorization == null || !authorization.startsWith("Bearer ")) {
                return ApiResponse.fail(401, "未登录");
            }
            String token = authorization.substring(7);
            Long userId = JwtUtil.getUserId(token);
            if (userId == null) {
                return ApiResponse.fail(401, "无效token");
            }

            String phone = body.get("phone");
            String email = body.get("email");
            String region = body.get("region");

            User user = userService.updateUserProfile(userId, phone, email, region);
            return ApiResponse.success(UserDTO.from(user));
        } catch (IllegalArgumentException e) {
            System.err.println("更新用户信息参数错误: " + e.getMessage());
            return ApiResponse.fail(400, e.getMessage());
        } catch (Exception e) {
            System.err.println("更新用户信息失败: " + e.getMessage());
            e.printStackTrace();
            return ApiResponse.fail(500, "更新用户信息失败: " + e.getMessage());
        }
    }

    /**
     * 上传/更新用户头像
     */
    @PostMapping("/avatar")
    public ApiResponse<String> uploadAvatar(
            @RequestHeader("Authorization") String authorization,
            @RequestParam("avatar") MultipartFile file) {
        
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return ApiResponse.fail(401, "未登录");
        }
        
        String token = authorization.substring(7);
        Long userId = JwtUtil.getUserId(token);
        if (userId == null) {
            return ApiResponse.fail(401, "无效token");
        }

        if (file.isEmpty()) {
            return ApiResponse.fail(400, "上传文件不能为空");
        }

        try {
            // 1. 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String ext = originalFilename != null && originalFilename.contains(".") 
                    ? originalFilename.substring(originalFilename.lastIndexOf(".")) 
                    : ".png";
            String newFileName = UUID.randomUUID().toString().replace("-", "") + ext;

            // 2. 确保目录存在 (关键步骤)
            File uploadDir = new File("uploads/avatar"); // 对应 application.yml 中的路径
            if (!uploadDir.exists()) {
                boolean created = uploadDir.mkdirs();
                if (!created) {
                    System.err.println("无法创建上传目录: " + uploadDir.getAbsolutePath());
                }
            }

            // 3. 保存文件到服务器本地
            File destFile = new File(uploadDir, newFileName);
            file.transferTo(destFile);

            // 4. 生成可访问的 URL
            String avatarUrl = "/uploads/avatar/" + newFileName;

            // 5. 更新数据库中的 avatarUrl
            User user = userService.findById(userId);
            if (user != null) {
                user.setAvatarUrl(avatarUrl);
                // 注意：这里需要调用一个只更新头像的方法，或者复用 updateUserProfile
                // 如果复用 updateUserProfile，确保它不会把其他字段清空
                userService.updateUserProfile(userId, user.getPhone(), user.getEmail(), user.getRegion());
                // 更好的做法是增加一个专门更新头像的方法，例如: userService.updateAvatar(userId, avatarUrl);
            }

            // 6. 返回新的头像 URL 给前端
            return ApiResponse.success(avatarUrl);

        } catch (IOException e) {
            e.printStackTrace(); // 打印详细错误日志
            return ApiResponse.fail(500, "文件上传失败: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.fail(500, "头像更新失败: " + e.getMessage());
        }
    }

    /**
     * 注销用户账号
     */
    @DeleteMapping("/deactivate")
    public ApiResponse<String> deactivateUser(@RequestHeader("Authorization") String authorization) {
        try {
            if (authorization == null || !authorization.startsWith("Bearer ")) {
                return ApiResponse.fail(401, "未登录");
            }
            String token = authorization.substring(7);
            Long userId = JwtUtil.getUserId(token);
            if (userId == null) {
                return ApiResponse.fail(401, "无效token");
            }

            // 调用 Service 层逻辑删除用户
            userService.deactivateUser(userId);
            return ApiResponse.success("账号已注销", null);
        } catch (Exception e) {
            return ApiResponse.fail(500, "账号注销失败");
        }
    }
}
