package com.bitzh.lvtu.controller;

import com.bitzh.lvtu.common.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/api/file")
public class FileController {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @PostMapping("/upload/avatar")
    public ApiResponse<String> uploadAvatar(@RequestParam("avatar") MultipartFile file) {
        if (file.isEmpty()) {
            return ApiResponse.fail(500, "上传文件不能为空");
        }

        try {
            // 1. 获取原始文件名和后缀
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                return ApiResponse.fail(500, "文件名无效");
            }
            String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

            // 2. 生成唯一文件名 (防止重名覆盖)
            String newFileName = UUID.randomUUID().toString() + suffix;

            // 3. 确保目录存在
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 4. 保存文件
            Path filePath = Paths.get(uploadDir, newFileName);
            Files.write(filePath, file.getBytes());

            // 5. 返回访问URL (假设后端运行在 localhost:8080)
            // 注意：这里返回的是相对路径或完整URL，取决于前端如何拼接
            String fileUrl = "/uploads/avatars/" + newFileName;
            
            return ApiResponse.success("上传成功", fileUrl);

        } catch (IOException e) {
            e.printStackTrace();
            return ApiResponse.fail(500, "文件上传失败: " + e.getMessage());
        }
    }
}