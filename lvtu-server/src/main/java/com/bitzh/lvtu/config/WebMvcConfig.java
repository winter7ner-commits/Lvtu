package com.bitzh.lvtu.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${file.access-path}")
    private String accessPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 将 /uploads/** 请求映射到本地文件系统 ./uploads/avatars/
        // addResourceLocations 需要以 file: 开头，并且路径末尾要有 /
        String location = "file:" + System.getProperty("user.dir") + "/" + uploadDir + "/";
        registry.addResourceHandler(accessPath)
                .addResourceLocations(location);
    }
}