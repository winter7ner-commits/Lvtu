USE lvtu;


create table users (
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
) engine=InnoDB default charset=utf8mb4 comment='用户基本信息表';

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