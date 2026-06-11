-- 评价举报表
DROP TABLE IF EXISTS evaluation_report;
CREATE TABLE evaluation_report (
  report_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '举报记录ID',
  evaluation_id BIGINT NOT NULL COMMENT '被举报评价ID',
  reporter_id BIGINT NOT NULL COMMENT '举报人用户ID',
  reason ENUM('abuse', 'false', 'ad', 'privacy') NOT NULL COMMENT '举报原因',
  description VARCHAR(300) COMMENT '举报详细说明',
  status ENUM('pending', 'approved', 'rejected') DEFAULT 'pending' COMMENT '处理状态',
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '举报时间',

  -- 外键约束
  CONSTRAINT fk_report_eval FOREIGN KEY (evaluation_id) REFERENCES evaluation(evaluation_id),
  CONSTRAINT fk_report_user FOREIGN KEY (reporter_id) REFERENCES users(userid)
) COMMENT '评价举报表';

-- 索引优化
CREATE INDEX idx_report_eval ON evaluation_report(evaluation_id);
CREATE INDEX idx_report_status ON evaluation_report(status);
CREATE INDEX idx_report_time ON evaluation_report(created_time DESC);
