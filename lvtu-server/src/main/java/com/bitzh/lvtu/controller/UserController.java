package com.bitzh.lvtu.controller;

import com.bitzh.lvtu.common.ApiResponse;
import com.bitzh.lvtu.dto.UserDTO;
import com.bitzh.lvtu.dto.UserVerificationDTO;
import com.bitzh.lvtu.entity.User;
import com.bitzh.lvtu.exception.BusinessException;
import com.bitzh.lvtu.mapper.UserMapper;
import com.bitzh.lvtu.service.AdminPermissionService;
import com.bitzh.lvtu.service.UserService;
import com.bitzh.lvtu.util.JwtUtil;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private UserMapper userMapper;

    @Resource
    private AdminPermissionService adminPermissionService;

    @Value("${web.upload-path:uploads/avatar/}")
    private String uploadPath;

    @Value("${file.upload-dir:lvtu_uploads}")
    private String fileUploadDir;

    /**
     * 获取用户信息
     */
    @GetMapping("/info")
    public ApiResponse<UserDTO> info(@RequestParam Long userId) {
        User user = userService.findById(userId);
        return ApiResponse.success(UserDTO.from(user));
    }

    /**
     * 获取用户认证信息
     */
    @GetMapping("/verification")
    public ApiResponse<UserVerificationDTO> verification(@RequestParam Long userId) {
        return ApiResponse.success(userService.getVerification(userId));
    }

    @GetMapping("/verification/{userId}")
    public ApiResponse<UserVerificationDTO> verificationByPath(@PathVariable Long userId) {
        return ApiResponse.success(userService.getVerification(userId));
    }

    @PostMapping("/verification/upload")
    public ApiResponse<String> uploadVerificationImage(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return ApiResponse.fail(400, "请选择要上传的图片");
        }
        String originalName = file.getOriginalFilename();
        if (originalName == null ||
                !(originalName.toLowerCase().endsWith(".jpg")
                        || originalName.toLowerCase().endsWith(".jpeg")
                        || originalName.toLowerCase().endsWith(".png"))) {
            return ApiResponse.fail(400, "只支持 JPG、JPEG、PNG 格式");
        }
        if (file.getSize() > 10 * 1024 * 1024) {
            return ApiResponse.fail(400, "图片大小不能超过 10MB");
        }
        try {
            String ext = originalName.substring(originalName.lastIndexOf("."));
            String fileName = UUID.randomUUID().toString().replace("-", "") + ext;
            Path folder = resolveUploadFolder().resolve("idcards");
            Files.createDirectories(folder);
            file.transferTo(folder.resolve(fileName).toFile());
            return ApiResponse.success("/uploads/idcards/" + fileName);
        } catch (IOException e) {
            return ApiResponse.fail(500, "文件上传失败");
        }
    }

    @GetMapping("/verification/image/{filename}")
    public ResponseEntity<byte[]> getVerificationImage(@PathVariable String filename) {
        File imageFile = resolveUploadFolder().resolve("idcards").resolve(filename).toFile();
        if (!imageFile.exists()) {
            return ResponseEntity.notFound().build();
        }
        try {
            String lower = filename.toLowerCase();
            String contentType = lower.endsWith(".png") ? "image/png" : "image/jpeg";
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(Files.readAllBytes(imageFile.toPath()));
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/verification/submit")
    public ApiResponse<String> submitVerification(
            @RequestHeader("Authorization") String authorization,
            @RequestBody Map<String, String> body) {
        try {
            Long userId = requireUserId(authorization);
            userService.submitVerification(
                    userId,
                    body.get("realName"),
                    body.get("idCardNumber"),
                    body.get("phone"),
                    body.get("idCardFrontUrl"),
                    body.get("idCardBackUrl")
            );
            return ApiResponse.success("实名认证申请已提交", null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(400, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.fail(500, "实名认证提交失败");
        }
    }

    @GetMapping("/admin/verifications")
    public ApiResponse<List<UserVerificationDTO>> listVerifications(@RequestHeader(value = "Authorization", required = false) String authorization) {
        adminPermissionService.requireAdmin(authorization, "SUPER_ADMIN", "CERT_AUDITOR");
        return ApiResponse.success(userService.listVerifications());
    }

    @PostMapping("/admin/verifications/{verificationId}/approve")
    public ApiResponse<String> approveVerification(@RequestHeader(value = "Authorization", required = false) String authorization,
                                                  @PathVariable Long verificationId) {
        try {
            User admin = adminPermissionService.requireAdmin(authorization, "SUPER_ADMIN", "CERT_AUDITOR");
            userService.approveVerification(verificationId, admin.getUserId());
            return ApiResponse.success("实名认证审核通过", null);
        } catch (BusinessException e) {
            return ApiResponse.fail(e.getCode(), e.getMessage());
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(400, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.fail(500, "实名认证审核失败");
        }
    }

    @PostMapping("/admin/verifications/{verificationId}/reject")
    public ApiResponse<String> rejectVerification(@RequestHeader(value = "Authorization", required = false) String authorization,
                                                 @PathVariable Long verificationId,
                                                 @RequestBody Map<String, String> body) {
        try {
            User admin = adminPermissionService.requireAdmin(authorization, "SUPER_ADMIN", "CERT_AUDITOR");
            userService.rejectVerification(verificationId, admin.getUserId(), body == null ? null : body.get("rejectReason"));
            return ApiResponse.success("实名认证已驳回", null);
        } catch (BusinessException e) {
            return ApiResponse.fail(e.getCode(), e.getMessage());
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(400, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.fail(500, "实名认证审核失败");
        }
    }

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
            String originalFilename = file.getOriginalFilename();
            String ext = originalFilename != null && originalFilename.contains(".") 
                    ? originalFilename.substring(originalFilename.lastIndexOf(".")) 
                    : ".png";
            String newFileName = UUID.randomUUID().toString().replace("-", "") + ext;

            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                boolean created = uploadDir.mkdirs();
                if (!created) {
                    System.err.println("无法创建上传目录: " + uploadDir.getAbsolutePath());
                }
            }

            File destFile = new File(uploadDir, newFileName);
            file.transferTo(destFile.getAbsoluteFile());

            String avatarUrl = "/uploads/avatars/" + newFileName;

            User user = userService.findById(userId);
            if (user != null) {
                user.setAvatarUrl(avatarUrl);
                userService.updateUser(user);
            }

            return ApiResponse.success(avatarUrl);

        } catch (IOException e) {
            e.printStackTrace();
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
            Long userId = requireUserId(authorization);
            userService.deactivateUser(userId);
            return ApiResponse.success("账号已进入注销冷静期", null);
        } catch (Exception e) {
            return ApiResponse.fail(500, "账号注销失败");
        }
    }

    @PostMapping("/deactivate/cancel")
    public ApiResponse<String> cancelDeactivateUser(@RequestHeader("Authorization") String authorization) {
        try {
            Long userId = requireUserId(authorization);
            userService.cancelDeactivation(userId);
            return ApiResponse.success("已取消账号注销", null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(400, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.fail(500, "取消注销失败");
        }
    }

    private Long requireUserId(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new IllegalArgumentException("未登录");
        }
        Long userId = JwtUtil.getUserId(authorization.substring(7));
        if (userId == null) {
            throw new IllegalArgumentException("无效token");
        }
        return userId;
    }

    private Path resolveUploadFolder() {
        Path path = Paths.get(fileUploadDir);
        if (!path.isAbsolute()) {
            path = Paths.get(System.getProperty("user.dir")).resolve(path);
        }
        return path.normalize().toAbsolutePath();
    }
}
