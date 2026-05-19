-- 管理员评价处理表
DROP TABLE IF EXISTS admin_evaluation_log;
CREATE TABLE admin_evaluation_log (
  log_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '管理记录ID',
  evaluation_id BIGINT NOT NULL COMMENT '被处理评价ID',
  admin_id BIGINT NOT NULL COMMENT '管理员ID',
  action_type ENUM('hide', 'delete', 'restore', 'feature', 'unfeature', 'reject_report') NOT NULL COMMENT '操作类型',
  reason VARCHAR(300) COMMENT '操作原因说明',
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',

  -- 外键约束
  CONSTRAINT fk_log_eval FOREIGN KEY (evaluation_id) REFERENCES evaluation(evaluation_id),
  CONSTRAINT fk_log_admin FOREIGN KEY (admin_id) REFERENCES users(user_id)
) COMMENT '管理员评价处理表';

-- 索引优化
CREATE INDEX idx_log_eval ON admin_evaluation_log(evaluation_id);
CREATE INDEX idx_log_action ON admin_evaluation_log(action_type);
CREATE INDEX idx_log_time ON admin_evaluation_log(created_time DESC);
