USE lvtu;

CREATE TABLE service_result (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    business_id INT NOT NULL COMMENT '关联的法律业务ID',
    lawyer_id INT NOT NULL COMMENT '提交结果的律师ID',
    result_title VARCHAR(100) NOT NULL COMMENT '服务结果标题',
    result_content TEXT NOT NULL COMMENT '服务结果详细内容',
    submit_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    status VARCHAR(20) DEFAULT 'SUBMITTED' COMMENT '结果状态：SUBMITTED已提交',
    CONSTRAINT fk_service_result_business FOREIGN KEY (business_id) REFERENCES business(id),
    CONSTRAINT fk_service_result_lawyer FOREIGN KEY (lawyer_id) REFERENCES lawyer(id)
) COMMENT='服务结果表';