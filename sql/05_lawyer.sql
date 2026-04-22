CREATE TABLE `user` (
                        `id` bigint NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
                        `username` varchar(64) NOT NULL UNIQUE COMMENT '账号/用户名',
                        `password` varchar(128) NOT NULL COMMENT '密码',
                        `phone` varchar(20) DEFAULT NULL UNIQUE COMMENT '手机号',
                        `email` varchar(128) DEFAULT NULL UNIQUE COMMENT '邮箱',
                        `avatar_url` varchar(512) DEFAULT NULL COMMENT '头像URL',
                        `gender` tinyint DEFAULT 0 COMMENT '性别 0-未知 1-男 2-女',
                        `region` varchar(128) DEFAULT NULL COMMENT '所在地区',
                        `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态 1-正常 0-禁用',
                        `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                        `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';



CREATE TABLE `lawyer` (
                          `id` bigint NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
                          `user_id` bigint NOT NULL UNIQUE COMMENT '关联用户ID',
                          `real_name` varchar(64) NOT NULL COMMENT '律师真实姓名',
                          `license_no` varchar(64) NOT NULL UNIQUE COMMENT '律师执业证号',
                          `law_firm` varchar(128) NOT NULL COMMENT '所属律所',
                          `bio` text COMMENT '个人简介',
                          `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：1正常 0禁用',
                          `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                          `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='律师信息表';
