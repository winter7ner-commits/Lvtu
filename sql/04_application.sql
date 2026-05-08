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

-- 律师由普通用户实名认证后提交申请并审核通过
INSERT INTO `application` (
    application_id, user_id, license_no, license_url, `status`,
    audit_time, reject_reason, create_time, apply_type, law_firm, practice_start_year
) VALUES
    (400001, 500003, 'LAWYER-ALPHA-001', '/mock/licenses/lawyer-alpha.jpg', 1, '2026-04-19 11:00:00', NULL, '2026-04-19 09:00:00', 0, '申城律师事务所', 2018),
    (400002, 500004, 'LAWYER-BETA-001', '/mock/licenses/lawyer-beta.jpg', 1, '2026-04-19 11:10:00', NULL, '2026-04-19 09:10:00', 0, '南山律师事务所', 2021);

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

INSERT INTO `lawyer` (
    lawyer_id, user_id, license_no, law_firm, bio, `status`, create_time, practice_years, rating
) VALUES
    (700001, 500003, 'LAWYER-ALPHA-001', '申城律师事务所', '擅长婚姻家事、合同审核与民商事纠纷。', 1, '2026-04-19 11:05:00', 8, 4.8),
    (700002, 500004, 'LAWYER-BETA-001', '南山律师事务所', '擅长劳动争议、企业合规与诉讼代理。', 1, '2026-04-19 11:15:00', 5, 4.6);

-- 擅长领域表
CREATE TABLE specialty (
                           specialty_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '领域ID',
                           name VARCHAR(50) NOT NULL UNIQUE COMMENT '领域名称'
) COMMENT='法律擅长领域表';

INSERT INTO specialty (specialty_id, name) VALUES
    (600001, '婚姻家事'),
    (600002, '合同审核'),
    (600003, '劳动争议');

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

INSERT INTO lawyer_specialty (lawyer_id, specialty_id) VALUES
    (700001, 600001),
    (700001, 600002),
    (700002, 600003);
