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
