package com.bitzh.lvtu.controller;

import com.bitzh.lvtu.common.ApiResponse;
import com.bitzh.lvtu.dto.request.UpdateServiceResultRequest;
import com.bitzh.lvtu.dto.response.FileUploadResponse;
import com.bitzh.lvtu.service.ServiceResultService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;

@RestController
@Validated
@RequestMapping("/api/lawyer/results")
public class LawyerResultController {

    private static final long MAX_ATTACHMENT_SIZE = 10L * 1024 * 1024;
    private static final Set<String> BLOCKED_EXTENSIONS = Set.of("exe", "bat", "cmd", "sh", "js", "jar", "msi", "app");

    @Resource
    private ServiceResultService serviceResultService;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @PutMapping("/{resultId}")
    public ApiResponse<Void> updateResult(@PathVariable @NotNull Long resultId,
                                          @Valid @RequestBody UpdateServiceResultRequest request) {
        serviceResultService.updateResult(resultId, request);
        return ApiResponse.success("修改服务结果成功", null);
    }

    @PostMapping("/attachments/upload")
    public ApiResponse<FileUploadResponse> uploadAttachment(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return ApiResponse.fail(400, "上传文件不能为空");
        }
        if (file.getSize() > MAX_ATTACHMENT_SIZE) {
            return ApiResponse.fail(400, "附件大小不能超过10MB");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            return ApiResponse.fail(400, "文件名无效");
        }

        String safeOriginalName = Paths.get(originalFilename).getFileName().toString();
        String extension = getExtension(safeOriginalName);
        if (!extension.isBlank() && BLOCKED_EXTENSIONS.contains(extension.toLowerCase())) {
            return ApiResponse.fail(400, "不支持上传该类型文件");
        }

        String storedFileName = UUID.randomUUID().toString().replace("-", "");
        if (!extension.isBlank()) {
            storedFileName += "." + extension;
        }

        try {
            Path folder = Paths.get(uploadDir, "service-results").toAbsolutePath().normalize();
            Files.createDirectories(folder);
            Path target = folder.resolve(storedFileName).normalize();
            if (!target.startsWith(folder)) {
                return ApiResponse.fail(400, "文件路径无效");
            }
            file.transferTo(target.toFile());

            String displayName = trimFileName(safeOriginalName);
            String fileUrl = "/uploads/service-results/" + storedFileName;
            String fileType = file.getContentType();
            return ApiResponse.success("上传成功", new FileUploadResponse(displayName, fileUrl, fileType));
        } catch (IOException e) {
            return ApiResponse.fail(500, "文件上传失败: " + e.getMessage());
        }
    }

    private String getExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == filename.length() - 1) {
            return "";
        }
        return filename.substring(dotIndex + 1);
    }

    private String trimFileName(String filename) {
        if (filename.length() <= 100) {
            return filename;
        }
        String extension = getExtension(filename);
        String suffix = extension.isBlank() ? "" : "." + extension;
        int maxBaseLength = Math.max(1, 100 - suffix.length());
        String baseName = extension.isBlank() ? filename : filename.substring(0, filename.length() - suffix.length());
        return baseName.substring(0, Math.min(baseName.length(), maxBaseLength)) + suffix;
    }
}
