-- 评价表结构
DROP TABLE IF EXISTS evaluation;
CREATE TABLE evaluation (
  evaluation_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '评价唯一ID',
  order_id BIGINT NOT NULL COMMENT '关联订单ID',
  user_id BIGINT NOT NULL COMMENT '评价者用户ID',
  lawyer_id BIGINT NOT NULL COMMENT '被评价律师ID',
  professional_score TINYINT NOT NULL COMMENT '专业度评分(1-5分)',
  responsiveness_score TINYINT NOT NULL COMMENT '响应速度评分(1-5分)',
  attitude_score TINYINT NOT NULL COMMENT '服务态度评分(1-5分)',
  total_score DECIMAL(2,1) NOT NULL COMMENT '综合评分',
  content VARCHAR(500) COMMENT '评价文字内容（选填）',
  is_anonymous BOOLEAN DEFAULT FALSE COMMENT '是否匿名评价',
  status ENUM('normal', 'hidden', 'deleted', 'pending') DEFAULT 'normal' COMMENT '状态',
  report_count INT DEFAULT 0 COMMENT '被举报次数',
  is_featured BOOLEAN DEFAULT FALSE COMMENT '是否精选评价',
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',

  -- 业务约束
  CONSTRAINT chk_scores_range CHECK (
    professional_score BETWEEN 1 AND 5 AND
    responsiveness_score BETWEEN 1 AND 5 AND
    attitude_score BETWEEN 1 AND 5
  ),
  CONSTRAINT uq_order_evaluation UNIQUE (order_id), -- 一单只能评价一次

  -- 外键约束
  CONSTRAINT fk_eval_order FOREIGN KEY (order_id) REFERENCES application(application_id),
  CONSTRAINT fk_eval_user FOREIGN KEY (user_id) REFERENCES users(user_id),
  CONSTRAINT fk_eval_lawyer FOREIGN KEY (lawyer_id) REFERENCES lawyer(lawyer_id)
) COMMENT '服务评价表';

-- 索引优化
CREATE INDEX idx_eval_lawyer ON evaluation(lawyer_id);
CREATE INDEX idx_eval_order ON evaluation(order_id);
CREATE INDEX idx_eval_status ON evaluation(status);
CREATE INDEX idx_eval_featured ON evaluation(is_featured);
