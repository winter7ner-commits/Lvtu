CREATE TABLE `evaluation` (
  `id` INT AUTO_INCREMENT PRIMARY KEY COMMENT '评价唯一ID',
  `application_id` INT NOT NULL COMMENT '关联申请ID（引用07_application.id）',
  `user_id` INT NOT NULL COMMENT '评价者ID（引用02_user.id）',
  `lawyer_id` INT NOT NULL COMMENT '被评价律师ID（引用05_lawyer.user_id）',
  `professional_score` TINYINT NOT NULL COMMENT '专业度评分（1-5分）',
  `responsiveness_score` TINYINT NOT NULL COMMENT '响应速度评分（1-5分）',
  `attitude_score` TINYINT NOT NULL COMMENT '服务态度评分（1-5分）',
  `content` VARCHAR(500) NOT NULL COMMENT '评价内容',
  `is_anonymous` BOOLEAN DEFAULT FALSE COMMENT '是否匿名显示',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  
  CONSTRAINT chk_scores_range CHECK (
    professional_score BETWEEN 1 AND 5 AND
    responsiveness_score BETWEEN 1 AND 5 AND
    attitude_score BETWEEN 1 AND 5
  ),
  
  CONSTRAINT fk_eval_application 
    FOREIGN KEY (application_id) 
    REFERENCES application(id) 
    ON DELETE CASCADE,
  
  CONSTRAINT fk_eval_user 
    FOREIGN KEY (user_id) 
    REFERENCES user(id) 
    ON DELETE CASCADE,
  
  CONSTRAINT fk_eval_lawyer 
    FOREIGN KEY (lawyer_id) 
    REFERENCES lawyer(user_id) 
    ON DELETE CASCADE
) COMMENT '服务评价表';

-- 索引优化
CREATE INDEX idx_eval_lawyer ON evaluation(lawyer_id);
CREATE INDEX idx_eval_application ON evaluation(application_id);
CREATE INDEX idx_eval_created ON evaluation(created_at DESC);
