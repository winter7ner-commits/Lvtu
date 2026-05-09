USE lvtu;


CREATE TABLE IF NOT EXISTS users (
	user_id bigint auto_increment primary key comment '主键ID',
    username varchar(50) not null unique comment '用户名',
    password_hash varchar(255) not null comment '加密后的密码',
    phone varchar(20) unique comment '手机号',
    email varchar(100) unique comment '电子邮箱',
    avatar_url varchar(255) default null comment '头像地址',
    user_type tinyint not null default 1 comment '用户类型：1.普通用户，2.律师，3.管理员',
    `status` tinyint not null default 1 comment '用户状态：0.冻结，1.正常，2.封禁',
    is_verified boolean default false comment '是否实名认证',
    auth_status tinyint default 0 comment '律师认证状态：0.未申请，1.审核中，2.通过，3.拒绝',
    created_time timestamp default current_timestamp comment '注册时间',
    updated_time timestamp default current_timestamp on update current_timestamp comment '更新时间',
    region varchar(255) default 0 comment '所在地区',
    index idx_username(username),
    index idx_phone(phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户基本信息表';

INSERT INTO users (
    user_id, username, password_hash, phone, email, avatar_url, user_type,
    `status`, is_verified, auth_status, created_time, updated_time, region
) VALUES
    (500001, 'user_order_a', 'test123456', '13800000001', 'user_order_a@lvtu.test', NULL, 1, 1, TRUE, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '上海'),
    (500002, 'user_order_b', 'test123456', '13800000002', 'user_order_b@lvtu.test', NULL, 1, 1, TRUE, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '北京'),
    (500003, 'lawyer_alpha_user', 'test123456', '13800000003', 'lawyer_alpha@lvtu.test', NULL, 2, 1, TRUE, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '上海'),
    (500004, 'lawyer_beta_user', 'test123456', '13800000004', 'lawyer_beta@lvtu.test', NULL, 2, 1, TRUE, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '深圳'),
    (500005, 'admin_finance', 'test123456', '13800000005', 'admin_finance@lvtu.test', NULL, 3, 1, TRUE, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '杭州');

CREATE TABLE IF NOT EXISTS users_verfications (
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
    
    CONSTRAINT `fk_user_id` FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户实名认证信息表';

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
