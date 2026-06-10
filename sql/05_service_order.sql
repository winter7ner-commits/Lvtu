USE lvtu;

-- 服务类型表
CREATE TABLE IF NOT EXISTS service_type (
    service_type_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '服务类型ID',
    name VARCHAR(50) NOT NULL COMMENT '服务名称（在线法律咨询/电话法律咨询/文书代写/合同审核/婚姻家事/诉讼代理）',
    description VARCHAR(255) COMMENT '服务描述'
) COMMENT='服务类型表';

INSERT INTO service_type (service_type_id, name, description) VALUES
    (101, '在线法律咨询', '通过文字或图文方式进行法律咨询'),
    (102, '电话法律咨询', '通过电话进行法律咨询'),
    (103, '文书代写', '起草或修改起诉状、协议等法律文书'),
    (104, '合同审核', '合同审查及风险提示'),
    (105, '婚姻家事', '婚姻、继承、抚养、财产分割咨询及代理'),
    (106, '诉讼代理', '民商事或劳动争议诉讼代理');


-- 订单表
CREATE TABLE IF NOT EXISTS `order` (
    order_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '订单ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    lawyer_id BIGINT COMMENT '实际接单律师ID',
    target_lawyer_id BIGINT NULL COMMENT '用户指定律师ID，公共订单为空',
    assignment_type VARCHAR(20) NOT NULL DEFAULT 'PUBLIC' COMMENT '分配类型：PUBLIC公共接单，DIRECT指定律师',
    service_type_id INT NOT NULL COMMENT '服务类型ID',
    total_amount DECIMAL(10,2) DEFAULT 0 COMMENT '订单总金额',
    status VARCHAR(20) NOT NULL DEFAULT '待支付' COMMENT '订单状态（待支付/待接单/处理中/待客户确认/待评价/已完成/已取消/平台介入）',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',

    updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    CONSTRAINT fk_order_user FOREIGN KEY (user_id) REFERENCES users(user_id),
    CONSTRAINT fk_order_lawyer FOREIGN KEY (lawyer_id) REFERENCES lawyer(lawyer_id),
    CONSTRAINT fk_order_target_lawyer FOREIGN KEY (target_lawyer_id) REFERENCES lawyer(lawyer_id),
    CONSTRAINT fk_order_service FOREIGN KEY (service_type_id) REFERENCES service_type(service_type_id),
    INDEX idx_order_assignment (assignment_type, target_lawyer_id, status)
) COMMENT='订单表（一个订单对应一个服务）';

INSERT INTO `order` (
    order_id, user_id, lawyer_id, target_lawyer_id, assignment_type, service_type_id, total_amount, status, created_time, updated_time
) VALUES
    (800001, 500001, NULL,   NULL,   'PUBLIC', 105, 199.00, '待支付', '2026-05-01 10:00:00', '2026-05-01 10:00:00'),
    (800002, 500002, NULL,   NULL,   'PUBLIC', 104, 299.00, '待接单', '2026-05-02 11:00:00', '2026-05-02 11:00:00'),
    (800003, 500001, 700001, NULL,   'PUBLIC', 103, 399.00, '处理中', '2026-05-03 09:30:00', '2026-05-03 10:00:00'),
    (800004, 500002, 700001, NULL,   'PUBLIC', 106, 899.00, '待客户确认', '2026-05-04 14:20:00', '2026-05-05 18:00:00'),
    (800005, 500001, 700001, 700001, 'DIRECT', 101, 129.00, '待评价', '2026-04-28 08:45:00', '2026-04-29 16:10:00'),
    (800006, 500002, 700002, NULL,   'PUBLIC', 104, 599.00, '已完成', '2026-05-06 13:15:00', '2026-05-06 15:30:00'),
    (800007, 500001, NULL,   NULL,   'PUBLIC', 102, 159.00, '已取消', '2026-05-07 16:00:00', '2026-05-07 16:20:00');


-- 订单服务详情表
CREATE TABLE IF NOT EXISTS order_service_detail (
    detail_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '详情ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    form_data JSON NOT NULL COMMENT '表单数据',

    CONSTRAINT fk_form_order FOREIGN KEY (order_id) REFERENCES `order`(order_id)

) COMMENT='订单服务详情表';

INSERT INTO order_service_detail (detail_id, order_id, form_data) VALUES
    (850001, 800001, JSON_OBJECT(
        'contactName', '张三',
        'contactPhone', '13800000001',
        'caseType', '离婚财产分割',
        'summary', '用户希望咨询婚后共同财产和孩子抚养权分配问题。',
        'expectedGoal', '明确诉讼和协议离婚方案差异'
    )),
    (850002, 800002, JSON_OBJECT(
        'contactName', '李四',
        'contactPhone', '13800000002',
        'contractName', '房屋租赁合同',
        'summary', '希望律师审核租赁合同中违约责任和押金退还条款。',
        'expectedGoal', '出具风险提示和修改建议'
    )),
    (850003, 800003, JSON_OBJECT(
        'contactName', '张三',
        'documentType', '民事起诉状',
        'summary', '用户需要代写民间借贷纠纷起诉状。',
        'facts', '借款20万，约定还款时间已到但对方未还款。'
    )),
    (850004, 800004, JSON_OBJECT(
        'contactName', '李四',
        'caseType', '劳动争议仲裁',
        'summary', '用户需要律师代理劳动争议仲裁并准备材料。',
        'employer', '某科技公司'
    )),
    (850005, 800005, JSON_OBJECT(
        'contactName', '张三',
        'consultMode', '在线咨询',
        'summary', '用户咨询合同解除通知送达的法律风险。'
    )),
    (850006, 800006, JSON_OBJECT(
        'contactName', '李四',
        'contractName', '技术服务合同',
        'summary', '用户希望审核技术服务合同和保密条款。'
    )),
    (850007, 800007, JSON_OBJECT(
        'contactName', '张三',
        'contactPhone', '13800000001',
        'consultMode', '电话咨询',
        'summary', '用户提交电话咨询后取消订单，尚未进入接单流程。'
    ));


-- 支付表
CREATE TABLE IF NOT EXISTS payment (
    payment_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '支付ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    amount DECIMAL(10,2) NOT NULL COMMENT '支付金额',
    status VARCHAR(20) DEFAULT '未支付' COMMENT '支付状态（未支付/已支付/已取消）',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '支付创建时间',
    payment_time DATETIME COMMENT '支付时间',

    CONSTRAINT fk_payment_order 
        FOREIGN KEY (order_id) REFERENCES `order`(order_id)
) COMMENT='支付表';


-- 测试数据
INSERT INTO payment (payment_id, order_id, amount, status, create_time, payment_time) VALUES
    (860001, 800001, 199.00, '未支付', '2026-05-01 09:58:00', NULL),
    (860002, 800002, 299.00, '已支付', '2026-05-02 10:52:00', '2026-05-02 11:05:00'),
    (860003, 800003, 399.00, '已支付', '2026-05-03 09:20:00', '2026-05-03 09:35:00'),
    (860004, 800004, 899.00, '已支付', '2026-05-04 14:10:00', '2026-05-04 14:25:00'),
    (860005, 800005, 129.00, '已支付', '2026-04-28 08:40:00', '2026-04-28 08:50:00'),
    (860006, 800006, 599.00, '已支付', '2026-05-06 13:05:00', '2026-05-06 13:20:00'),
    (860007, 800007, 159.00, '已取消', '2026-05-07 15:50:00', NULL);
