CREATE TABLE `application` (
                               `id` bigint NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
                               `user_id` bigint NOT NULL COMMENT '申请人用户ID',
                               `real_name` varchar(64) NOT NULL COMMENT '真实姓名',
                               `id_card` varchar(18) NOT NULL COMMENT '身份证号',
                               `license_no` varchar(64) NOT NULL COMMENT '律师执业证号',
                               `id_card_url` varchar(512) NOT NULL COMMENT '身份证照片URL',
                               `license_url` varchar(512) NOT NULL COMMENT '执业证照片URL',
                               `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态：0待审核 1通过 2驳回',
                               `audit_time` datetime DEFAULT NULL COMMENT '审核时间',
                               `reject_reason` varchar(512) DEFAULT NULL COMMENT '驳回原因',
                               `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
                               `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                               KEY `idx_user_id` (`user_id`),
                               KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='律师认证申请表';