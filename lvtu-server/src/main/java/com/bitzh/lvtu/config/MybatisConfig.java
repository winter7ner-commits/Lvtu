
package com.bitzh.lvtu.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis配置
 */
@Configuration
@MapperScan("com.bitzh.lvtu.mapper")
public class MybatisConfig {
}
