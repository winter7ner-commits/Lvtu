USE lvtu;


-- 用户表
CREATE TABLE user (
    user_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    phone VARCHAR(20) COMMENT '手机号',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) COMMENT='用户表';


-- 律师表
CREATE TABLE lawyer (
    lawyer_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '律师ID',
    name VARCHAR(50) NOT NULL COMMENT '律师姓名',
    phone VARCHAR(20) COMMENT '联系电话',
    law_firm VARCHAR(100) COMMENT '所属律师事务所',
    practice_years INT DEFAULT 0 COMMENT '执业年限（单位：年）',
    rating DECIMAL(2,1) DEFAULT 0 COMMENT '评分'
) COMMENT='律师表';


-- 擅长领域表
CREATE TABLE specialty (
    specialty_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '领域ID',
    name VARCHAR(50) NOT NULL COMMENT '领域名称（劳动法/合同法/刑事/民事等）'
) COMMENT='法律擅长领域表';


-- 律师-领域关联表
CREATE TABLE lawyer_specialty (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    lawyer_id BIGINT NOT NULL COMMENT '律师ID',
    specialty_id INT NOT NULL COMMENT '领域ID',

    CONSTRAINT fk_ls_lawyer FOREIGN KEY (lawyer_id) REFERENCES lawyer(lawyer_id),
    CONSTRAINT fk_ls_specialty FOREIGN KEY (specialty_id) REFERENCES specialty(specialty_id)
) COMMENT='律师与擅长领域关联表';


-- 服务类型表
CREATE TABLE service_type (
    service_type_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '服务类型ID',
    name VARCHAR(50) NOT NULL COMMENT '服务名称（咨询律师/文书代写/合同审核/解困热线）',
    description VARCHAR(255) COMMENT '服务描述'
) COMMENT='服务类型表';


-- 订单表
CREATE TABLE `order` (
    order_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '订单ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    lawyer_id BIGINT COMMENT '律师ID',
    service_type_id INT NOT NULL COMMENT '服务类型ID',
    total_amount DECIMAL(10,2) DEFAULT 0 COMMENT '订单总金额',
    status VARCHAR(20) DEFAULT '待支付' COMMENT '订单状态（待支付/已支付/处理中/已完成）',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',

    CONSTRAINT fk_order_user FOREIGN KEY (user_id) REFERENCES user(user_id),
    CONSTRAINT fk_order_lawyer FOREIGN KEY (lawyer_id) REFERENCES lawyer(lawyer_id),
    CONSTRAINT fk_order_service FOREIGN KEY (service_type_id) REFERENCES service_type(service_type_id)
) COMMENT='订单表（一个订单对应一个服务）';


-- 订单服务详情表
CREATE TABLE order_service_detail (
    detail_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '详情ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    form_data JSON NOT NULL COMMENT '表单数据',

    CONSTRAINT fk_form_order FOREIGN KEY (order_id) REFERENCES `order`(order_id)

) COMMENT='订单服务详情表';


-- 支付表
CREATE TABLE payment (
    payment_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '支付ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    amount DECIMAL(10,2) NOT NULL COMMENT '支付金额',
    status VARCHAR(20) DEFAULT '未支付' COMMENT '支付状态（未支付/已支付）',
    payment_time DATETIME COMMENT '支付时间',

    CONSTRAINT fk_payment_order FOREIGN KEY (order_id) REFERENCES `order`(order_id)
) COMMENT='支付表';


-- 服务结果表
CREATE TABLE service_result (
    result_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '结果ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    lawyer_id BIGINT NOT NULL COMMENT '律师ID',
    content TEXT COMMENT '服务结果内容（回复/文书内容）',
    attachments VARCHAR(255) COMMENT '附件路径',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',

    CONSTRAINT fk_result_order FOREIGN KEY (order_id) REFERENCES `order`(order_id),
    CONSTRAINT fk_result_lawyer FOREIGN KEY (lawyer_id) REFERENCES lawyer(lawyer_id)
) COMMENT='服务结果表（一个订单对应一个结果）';




-- 测试数据插入
-- 用户
INSERT INTO user (user_id, username, password, phone) VALUES
(1, '张三', '123456', '13800000001'),
(2, '李四', '123456', '13800000002'),
(3, '王五', '123456', '13800000003');

-- 律师
INSERT INTO lawyer (lawyer_id, name, phone, law_firm, practice_years, rating) VALUES
(1, '张伟', '13900000001', '正义律师事务所', 5, 4.5),
(2, '李娜', '13900000002', '诚信律师事务所', 8, 4.8),
(3, '张律师', '13900000003', '公平律师事务所', 3, 4.2);

-- 擅长领域
INSERT INTO specialty (specialty_id, name) VALUES
(1, '劳动法'),
(2, '合同纠纷'),
(3, '民事诉讼'),
(4, '刑事辩护'),
(5,'婚姻家事'),
(6,'债权债务'),
(7,'知识产权'),
(8,'经济犯罪')，
(9,'取保候审'),
(10,'交通事故');

-- 律师-领域关联
INSERT INTO lawyer_specialty (lawyer_id, specialty_id) VALUES
(1, 1), -- 张伟：劳动法
(1, 3), -- 张伟：民事诉讼

(2, 2), -- 李娜：合同纠纷
(2, 3), -- 李娜：民事诉讼

(3, 3), -- 张律师：民事诉讼
(3, 4); -- 张律师：刑事辩护

-- 服务类型
INSERT INTO service_type (service_type_id, name, description) VALUES
(1, '咨询律师', '在线法律咨询'),
(2, '文书代写', '法律文书撰写服务'),
(3, '合同审核', '合同风险审查'),
(4, '解困热线', '紧急法律援助');



-- 测试订单一
-- 订单
INSERT INTO `order` (order_id, user_id, lawyer_id, service_type_id, total_amount, status)
VALUES (1001, 1, 1, 1, 50.00, '已完成');

-- 表单数据
INSERT INTO form_data (order_id, form_data) VALUES
(1001, JSON_OBJECT(
    'question', '公司拖欠工资怎么办？'
));

-- 支付
INSERT INTO payment (order_id, amount, status, payment_time) VALUES
(1001, 50.00, '已支付', NOW());

-- 服务结果
INSERT INTO service_result (order_id, lawyer_id, content, attachments) VALUES
(1001, 1, '建议先与公司协商，若无果可申请劳动仲裁。', NULL);


-- 测试订单二
-- 订单
INSERT INTO `order` (order_id, user_id, lawyer_id, service_type_id, total_amount, status)
VALUES (1002, 2, 2, 2, 120.00, '已完成');

-- 表单数据
INSERT INTO form_data (order_id, form_data) VALUES
(1002, JSON_OBJECT(
    'title', '起诉状',
    'content', '因借款纠纷起诉对方...',
    'attachments', JSON_ARRAY('evidence1.pdf', 'evidence2.jpg')
));

-- 支付（第一次）
INSERT INTO payment (order_id, amount, status, payment_time) VALUES
(1002, 100.00, '已支付', NOW());

-- 支付（追加打赏）
INSERT INTO payment (order_id, amount, status, payment_time) VALUES
(1002, 20.00, '已支付', NOW());

-- 服务结果
INSERT INTO service_result (order_id, lawyer_id, content, attachments) VALUES
(1002, 2, '已为您完成起诉状撰写，请查收附件。', 'complaint.docx');



-- 测试订单三
-- 订单
INSERT INTO `order` (order_id, user_id, lawyer_id, service_type_id, total_amount, status)
VALUES (1003, 3, 3, 3, 80.00, '处理中');

-- 表单数据
INSERT INTO form_data (order_id, form_data) VALUES
(1003, JSON_OBJECT(
    'contract_type', '劳动合同',
    'risk_focus', '违约责任条款',
    'attachments', JSON_ARRAY('contract.pdf')
));

-- 支付
INSERT INTO payment (order_id, amount, status, payment_time) VALUES
(1003, 80.00, '已支付', NOW());

-- 无服务结果（因为还在处理中）