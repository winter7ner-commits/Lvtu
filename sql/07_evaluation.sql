-- ===================================================================
-- 评价系统 DDL (v1.0)
-- 包含：服务评价表 + 举报表 + 管理日志表
-- 执行顺序：1. evaluation → 2. evaluation_report → 3. admin_evaluation_log
-- 依赖前置：07_application.sql, 02_user.sql, 05_lawyer.sql 必须已执行
-- ===================================================================

USE lvtu;
-- ------------------------------
-- 1. 服务评价表 (核心表)
-- ------------------------------
DROP TABLE IF EXISTS `evaluation`;
CREATE TABLE `evaluation` (
  `evaluation_id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '评价唯一ID',
  `order_id` BIGINT NOT NULL COMMENT '关联订单ID',
  `user_id` INT NOT NULL COMMENT '评价者用户ID',
  `lawyer_id` INT NOT NULL COMMENT '被评价律师ID',
  `professional_score` TINYINT NOT NULL COMMENT '专业度评分(1-5分)',
  `responsiveness_score` TINYINT NOT NULL COMMENT '响应速度评分(1-5分)',
  `attitude_score` TINYINT NOT NULL COMMENT '服务态度评分(1-5分)',
  `total_score` DECIMAL(2,1) NOT NULL COMMENT '综合评分',
  `content` VARCHAR(500) COMMENT '评价文字内容（选填）',
  `is_anonymous` BOOLEAN DEFAULT FALSE COMMENT '是否匿名评价',
  `status` ENUM('normal', 'hidden', 'deleted', 'pending') DEFAULT 'normal' COMMENT '状态',
  `report_count` INT DEFAULT 0 COMMENT '被举报次数',
  `is_featured` BOOLEAN DEFAULT FALSE COMMENT '是否精选评价',
  `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  
  -- 业务约束
  CONSTRAINT chk_scores_range CHECK (
    professional_score BETWEEN 1 AND 5 AND
    responsiveness_score BETWEEN 1 AND 5 AND
    attitude_score BETWEEN 1 AND 5
  ),
  CONSTRAINT uq_order_evaluation UNIQUE (order_id), -- 一单只能评价一次
  
  -- 外键约束
  CONSTRAINT fk_eval_order 
    FOREIGN KEY (order_id) 
    REFERENCES application(application_id),
    
  CONSTRAINT fk_eval_user 
    FOREIGN KEY (user_id) 
    REFERENCES user(id),
    
  CONSTRAINT fk_eval_lawyer 
    FOREIGN KEY (lawyer_id) 
    REFERENCES lawyer(user_id)
) COMMENT '服务评价表';

-- 索引优化
CREATE INDEX idx_eval_lawyer ON evaluation(lawyer_id);
CREATE INDEX idx_eval_order ON evaluation(order_id);
CREATE INDEX idx_eval_status ON evaluation(status);
CREATE INDEX idx_eval_featured ON evaluation(is_featured);

-- ------------------------------
-- 2. 评价举报表
-- ------------------------------
DROP TABLE IF EXISTS `evaluation_report`;
CREATE TABLE `evaluation_report` (
  `report_id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '举报记录ID',
  `evaluation_id` BIGINT NOT NULL COMMENT '被举报评价ID',
  `reporter_id` INT NOT NULL COMMENT '举报人用户ID',
  `reason` ENUM('abuse', 'false', 'ad', 'privacy') NOT NULL COMMENT '举报原因',
  `description` VARCHAR(300) COMMENT '举报详细说明',
  `status` ENUM('pending', 'approved', 'rejected') DEFAULT 'pending' COMMENT '处理状态',
  `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '举报时间',
  
  -- 外键约束
  CONSTRAINT fk_report_eval 
    FOREIGN KEY (evaluation_id) 
    REFERENCES evaluation(evaluation_id),
    
  CONSTRAINT fk_report_user 
    FOREIGN KEY (reporter_id) 
    REFERENCES user(id)
) COMMENT '评价举报表';

-- 索引优化
CREATE INDEX idx_report_eval ON evaluation_report(evaluation_id);
CREATE INDEX idx_report_status ON evaluation_report(status);
CREATE INDEX idx_report_time ON evaluation_report(created_time DESC);

-- ------------------------------
-- 3. 管理员评价处理表
-- ------------------------------
DROP TABLE IF EXISTS `admin_evaluation_log`;
CREATE TABLE `admin_evaluation_log` (
  `log_id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '管理记录ID',
  `evaluation_id` BIGINT NOT NULL COMMENT '被处理评价ID',
  `admin_id` INT NOT NULL COMMENT '管理员ID',
  `action_type` ENUM('hide', 'delete', 'restore', 'feature', 'unfeature', 'reject_report') NOT NULL COMMENT '操作类型',
  `reason` VARCHAR(300) COMMENT '操作原因说明',
  `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  
  -- 外键约束
  CONSTRAINT fk_log_eval 
    FOREIGN KEY (evaluation_id) 
    REFERENCES evaluation(evaluation_id),
    
  CONSTRAINT fk_log_admin 
    FOREIGN KEY (admin_id) 
    REFERENCES user(id)
) COMMENT '管理员评价处理表';

-- 索引优化
CREATE INDEX idx_log_eval ON admin_evaluation_log(evaluation_id);
CREATE INDEX idx_log_action ON admin_evaluation_log(action_type);
CREATE INDEX idx_log_time ON admin_evaluation_log(created_time DESC);


