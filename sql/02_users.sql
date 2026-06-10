USE lvtu;


create table users (
	user_id bigint auto_increment primary key comment '主键ID',
    username varchar(50) not null unique comment '用户名',
    password_hash varchar(255) not null comment '加密后的密码',
    phone varchar(20) unique comment '手机号',
    email varchar(100) unique comment '电子邮箱',
    avatar_url varchar(255) default null comment '头像地址',
    user_type tinyint not null default 1 comment '用户类型：1.普通用户，2.律师，3.管理员',
    admin_role varchar(32) default null comment '后台角色：SUPER_ADMIN超级管理员，CERT_AUDITOR认证审核员，OPERATOR运营管理员，CUSTOMER_SERVICE客服专员',
    `status` tinyint not null default 1 comment '用户状态：0.冻结，1.正常，2.封禁',
    is_verified boolean default false comment '是否实名认证',
    auth_status tinyint default 0 comment '律师认证状态：0.未申请，1.审核中，2.通过，3.拒绝',
    created_time timestamp default current_timestamp comment '注册时间',
    updated_time timestamp default current_timestamp on update current_timestamp comment '更新时间',
    region varchar(255) default 0 comment '所在地区',
    index idx_username(username),
    index idx_phone(phone)
) engine=InnoDB default charset=utf8mb4 comment='用户基本信息表';

-- 测试用户数据（所有密码均为: test123456）
INSERT INTO users (
    user_id, username, password_hash, phone, email, avatar_url, user_type, admin_role,
    `status`, is_verified, auth_status, created_time, updated_time, region
) VALUES
    -- 用户1: user_order_a (普通用户)
    (500001, 'user_order_a', '$2b$10$jq2QdT9WGxb55eluNSi6qOzb4mndZdVYO.PRTZ.JmNuhggqctolWC', '13800000001', 'user_order_a@lvtu.test', NULL, 1, NULL, 1, TRUE, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '上海'),
    -- 用户2: user_order_b (普通用户)
    (500002, 'user_order_b', '$2b$10$z.6RcrDaZKgE/ihtf8F4Rei0yaQmKYGRoGgE1uPVWAmkhf9fkDaoO', '13800000002', 'user_order_b@lvtu.test', NULL, 1, NULL, 1, TRUE, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '北京'),
    -- 用户3: lawyer_alpha_user (律师)
    (500003, 'lawyer_alpha_user', '$2b$10$FCJo2M.Jzeio4jYqqh2ndex/Pg1WR4ll7DF8IBspQXvHp2tOl8U5S', '13800000003', 'lawyer_alpha@lvtu.test', NULL, 2, NULL, 1, TRUE, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '上海'),
    -- 用户4: lawyer_beta_user (律师)
    (500004, 'lawyer_beta_user', '$2b$10$gzfDpedOuG.6EO09UJAUZuXyt2LUAD4dhFIb0U0139LwPCW6LNIzu', '13800000004', 'lawyer_beta@lvtu.test', NULL, 2, NULL, 1, TRUE, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '深圳'),
    -- 后台账号（所有密码均为: test123456）
    (500005, 'admin_super', '$2b$10$1RTvAJcWLmV/fBshcTFf/OgOAHJhigOeo6xTUk0gI6p1Flno78aDu', '13800000005', 'admin_super@lvtu.test', NULL, 3, 'SUPER_ADMIN', 1, TRUE, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '杭州'),
    (500006, 'admin_audit', '$2b$10$1RTvAJcWLmV/fBshcTFf/OgOAHJhigOeo6xTUk0gI6p1Flno78aDu', '13800000006', 'admin_audit@lvtu.test', NULL, 3, 'CERT_AUDITOR', 1, TRUE, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '杭州'),
    (500007, 'admin_operator', '$2b$10$1RTvAJcWLmV/fBshcTFf/OgOAHJhigOeo6xTUk0gI6p1Flno78aDu', '13800000007', 'admin_operator@lvtu.test', NULL, 3, 'OPERATOR', 1, TRUE, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '杭州'),
    (500008, 'admin_service', '$2b$10$1RTvAJcWLmV/fBshcTFf/OgOAHJhigOeo6xTUk0gI6p1Flno78aDu', '13800000008', 'admin_service@lvtu.test', NULL, 3, 'CUSTOMER_SERVICE', 1, TRUE, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '杭州');

create table users_verfications (
	verification_id bigint auto_increment primary key comment '实名认证记录ID',
    user_id bigint not null unique comment '关联用户ID',
    real_name varchar(100) not null comment '真实姓名',
    id_card_number varchar(100) not null comment '身份证号（加密存储）',
    id_card_front_url varchar(255) default null comment '身份证正面照URL',
    id_card_back_url varchar(255) default null comment '身份证反面照URL',
    verification_status tinyint not null default 0 comment '认证状态：0.待审核，1.已通过，2.被拒绝',
    reject_reason varchar(255) default null comment '审核拒绝原因',
    reviewer_id bigint unique comment '审核管理员ID',
    reviewed_time datetime default null comment '审核时间',
    created_time timestamp default current_timestamp comment '认证提交时间',
    updated_time timestamp default current_timestamp on update current_timestamp comment '更新时间',
    
    constraint `fk_user_id` foreign key (user_id) references users (user_id) on delete cascade
) engine=InnoDB default charset=utf8mb4 comment='用户实名认证信息表';

-- 发单用户和律师用户都先完成实名认证
INSERT INTO users_verfications (
    verification_id, user_id, real_name, id_card_number,
    id_card_front_url, id_card_back_url, verification_status,
    reject_reason, reviewer_id, reviewed_time, created_time, updated_time
) VALUES
    (300001, 500001, '张三', '310101199001011234', '/mock/idcards/500001-front.jpg', '/mock/idcards/500001-back.jpg', 1, NULL, NULL, '2026-04-20 10:00:00', '2026-04-20 09:30:00', '2026-04-20 10:00:00'),
    (300002, 500002, '李四', '110101199202021234', '/mock/idcards/500002-front.jpg', '/mock/idcards/500002-back.jpg', 1, NULL, NULL, '2026-04-20 10:05:00', '2026-04-20 09:35:00', '2026-04-20 10:05:00'),
    (300003, 500003, '王律师', '310101198805053456', '/mock/idcards/500003-front.jpg', '/mock/idcards/500003-back.jpg', 1, NULL, NULL, '2026-04-18 15:00:00', '2026-04-18 14:20:00', '2026-04-18 15:00:00'),
    (300004, 500004, '赵律师', '440301199003033456', '/mock/idcards/500004-front.jpg', '/mock/idcards/500004-back.jpg', 1, NULL, NULL, '2026-04-18 15:10:00', '2026-04-18 14:30:00', '2026-04-18 15:10:00');

create table if not exists user_notification (
    id bigint primary key auto_increment comment '消息ID',
    user_id bigint not null comment '接收用户ID',
    type varchar(50) not null comment '消息类型',
    title varchar(100) not null comment '消息标题',
    content varchar(500) not null comment '消息内容',
    related_order_id bigint null comment '关联订单ID',
    related_path varchar(255) null comment '前端跳转路径',
    is_read tinyint(1) not null default 0 comment '是否已读：0未读，1已读',
    created_at datetime not null default current_timestamp comment '创建时间',
    read_at datetime null comment '阅读时间',
    constraint fk_notification_user foreign key (user_id) references users(user_id),
    index idx_notification_user_read(user_id, is_read),
    index idx_notification_created(created_at),
    index idx_notification_order(related_order_id)
) engine=InnoDB default charset=utf8mb4 comment='用户消息通知表';
