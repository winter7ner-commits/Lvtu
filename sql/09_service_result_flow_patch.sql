USE lvtu;

-- Existing databases created before the service-result revision flow need these
-- tables for users to request revisions after lawyers submit results.
CREATE TABLE IF NOT EXISTS service_result_submission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '服务结果提交历史ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    result_id BIGINT NOT NULL COMMENT '服务结果ID',
    lawyer_id BIGINT NOT NULL COMMENT '提交律师ID',
    submission_no INT NOT NULL COMMENT '第几次提交',
    title VARCHAR(100) NOT NULL COMMENT '提交标题',
    content TEXT NULL COMMENT '提交内容',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
    CONSTRAINT fk_result_submission_order FOREIGN KEY (order_id) REFERENCES `order`(order_id),
    CONSTRAINT fk_result_submission_result FOREIGN KEY (result_id) REFERENCES service_result(id) ON DELETE CASCADE,
    CONSTRAINT fk_result_submission_lawyer FOREIGN KEY (lawyer_id) REFERENCES lawyer(lawyer_id),
    UNIQUE KEY uk_result_submission_order_no (order_id, submission_no)
) COMMENT='服务结果提交历史表';

CREATE TABLE IF NOT EXISTS service_result_submission_attachment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '服务结果提交历史附件ID',
    submission_id BIGINT NOT NULL COMMENT '提交历史ID',
    file_name VARCHAR(100) NOT NULL COMMENT '文件名',
    file_url VARCHAR(255) NOT NULL COMMENT '文件路径',
    file_type VARCHAR(255) NULL COMMENT '文件类型',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
    CONSTRAINT fk_submission_attachment_submission FOREIGN KEY (submission_id) REFERENCES service_result_submission(id) ON DELETE CASCADE
) COMMENT='服务结果提交历史附件表';

CREATE TABLE IF NOT EXISTS service_result_revision (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '修改意见ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    result_id BIGINT NOT NULL COMMENT '服务结果ID',
    user_id BIGINT NOT NULL COMMENT '申请修改的用户ID',
    lawyer_id BIGINT NOT NULL COMMENT '接单律师ID',
    revision_no INT NOT NULL COMMENT '第几次修改申请',
    content TEXT NOT NULL COMMENT '修改意见',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0待处理，1律师已重新提交，2平台介入',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
    handled_at DATETIME NULL COMMENT '处理时间',
    CONSTRAINT fk_result_revision_order FOREIGN KEY (order_id) REFERENCES `order`(order_id),
    CONSTRAINT fk_result_revision_result FOREIGN KEY (result_id) REFERENCES service_result(id) ON DELETE CASCADE,
    CONSTRAINT fk_result_revision_user FOREIGN KEY (user_id) REFERENCES users(user_id),
    CONSTRAINT fk_result_revision_lawyer FOREIGN KEY (lawyer_id) REFERENCES lawyer(lawyer_id),
    UNIQUE KEY uk_result_revision_order_no (order_id, revision_no),
    INDEX idx_result_revision_order_status (order_id, status)
) COMMENT='服务结果修改意见表';
