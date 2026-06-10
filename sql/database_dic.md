# 数据库字典

当前 SQL 初始化文件保留 8 个：

| 文件 | 模块 |
| ---- | ---- |
| 01_database.sql | 数据库创建 |
| 02_users.sql | 用户、后台角色、实名认证、消息通知 |
| 03_legal_article.sql | 法律法规、法条解释、收藏、评论、解释反馈 |
| 04_application.sql | 律师认证申请、律师信息、擅长领域 |
| 05_service_order.sql | 服务类型、订单、订单详情、支付 |
| 06_result.sql | 服务结果、提交历史、修改意见、修改次数配置、平台介入、结算 |
| 07_evaluation.sql | 评价、举报、评价管理日志 |
| 08_china_region.sql | 省市地区基础数据 |

## 用户与消息模块

### 表名: users (用户基本信息表)

| 列名 | 数据类型 | 长度 | 小数点 | 是否可空 | 键 | 注释 |
| ---- | -------- | ---- | ------ | -------- | -- | ---- |
| user_id | bigint | - | - | 否 | 主键 | 主键ID |
| username | varchar | 50 | - | 否 | 唯一键 | 用户名 |
| password_hash | varchar | 255 | - | 否 | - | 加密后的密码 |
| phone | varchar | 20 | - | 可 | 唯一键 | 手机号 |
| email | varchar | 100 | - | 可 | 唯一键 | 电子邮箱 |
| avatar_url | varchar | 255 | - | 可 | - | 头像地址 |
| user_type | tinyint | - | - | 否 | - | 用户类型：1普通用户，2律师，3管理员 |
| admin_role | varchar | 32 | - | 可 | - | 后台角色：SUPER_ADMIN超级管理员，CERT_AUDITOR认证审核员，OPERATOR运营管理员，CUSTOMER_SERVICE客服专员 |
| status | tinyint | - | - | 否 | - | 用户状态：0冻结，1正常，2封禁 |
| is_verified | boolean | - | - | 可 | - | 是否实名认证 |
| auth_status | tinyint | - | - | 可 | - | 律师认证状态：0未申请，1审核中，2通过，3拒绝 |
| created_time | timestamp | - | - | 可 | - | 注册时间 |
| updated_time | timestamp | - | - | 可 | - | 更新时间 |
| region | varchar | 255 | - | 可 | - | 所在地区 |

默认后台测试账号密码均为 `test123456`：

| 账号 | 角色 |
| ---- | ---- |
| admin_super | 超级管理员 |
| admin_audit | 认证审核员 |
| admin_operator | 运营管理员 |
| admin_service | 客服专员 |

### 表名: users_verfications (用户实名认证信息表)

| 列名 | 数据类型 | 长度 | 小数点 | 是否可空 | 键 | 注释 |
| ---- | -------- | ---- | ------ | -------- | -- | ---- |
| verification_id | bigint | - | - | 否 | 主键 | 实名认证记录ID |
| user_id | bigint | - | - | 否 | 唯一键/外键 | 关联用户ID |
| real_name | varchar | 100 | - | 否 | - | 真实姓名 |
| id_card_number | varchar | 100 | - | 否 | - | 身份证号（加密存储） |
| id_card_front_url | varchar | 255 | - | 可 | - | 身份证正面照URL |
| id_card_back_url | varchar | 255 | - | 可 | - | 身份证反面照URL |
| verification_status | tinyint | - | - | 否 | - | 认证状态：0待审核，1已通过，2被拒绝 |
| reject_reason | varchar | 255 | - | 可 | - | 审核拒绝原因 |
| reviewer_id | bigint | - | - | 可 | 唯一键 | 审核管理员ID |
| reviewed_time | datetime | - | - | 可 | - | 审核时间 |
| created_time | timestamp | - | - | 可 | - | 认证提交时间 |
| updated_time | timestamp | - | - | 可 | - | 更新时间 |

### 表名: user_notification (用户消息通知表)

| 列名 | 数据类型 | 长度 | 小数点 | 是否可空 | 键 | 注释 |
| ---- | -------- | ---- | ------ | -------- | -- | ---- |
| id | bigint | - | - | 否 | 主键 | 消息ID |
| user_id | bigint | - | - | 否 | 外键 | 接收用户ID |
| type | varchar | 50 | - | 否 | - | 消息类型 |
| title | varchar | 100 | - | 否 | - | 消息标题 |
| content | varchar | 500 | - | 否 | - | 消息内容 |
| related_order_id | bigint | - | - | 可 | 索引 | 关联订单ID |
| related_path | varchar | 255 | - | 可 | - | 前端跳转路径 |
| is_read | tinyint | 1 | - | 否 | - | 是否已读：0未读，1已读 |
| created_at | datetime | - | - | 否 | - | 创建时间 |
| read_at | datetime | - | - | 可 | - | 阅读时间 |

## 法律条文模块

### 表名: legal_category (法规分类表)

| 列名 | 数据类型 | 长度 | 小数点 | 是否可空 | 键 | 注释 |
| ---- | -------- | ---- | ------ | -------- | -- | ---- |
| id | bigint | - | - | 否 | 主键 | 分类ID |
| name | varchar | 50 | - | 否 | - | 分类名称，如民法、行政法、刑法 |
| parent_id | bigint | - | - | 否 | - | 父分类ID，0表示一级分类 |
| sort_order | int | - | - | 否 | - | 排序号 |
| status | tinyint | - | - | 否 | - | 状态：1启用，0禁用 |
| created_at | datetime | - | - | 否 | - | 创建时间 |
| updated_at | datetime | - | - | 否 | - | 更新时间 |

### 表名: legal_document (法规文件表)

| 列名 | 数据类型 | 长度 | 小数点 | 是否可空 | 键 | 注释 |
| ---- | -------- | ---- | ------ | -------- | -- | ---- |
| id | bigint | - | - | 否 | 主键 | 文件ID |
| name | varchar | 200 | - | 否 | - | 文件名称 |
| category_id | bigint | - | - | 否 | 外键 | 所属分类ID |
| publish_date | datetime | - | - | 可 | - | 发布日期 |
| status | tinyint | - | - | 否 | - | 状态：1启用，0禁用 |
| sort_order | int | - | - | 否 | - | 排序号 |
| created_at | datetime | - | - | 否 | - | 创建时间 |
| updated_at | datetime | - | - | 否 | - | 更新时间 |

### 表名: legal_article (法条表)

| 列名 | 数据类型 | 长度 | 小数点 | 是否可空 | 键 | 注释 |
| ---- | -------- | ---- | ------ | -------- | -- | ---- |
| id | bigint | - | - | 否 | 主键 | 法条ID |
| document_id | bigint | - | - | 否 | 外键 | 所属法规文件ID |
| article_number | varchar | 50 | - | 否 | - | 条文编号，如第一条、第二条 |
| title | varchar | 200 | - | 可 | - | 章节标题 |
| content | text | - | - | 否 | - | 条文内容 |
| sort_order | int | - | - | 否 | - | 条文排序 |
| status | tinyint | - | - | 否 | - | 状态：1启用，0禁用 |
| created_at | datetime | - | - | 否 | - | 创建时间 |
| updated_at | datetime | - | - | 否 | - | 更新时间 |

### 表名: legal_article_explanation (法条解释表)

| 列名 | 数据类型 | 长度 | 小数点 | 是否可空 | 键 | 注释 |
| ---- | -------- | ---- | ------ | -------- | -- | ---- |
| id | bigint | - | - | 否 | 主键 | 解释ID |
| article_id | bigint | - | - | 否 | 外键 | 法条ID |
| content | text | - | - | 否 | - | 条文解释 |
| source | varchar | 100 | - | 可 | - | 解释来源 |
| status | tinyint | - | - | 否 | - | 状态：1启用，0禁用 |
| created_at | datetime | - | - | 否 | - | 创建时间 |
| updated_at | datetime | - | - | 否 | - | 更新时间 |

### 表名: legal_article_comment (法条评论表)

| 列名 | 数据类型 | 长度 | 小数点 | 是否可空 | 键 | 注释 |
| ---- | -------- | ---- | ------ | -------- | -- | ---- |
| id | bigint | - | - | 否 | 主键 | 评论ID |
| article_id | bigint | - | - | 否 | 外键 | 法条ID |
| user_id | bigint | - | - | 否 | 外键 | 评论用户ID |
| content | text | - | - | 否 | - | 评论内容 |
| status | tinyint | - | - | 否 | - | 状态：1正常，0隐藏 |
| created_at | datetime | - | - | 否 | - | 创建时间 |
| updated_at | datetime | - | - | 否 | - | 更新时间 |

### 表名: legal_article_favorite (法条收藏表)

| 列名 | 数据类型 | 长度 | 小数点 | 是否可空 | 键 | 注释 |
| ---- | -------- | ---- | ------ | -------- | -- | ---- |
| id | bigint | - | - | 否 | 主键 | 收藏ID |
| article_id | bigint | - | - | 否 | 唯一键/外键 | 法条ID |
| user_id | bigint | - | - | 否 | 唯一键/外键 | 收藏用户ID |
| created_at | datetime | - | - | 否 | - | 收藏时间 |

### 表名: legal_article_explanation_feedback (法条解释反馈表)

| 列名 | 数据类型 | 长度 | 小数点 | 是否可空 | 键 | 注释 |
| ---- | -------- | ---- | ------ | -------- | -- | ---- |
| id | bigint | - | - | 否 | 主键 | 解释反馈ID |
| article_id | bigint | - | - | 否 | 外键 | 法条ID |
| explanation_id | bigint | - | - | 可 | 外键 | 解释ID，暂无解释时为空 |
| user_id | bigint | - | - | 否 | 外键 | 反馈用户ID |
| helpful | tinyint | 1 | - | 否 | - | 是否有帮助：1有帮助，0无帮助 |
| reason | varchar | 50 | - | 可 | - | 反馈原因 |
| content | varchar | 140 | - | 可 | - | 补充说明 |
| status | varchar | 20 | - | 否 | - | 处理状态：pending待处理，handled已处理 |
| created_at | datetime | - | - | 否 | - | 创建时间 |
| handled_at | datetime | - | - | 可 | - | 处理时间 |

## 律师认证模块

### 表名: application (律师认证申请表)

| 列名 | 数据类型 | 长度 | 小数点 | 是否可空 | 键 | 注释 |
| ---- | -------- | ---- | ------ | -------- | -- | ---- |
| application_id | bigint | - | - | 否 | 主键 | 主键ID |
| user_id | bigint | - | - | 否 | 外键 | 申请人用户ID |
| license_no | varchar | 64 | - | 否 | - | 律师执业证号 |
| license_url | varchar | 512 | - | 否 | - | 执业证照片URL |
| status | tinyint | - | - | 否 | - | 状态：0待审核，1通过，2驳回 |
| audit_time | datetime | - | - | 可 | - | 审核时间 |
| reject_reason | varchar | 512 | - | 可 | - | 驳回原因 |
| create_time | datetime | - | - | 否 | - | 申请时间 |
| apply_type | tinyint | - | - | 否 | - | 0首次入驻，1资料变更 |
| law_firm | varchar | 128 | - | 否 | - | 所属律所 |
| practice_start_year | int | - | - | 否 | - | 执业开始年份 |

### 表名: lawyer (律师信息表)

| 列名 | 数据类型 | 长度 | 小数点 | 是否可空 | 键 | 注释 |
| ---- | -------- | ---- | ------ | -------- | -- | ---- |
| lawyer_id | bigint | - | - | 否 | 主键 | 主键ID |
| user_id | bigint | - | - | 否 | 唯一键/外键 | 关联用户ID |
| license_no | varchar | 64 | - | 否 | 唯一键 | 律师执业证号 |
| law_firm | varchar | 128 | - | 否 | - | 所属律所 |
| bio | text | - | - | 可 | - | 个人简介 |
| status | tinyint | - | - | 否 | - | 用户状态：0冻结，1正常，2待审核 |
| create_time | datetime | - | - | 否 | - | 创建时间 |
| practice_years | int | - | - | 可 | - | 执业年限 |
| rating | decimal | 2 | 1 | 可 | - | 评分 |

### 表名: specialty (法律擅长领域表)

| 列名 | 数据类型 | 长度 | 小数点 | 是否可空 | 键 | 注释 |
| ---- | -------- | ---- | ------ | -------- | -- | ---- |
| specialty_id | int | - | - | 否 | 主键 | 领域ID |
| name | varchar | 50 | - | 否 | 唯一键 | 领域名称 |

### 表名: lawyer_specialty (律师与擅长领域关联表)

| 列名 | 数据类型 | 长度 | 小数点 | 是否可空 | 键 | 注释 |
| ---- | -------- | ---- | ------ | -------- | -- | ---- |
| id | bigint | - | - | 否 | 主键 | 主键ID |
| lawyer_id | bigint | - | - | 否 | 唯一键/外键 | 律师ID |
| specialty_id | int | - | - | 否 | 唯一键/外键 | 领域ID |

## 订单支付模块

### 表名: service_type (服务类型表)

| 列名 | 数据类型 | 长度 | 小数点 | 是否可空 | 键 | 注释 |
| ---- | -------- | ---- | ------ | -------- | -- | ---- |
| service_type_id | int | - | - | 否 | 主键 | 服务类型ID |
| name | varchar | 50 | - | 否 | - | 服务名称 |
| description | varchar | 255 | - | 可 | - | 服务描述 |

### 表名: order (订单表)

| 列名 | 数据类型 | 长度 | 小数点 | 是否可空 | 键 | 注释 |
| ---- | -------- | ---- | ------ | -------- | -- | ---- |
| order_id | bigint | - | - | 否 | 主键 | 订单ID |
| user_id | bigint | - | - | 否 | 外键 | 用户ID |
| lawyer_id | bigint | - | - | 可 | 外键 | 实际接单律师ID |
| target_lawyer_id | bigint | - | - | 可 | 外键 | 用户指定律师ID，公共订单为空 |
| assignment_type | varchar | 20 | - | 否 | 索引 | 分配类型：PUBLIC公共接单，DIRECT指定律师 |
| service_type_id | int | - | - | 否 | 外键 | 服务类型ID |
| total_amount | decimal | 10 | 2 | 可 | - | 订单总金额 |
| status | varchar | 20 | - | 否 | 索引 | 订单状态：待支付/待接单/处理中/待客户确认/待评价/已完成/已取消/平台介入 |
| created_time | datetime | - | - | 否 | - | 创建时间 |
| updated_time | datetime | - | - | 否 | - | 更新时间 |

### 表名: order_service_detail (订单服务详情表)

| 列名 | 数据类型 | 长度 | 小数点 | 是否可空 | 键 | 注释 |
| ---- | -------- | ---- | ------ | -------- | -- | ---- |
| detail_id | bigint | - | - | 否 | 主键 | 详情ID |
| order_id | bigint | - | - | 否 | 外键 | 订单ID |
| form_data | json | - | - | 否 | - | 表单数据 |

### 表名: payment (支付表)

| 列名 | 数据类型 | 长度 | 小数点 | 是否可空 | 键 | 注释 |
| ---- | -------- | ---- | ------ | -------- | -- | ---- |
| payment_id | bigint | - | - | 否 | 主键 | 支付ID |
| order_id | bigint | - | - | 否 | 外键 | 订单ID |
| amount | decimal | 10 | 2 | 否 | - | 支付金额 |
| status | varchar | 20 | - | 可 | - | 支付状态：未支付/已支付/已取消 |
| create_time | datetime | - | - | 否 | - | 支付创建时间 |
| payment_time | datetime | - | - | 可 | - | 支付时间 |

## 服务结果、平台介入、结算与评价模块

### 表名: service_result (服务结果表)

| 列名 | 数据类型 | 长度 | 小数点 | 是否可空 | 键 | 注释 |
| ---- | -------- | ---- | ------ | -------- | -- | ---- |
| id | bigint | - | - | 否 | 主键 | 服务结果ID |
| order_id | bigint | - | - | 否 | 外键 | 订单ID |
| lawyer_id | bigint | - | - | 否 | 外键 | 提交结果的律师ID |
| title | varchar | 100 | - | 否 | - | 服务结果标题 |
| content | text | - | - | 可 | - | 服务结果说明 |
| status | tinyint | - | - | 否 | - | 状态：0已提交，1用户确认，2需修改 |
| created_at | datetime | - | - | 否 | - | 提交时间 |
| updated_at | datetime | - | - | 否 | - | 更新时间 |

### 表名: service_result_attachment (服务结果附件表)

| 列名 | 数据类型 | 长度 | 小数点 | 是否可空 | 键 | 注释 |
| ---- | -------- | ---- | ------ | -------- | -- | ---- |
| id | bigint | - | - | 否 | 主键 | 附件ID |
| result_id | bigint | - | - | 否 | 外键 | 服务结果ID |
| file_name | varchar | 100 | - | 否 | - | 文件名 |
| file_url | varchar | 255 | - | 否 | - | 文件访问路径 |
| file_type | varchar | 255 | - | 可 | - | 文件类型 |
| created_at | datetime | - | - | 否 | - | 上传时间 |

### 表名: service_result_submission (服务结果提交历史表)

| 列名 | 数据类型 | 长度 | 小数点 | 是否可空 | 键 | 注释 |
| ---- | -------- | ---- | ------ | -------- | -- | ---- |
| id | bigint | - | - | 否 | 主键 | 服务结果提交历史ID |
| order_id | bigint | - | - | 否 | 唯一键/外键 | 订单ID |
| result_id | bigint | - | - | 否 | 外键 | 服务结果ID |
| lawyer_id | bigint | - | - | 否 | 外键 | 提交律师ID |
| submission_no | int | - | - | 否 | 唯一键 | 第几次提交 |
| title | varchar | 100 | - | 否 | - | 提交标题 |
| content | text | - | - | 可 | - | 提交内容 |
| created_at | datetime | - | - | 否 | - | 提交时间 |

### 表名: service_result_submission_attachment (服务结果提交历史附件表)

| 列名 | 数据类型 | 长度 | 小数点 | 是否可空 | 键 | 注释 |
| ---- | -------- | ---- | ------ | -------- | -- | ---- |
| id | bigint | - | - | 否 | 主键 | 服务结果提交历史附件ID |
| submission_id | bigint | - | - | 否 | 外键 | 提交历史ID |
| file_name | varchar | 100 | - | 否 | - | 文件名 |
| file_url | varchar | 255 | - | 否 | - | 文件访问路径 |
| file_type | varchar | 255 | - | 可 | - | 文件类型 |
| created_at | datetime | - | - | 否 | - | 上传时间 |

### 表名: service_result_revision (服务结果修改意见表)

| 列名 | 数据类型 | 长度 | 小数点 | 是否可空 | 键 | 注释 |
| ---- | -------- | ---- | ------ | -------- | -- | ---- |
| id | bigint | - | - | 否 | 主键 | 修改意见ID |
| order_id | bigint | - | - | 否 | 外键 | 订单ID |
| result_id | bigint | - | - | 否 | 外键 | 服务结果ID |
| user_id | bigint | - | - | 否 | 外键 | 申请修改的用户ID |
| lawyer_id | bigint | - | - | 否 | 外键 | 接单律师ID |
| revision_no | int | - | - | 否 | - | 第几次修改申请 |
| content | text | - | - | 否 | - | 修改意见 |
| status | tinyint | - | - | 否 | - | 状态：0待处理，1律师已重新提交，2平台介入 |
| created_at | datetime | - | - | 否 | - | 申请时间 |
| handled_at | datetime | - | - | 可 | - | 处理时间 |

### 表名: system_config (系统配置表)

| 列名 | 数据类型 | 长度 | 小数点 | 是否可空 | 键 | 注释 |
| ---- | -------- | ---- | ------ | -------- | -- | ---- |
| id | bigint | - | - | 否 | 主键 | 配置ID |
| config_key | varchar | 100 | - | 否 | 唯一键 | 配置键 |
| config_value | varchar | 255 | - | 否 | - | 配置值 |
| description | varchar | 255 | - | 可 | - | 配置说明 |
| created_at | datetime | - | - | 否 | - | 创建时间 |
| updated_at | datetime | - | - | 否 | - | 更新时间 |

### 表名: order_intervention_action (平台介入处理记录表)

| 列名 | 数据类型 | 长度 | 小数点 | 是否可空 | 键 | 注释 |
| ---- | -------- | ---- | ------ | -------- | -- | ---- |
| id | bigint | - | - | 否 | 主键 | 平台介入处理记录ID |
| order_id | bigint | - | - | 否 | 外键 | 订单ID |
| admin_id | bigint | - | - | 否 | 外键 | 处理管理员ID |
| action_type | varchar | 50 | - | 否 | - | 处理动作 |
| reason | varchar | 500 | - | 否 | - | 处理说明 |
| from_status | varchar | 50 | - | 否 | - | 处理前状态 |
| to_status | varchar | 50 | - | 否 | - | 处理后状态 |
| created_at | datetime | - | - | 否 | - | 处理时间 |

### 表名: lawyer_settlement (律师结算表)

| 列名 | 数据类型 | 长度 | 小数点 | 是否可空 | 键 | 注释 |
| ---- | -------- | ---- | ------ | -------- | -- | ---- |
| id | bigint | - | - | 否 | 主键 | 结算ID |
| order_id | bigint | - | - | 否 | 唯一键/外键 | 订单ID |
| lawyer_id | bigint | - | - | 否 | 外键 | 律师ID |
| amount | decimal | 10 | 2 | 否 | - | 结算金额 |
| status | tinyint | - | - | 否 | - | 状态：0待结算，1已结算 |
| settled_at | datetime | - | - | 可 | - | 结算时间 |
| created_at | datetime | - | - | 否 | - | 创建时间 |

### 表名: evaluation (服务评价表)

| 列名 | 数据类型 | 长度 | 小数点 | 是否可空 | 键 | 注释 |
| ---- | -------- | ---- | ------ | -------- | -- | ---- |
| evaluation_id | bigint | - | - | 否 | 主键 | 评价唯一ID |
| order_id | bigint | - | - | 否 | 唯一键/外键 | 关联订单ID |
| user_id | bigint | - | - | 否 | 外键 | 评价者用户ID |
| lawyer_id | bigint | - | - | 否 | 外键 | 被评价律师ID |
| professional_score | tinyint | - | - | 否 | - | 专业度评分1-5分 |
| responsiveness_score | tinyint | - | - | 否 | - | 响应速度评分1-5分 |
| attitude_score | tinyint | - | - | 否 | - | 服务态度评分1-5分 |
| total_score | decimal | 2 | 1 | 否 | - | 综合评分 |
| content | varchar | 500 | - | 可 | - | 评价文字内容 |
| is_anonymous | boolean | - | - | 可 | - | 是否匿名评价 |
| status | enum | - | - | 可 | - | normal、hidden、deleted、pending |
| report_count | int | - | - | 可 | - | 被举报次数 |
| is_featured | boolean | - | - | 可 | - | 是否精选评价 |
| created_time | datetime | - | - | 可 | - | 创建时间 |
| updated_time | datetime | - | - | 可 | - | 修改时间 |

### 表名: evaluation_report (评价举报表)

| 列名 | 数据类型 | 长度 | 小数点 | 是否可空 | 键 | 注释 |
| ---- | -------- | ---- | ------ | -------- | -- | ---- |
| report_id | bigint | - | - | 否 | 主键 | 举报记录ID |
| evaluation_id | bigint | - | - | 否 | 外键 | 被举报评价ID |
| reporter_id | bigint | - | - | 否 | 外键 | 举报人用户ID |
| reason | enum | - | - | 否 | - | abuse、false、ad、privacy |
| description | varchar | 300 | - | 可 | - | 举报详细说明 |
| status | enum | - | - | 可 | - | pending、approved、rejected |
| created_time | datetime | - | - | 可 | - | 举报时间 |

### 表名: admin_evaluation_log (管理员评价处理表)

| 列名 | 数据类型 | 长度 | 小数点 | 是否可空 | 键 | 注释 |
| ---- | -------- | ---- | ------ | -------- | -- | ---- |
| log_id | bigint | - | - | 否 | 主键 | 管理记录ID |
| evaluation_id | bigint | - | - | 否 | 外键 | 被处理评价ID |
| admin_id | bigint | - | - | 否 | 外键 | 管理员ID |
| action_type | enum | - | - | 否 | - | hide、delete、restore、feature、unfeature、reject_report |
| reason | varchar | 300 | - | 可 | - | 操作原因说明 |
| created_time | datetime | - | - | 可 | - | 操作时间 |

## 地区基础数据模块

### 表名: region (全国省市地区表)

| 列名 | 数据类型 | 长度 | 小数点 | 是否可空 | 键 | 注释 |
| ---- | -------- | ---- | ------ | -------- | -- | ---- |
| region_id | bigint | - | - | 否 | 主键 | 地区ID |
| region_name | varchar | 50 | - | 否 | - | 地区名称 |
| parent_id | bigint | - | - | 可 | - | 父级ID，省级为NULL |
| region_level | tinyint | - | - | 否 | - | 层级：1省级，2市级 |
| sort_order | int | - | - | 可 | - | 排序 |
| status | tinyint | - | - | 可 | - | 状态：1启用，0禁用 |
| created_time | datetime | - | - | 可 | - | 创建时间 |
