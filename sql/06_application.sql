USE lvtu;

CREATE TABLE `application` (
                               `application_id` bigint NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
                               `user_id` bigint NOT NULL COMMENT '申请人用户ID',
                               `license_no` varchar(64) NOT NULL COMMENT '律师执业证号',
                               `license_url` varchar(512) NOT NULL COMMENT '执业证照片URL',
                               `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态: 0待审核, 1通过, 2驳回',
                               `audit_time` datetime DEFAULT NULL COMMENT '审核时间',
                               `reject_reason` varchar(512) DEFAULT NULL COMMENT '驳回原因',
                               `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
                               `apply_type` tinyint NOT NULL DEFAULT 0 COMMENT '0-首次入驻, 1-资料变更',
                               `law_firm` varchar(128) NOT NULL COMMENT '所属律所',
                               `practice_start_year` int NOT NULL COMMENT '执业开始年份'
) COMMENT='律师认证申请表';

CREATE TABLE `lawyer` (
                          `lawyer_id` bigint NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
                          `user_id` bigint NOT NULL UNIQUE COMMENT '关联用户ID',
                          `license_no` varchar(64) NOT NULL UNIQUE COMMENT '律师执业证号',
                          `law_firm` varchar(128) NOT NULL COMMENT '所属律所',
                          `bio` text DEFAULT NULL COMMENT '个人简介',
                          `status` tinyint NOT NULL DEFAULT 1 COMMENT '用户状态: 0.冻结, 1.正常, 2.待审核',
                          `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                          `practice_years` int DEFAULT 0 COMMENT '执业年限',
                          `rating` decimal(2,1) DEFAULT 0.0 COMMENT '评分'
) COMMENT='律师信息表';

-- 擅长领域表
CREATE TABLE specialty (
                           specialty_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '领域ID',
                           name VARCHAR(50) NOT NULL UNIQUE COMMENT '领域名称'
) COMMENT='法律擅长领域表';

-- 律师-领域关联表
CREATE TABLE lawyer_specialty (
                                  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
                                  lawyer_id BIGINT NOT NULL COMMENT '律师ID',
                                  specialty_id INT NOT NULL COMMENT '领域ID',

                                  UNIQUE KEY uk_lawyer_specialty (lawyer_id, specialty_id),

                                  INDEX idx_ls_lawyer (lawyer_id),
                                  INDEX idx_ls_specialty (specialty_id),

                                  CONSTRAINT fk_ls_lawyer FOREIGN KEY (lawyer_id) REFERENCES lawyer(lawyer_id),
                                  CONSTRAINT fk_ls_specialty FOREIGN KEY (specialty_id) REFERENCES specialty(specialty_id)
) COMMENT='律师与擅长领域关联表';