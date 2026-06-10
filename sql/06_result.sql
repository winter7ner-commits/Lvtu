USE lvtu;

CREATE TABLE IF NOT EXISTS service_result (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '服务结果ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    lawyer_id BIGINT NOT NULL COMMENT '提交结果的律师ID',
    title VARCHAR(100) NOT NULL COMMENT '服务结果标题',
    content TEXT NULL COMMENT '服务结果说明',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0已提交，1用户确认，2需修改',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    CONSTRAINT fk_service_result_order FOREIGN KEY (order_id) REFERENCES `order`(order_id),
    CONSTRAINT fk_service_result_lawyer FOREIGN KEY (lawyer_id) REFERENCES lawyer(lawyer_id)
) COMMENT='服务结果表';

INSERT INTO service_result (
    id, order_id, lawyer_id, title, content, status, created_at, updated_at
) VALUES
    (900001, 800004, 700001, '劳动争议仲裁代理方案', '已完成仲裁申请书、证据目录和风险提示说明，建议用户按约定时间提交仲裁材料。', 0, '2026-05-05 17:30:00', '2026-05-05 17:30:00'),
    (900002, 800005, 700001, '合同解除法律意见', '已向用户说明解除通知送达、生效时间以及证据保全要点，用户已确认服务完成，等待评价。', 1, '2026-04-29 15:30:00', '2026-04-29 16:10:00'),
    (900003, 800006, 700002, '技术服务合同审核意见', '已完成技术服务合同和保密条款审核，用户已确认并完成评价。', 1, '2026-05-06 14:50:00', '2026-05-06 15:30:00');

CREATE TABLE IF NOT EXISTS service_result_attachment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '附件ID',
    result_id BIGINT NOT NULL COMMENT '服务结果ID',
    file_name VARCHAR(100) NOT NULL COMMENT '文件名',
    file_url VARCHAR(255) NOT NULL COMMENT '文件路径',
    file_type VARCHAR(255) NULL COMMENT '文件类型',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
    CONSTRAINT fk_result_attachment_result FOREIGN KEY (result_id) REFERENCES service_result(id) ON DELETE CASCADE
) COMMENT='服务结果附件表';

INSERT INTO service_result_attachment (
    id, result_id, file_name, file_url, file_type, created_at
) VALUES
    (910001, 900001, '仲裁申请书.docx', '/mock/files/arbitration-application.docx', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', '2026-05-05 17:31:00'),
    (910002, 900001, '证据目录.pdf', '/mock/files/evidence-list.pdf', 'application/pdf', '2026-05-05 17:32:00'),
    (910003, 900002, '法律意见书.pdf', '/mock/files/legal-opinion.pdf', 'application/pdf', '2026-04-29 15:40:00'),
    (910004, 900003, '合同审核意见.pdf', '/mock/files/contract-review-opinion.pdf', 'application/pdf', '2026-05-06 14:55:00');

CREATE TABLE IF NOT EXISTS service_result_submission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '服务结果提交历史ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    result_id BIGINT NOT NULL COMMENT '服务结果ID',
    lawyer_id BIGINT NOT NULL COMMENT '提交律师ID',
    submission_no INT NOT NULL COMMENT '第几次提交',
    title VARCHAR(100) NOT NULL COMMENT '提交标题',
    content TEXT NULL COMMENT '提交内容',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
    CONSTRAINT fk_result_submission_order FOREIGN KEY (order_id) REFERENCES `order`(order_id) ON DELETE CASCADE,
    CONSTRAINT fk_result_submission_result FOREIGN KEY (result_id) REFERENCES service_result(id) ON DELETE CASCADE,
    CONSTRAINT fk_result_submission_lawyer FOREIGN KEY (lawyer_id) REFERENCES lawyer(lawyer_id),
    UNIQUE KEY uk_submission_order_no (order_id, submission_no),
    KEY idx_submission_order (order_id),
    KEY idx_submission_result (result_id)
) COMMENT='服务结果提交历史表';

INSERT INTO service_result_submission (
    id, order_id, result_id, lawyer_id, submission_no, title, content, created_at
) VALUES
    (930001, 800004, 900001, 700001, 1, '劳动争议仲裁代理方案', '已完成仲裁申请书、证据目录和风险提示说明，建议用户按约定时间提交仲裁材料。', '2026-05-05 17:30:00'),
    (930002, 800005, 900002, 700001, 1, '合同解除法律意见', '已向用户说明解除通知送达、生效时间以及证据保全要点，用户已确认服务完成，等待评价。', '2026-04-29 15:30:00'),
    (930003, 800006, 900003, 700002, 1, '技术服务合同审核意见', '已完成技术服务合同和保密条款审核，用户已确认并完成评价。', '2026-05-06 14:50:00');

CREATE TABLE IF NOT EXISTS service_result_submission_attachment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '服务结果提交历史附件ID',
    submission_id BIGINT NOT NULL COMMENT '提交历史ID',
    file_name VARCHAR(100) NOT NULL COMMENT '文件名',
    file_url VARCHAR(255) NOT NULL COMMENT '文件路径',
    file_type VARCHAR(255) NULL COMMENT '文件类型',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
    CONSTRAINT fk_submission_attachment_submission FOREIGN KEY (submission_id) REFERENCES service_result_submission(id) ON DELETE CASCADE,
    KEY idx_submission_attachment_submission (submission_id)
) COMMENT='服务结果提交历史附件表';

INSERT INTO service_result_submission_attachment (
    id, submission_id, file_name, file_url, file_type, created_at
) VALUES
    (940001, 930001, '仲裁申请书.docx', '/mock/files/arbitration-application.docx', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', '2026-05-05 17:31:00'),
    (940002, 930001, '证据目录.pdf', '/mock/files/evidence-list.pdf', 'application/pdf', '2026-05-05 17:32:00'),
    (940003, 930002, '法律意见书.pdf', '/mock/files/legal-opinion.pdf', 'application/pdf', '2026-04-29 15:40:00'),
    (940004, 930003, '合同审核意见.pdf', '/mock/files/contract-review-opinion.pdf', 'application/pdf', '2026-05-06 14:55:00');

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
    CONSTRAINT fk_result_revision_result FOREIGN KEY (result_id) REFERENCES service_result(id),
    CONSTRAINT fk_result_revision_user FOREIGN KEY (user_id) REFERENCES users(user_id),
    CONSTRAINT fk_result_revision_lawyer FOREIGN KEY (lawyer_id) REFERENCES lawyer(lawyer_id),
    KEY idx_revision_order (order_id),
    KEY idx_revision_result (result_id),
    KEY idx_revision_status (status)
) COMMENT='服务结果修改意见表';

CREATE TABLE IF NOT EXISTS system_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '配置ID',
    config_key VARCHAR(100) NOT NULL COMMENT '配置键',
    config_value VARCHAR(255) NOT NULL COMMENT '配置值',
    description VARCHAR(255) NULL COMMENT '配置说明',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_system_config_key (config_key)
) COMMENT='系统配置表';

INSERT INTO system_config (config_key, config_value, description)
VALUES ('order.max_revision_request_count', '2', '订单服务结果最多可申请修改次数')
ON DUPLICATE KEY UPDATE
    description = VALUES(description);

CREATE TABLE IF NOT EXISTS order_intervention_action (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '平台介入处理记录ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    admin_id BIGINT NOT NULL COMMENT '处理管理员ID',
    action_type VARCHAR(50) NOT NULL COMMENT '处理动作',
    reason VARCHAR(500) NOT NULL COMMENT '处理说明',
    from_status VARCHAR(50) NOT NULL COMMENT '处理前状态',
    to_status VARCHAR(50) NOT NULL COMMENT '处理后状态',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '处理时间',
    CONSTRAINT fk_intervention_action_order FOREIGN KEY (order_id) REFERENCES `order`(order_id),
    CONSTRAINT fk_intervention_action_admin FOREIGN KEY (admin_id) REFERENCES users(user_id),
    KEY idx_intervention_action_order (order_id),
    KEY idx_intervention_action_admin (admin_id)
) COMMENT='平台介入处理记录表';

CREATE TABLE IF NOT EXISTS lawyer_settlement (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '结算ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    lawyer_id BIGINT NOT NULL COMMENT '律师ID',
    amount DECIMAL(10,2) NOT NULL COMMENT '结算金额',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0待结算，1已结算',
    settled_at DATETIME NULL COMMENT '结算时间',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    CONSTRAINT fk_settlement_order FOREIGN KEY (order_id) REFERENCES `order`(order_id),
    CONSTRAINT fk_settlement_lawyer FOREIGN KEY (lawyer_id) REFERENCES lawyer(lawyer_id),
    CONSTRAINT uk_settlement_order UNIQUE (order_id)
) COMMENT='律师结算表';

INSERT INTO lawyer_settlement (
    id, order_id, lawyer_id, amount, status, settled_at, created_at
) VALUES
    (920001, 800005, 700001, 129.00, 0, NULL, '2026-04-29 16:15:00'),
    (920002, 800006, 700002, 599.00, 1, '2026-05-06 16:00:00', '2026-05-06 15:35:00');
