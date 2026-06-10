package com.bitzh.lvtu.controller;

import com.bitzh.lvtu.common.ApiResponse;
import com.bitzh.lvtu.dto.ApplicationDTO;
import com.bitzh.lvtu.service.AdminPermissionService;
import com.bitzh.lvtu.service.ApplicationService;
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
import java.util.UUID;

@RestController
@RequestMapping("/application")
@CrossOrigin(origins = "*")  // 临时确保跨域，与 CorsConfig 互补
public class ApplicationController {

    @Resource
    private ApplicationService applicationService;

    @Resource
    private AdminPermissionService adminPermissionService;

    // 图片保存目录（从配置文件读取）
    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;


    @PostMapping("/submit")
    public ApiResponse<String> submit(@RequestBody ApplicationDTO dto) {
        applicationService.submit(dto);
        return ApiResponse.success("提交成功", null);
    }

    @GetMapping("/my")
    public ApiResponse<ApplicationDTO> myApplication(@RequestParam Long userId) {
        ApplicationDTO dto = applicationService.getMyApplication(userId);
        return ApiResponse.success(dto);
    }

    @GetMapping("/pending")
    public ApiResponse<List<ApplicationDTO>> pendingList(@RequestHeader(value = "Authorization", required = false) String authorization) {
        adminPermissionService.requireAdmin(authorization, "SUPER_ADMIN", "CERT_AUDITOR");
        return ApiResponse.success(applicationService.listAll());
    }

    @GetMapping("/list")
    public ApiResponse<List<ApplicationDTO>> list(@RequestHeader(value = "Authorization", required = false) String authorization) {
        adminPermissionService.requireAdmin(authorization, "SUPER_ADMIN", "CERT_AUDITOR");
        return ApiResponse.success(applicationService.listAll());
    }

    @PostMapping("/approve")
    public ApiResponse<String> approve(@RequestHeader(value = "Authorization", required = false) String authorization,
                                       @RequestParam Long applicationId) {
        adminPermissionService.requireAdmin(authorization, "SUPER_ADMIN", "CERT_AUDITOR");
        applicationService.approve(applicationId);
        return ApiResponse.success("审核通过", null);
    }

    @PostMapping("/reject")
    public ApiResponse<String> reject(@RequestHeader(value = "Authorization", required = false) String authorization,
                                      @RequestParam Long applicationId,
                                      @RequestParam String rejectReason) {
        adminPermissionService.requireAdmin(authorization, "SUPER_ADMIN", "CERT_AUDITOR");
        applicationService.reject(applicationId, rejectReason);
        return ApiResponse.success("已驳回", null);
    }

    // ========== 新增：图片上传接口 ==========
    @PostMapping("/uploadImage")
    public ApiResponse<String> uploadImage(@RequestParam("file") MultipartFile file) {
        // 1. 非空校验
        if (file == null || file.isEmpty()) {
            return ApiResponse.fail(400, "请选择要上传的图片");
        }

        // 2. 文件类型校验
        String originalName = file.getOriginalFilename();
        if (originalName == null ||
                !(originalName.toLowerCase().endsWith(".jpg") ||
                        originalName.toLowerCase().endsWith(".jpeg") ||
                        originalName.toLowerCase().endsWith(".png"))) {
            return ApiResponse.fail(400, "只支持 JPG、JPEG、PNG 格式");
        }

        // 3. 文件大小限制（10MB）
        if (file.getSize() > 10 * 1024 * 1024) {
            return ApiResponse.fail(400, "图片大小不能超过 10MB");
        }

        try {
            // 4. 生成唯一文件名
            String ext = originalName.substring(originalName.lastIndexOf("."));
            String newFileName = UUID.randomUUID().toString() + ext;

            // 5. 确保目录存在
            Path uploadFolder = resolveUploadFolder();
            Files.createDirectories(uploadFolder);

            // 6. 保存文件
            File destFile = uploadFolder.resolve(newFileName).toFile();
            file.transferTo(destFile);

            // 7. 返回可访问的 URL（通过本 Controller 的另一个接口）
            String imageUrl = "/application/image/" + newFileName;
            return ApiResponse.success(imageUrl);

        } catch (IOException e) {
            e.printStackTrace();
            return ApiResponse.fail(500, "文件上传失败: " + e.getMessage());
        }
    }

    // ========== 新增：图片下载接口（供前端预览） ==========
    @GetMapping("/image/{filename}")
    public ResponseEntity<byte[]> getImage(@PathVariable String filename) {
        File imageFile = resolveUploadFolder().resolve(filename).toFile();
        if (!imageFile.exists()) {
            return ResponseEntity.notFound().build();
        }
        try {
            // 根据扩展名设置 Content-Type
            String contentType = "application/octet-stream";
            if (filename.toLowerCase().endsWith(".jpg") || filename.toLowerCase().endsWith(".jpeg")) {
                contentType = "image/jpeg";
            } else if (filename.toLowerCase().endsWith(".png")) {
                contentType = "image/png";
            }
            byte[] data = Files.readAllBytes(imageFile.toPath());
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(data);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    private Path resolveUploadFolder() {
        Path path = Paths.get(uploadDir);
        if (!path.isAbsolute()) {
            path = Paths.get(System.getProperty("user.dir")).resolve(path);
        }
        return path.normalize().toAbsolutePath();
    }
}
