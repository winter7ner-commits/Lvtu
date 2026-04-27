USE lvtu;

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
    status VARCHAR(20) DEFAULT '待支付' COMMENT '订单状态（待支付/已支付/待接单/处理中/待评价/已完成）',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',

    CONSTRAINT fk_order_user FOREIGN KEY (user_id) REFERENCES users(user_id),
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
