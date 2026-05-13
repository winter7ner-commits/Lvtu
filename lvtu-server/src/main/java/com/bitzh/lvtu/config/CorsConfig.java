package com.bitzh.lvtu.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 全局跨域配置
 *
 * 解决前端 Vue（localhost:5173）
 * 访问后端 Spring Boot（localhost:8080）
 * 时出现的：
 *
 * AxiosError: Network Error
 * blocked by CORS policy
 *
 * 等跨域问题
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {

        registry.addMapping("/**")

                /**
                 * 允许前端访问地址
                 * Vite 默认端口：5173
                 */
                .allowedOrigins("http://localhost:5173")

                /**
                 * 允许所有请求方式
                 * GET / POST / PUT / DELETE / OPTIONS 等
                 */
                .allowedMethods("*")

                /**
                 * 允许所有请求头
                 */
                .allowedHeaders("*")

                /**
                 * 是否允许携带 Cookie
                 * 如果前后端有登录状态、Session、Token 等，
                 * 建议开启
                 */
                .allowCredentials(true)

                /**
                 * 预检请求缓存时间（秒）
                 * 减少 OPTIONS 请求次数
                 */
                .maxAge(3600);
    }
}