create table users (
	id bigint auto_increment primary key comment '主键ID',
    username varchar(50) not null unique comment '用户名',
    password_hash varchar(255) not null comment '加密后的密码',
    phone varchar(20) unique comment '手机号',
    email varchar(100) unique comment '电子邮箱',
    avatar_url varchar(255) default null comment '头像地址',
    user_type tinyint not null default 1 comment '用户类型：1.普通用户，2.律师，3.管理员',
    `status` tinyint not null default 1 comment '用户状态：0.冻结，1.正常，2.封禁',
    is_verified boolean default false comment '是否实名认证',
    auth_status tinyint default 0 comment '律师认证状态：0.未申请，1.审核中，2.通过，3.拒绝',
    login_fail_count int default 0 comment '连续登录失败次数',
    locked_ubtil datetime default null comment '账号锁定截止时间',
    created_at timestamp default current_timestamp comment '注册时间',
    updated_at timestamp default current_timestamp on update current_timestamp comment '更新时间',
    index idx_username(username),
    index idx_phone(phone)
) engine=InnoDB default charset=utf8mb4 comment='用户基本信息表';