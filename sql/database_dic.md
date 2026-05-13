# 数据库字典

## 1. `users` (用户基本信息表)
| 列名 | 数据类型 | 长度 | 小数点 | 是否可空 | 键 | 注释 |
| --- | --- | --- | --- | --- | --- | --- |
| user_id | bigint | - | - | 否 | 主键 | 主键ID |
| username | varchar | 50 | - | 否 | 唯一键, 索引 | 用户名 |
| password_hash | varchar | 255 | - | 否 | - | 加密后的密码 |
| phone | varchar | 20 | - | 是 | 唯一键, 索引 | 手机号 |
| email | varchar | 100 | - | 是 | 唯一键 | 电子邮箱 |
| avatar_url | varchar | 255 | - | 是 | - | 头像地址 |
| user_type | tinyint | - | - | 否 | - | 用户类型：1.普通用户，2.律师，3.管理员 |
| status | tinyint | - | - | 否 | - | 用户状态：0.冻结，1.正常，2.封禁 |
| is_verified | boolean | - | - | 是 | - | 是否实名认证 |
| auth_status | tinyint | - | - | 是 | - | 律师认证状态：0.未申请，1.审核中，2.通过，3.拒绝 |
| created_time | timestamp | - | - | 是 | - | 注册时间 |
| updated_time | timestamp | - | - | 是 | - | 更新时间 |
| region | varchar | 255 | - | 是 | - | 所在地区 |

## 2. `users_verfications` (用户实名认证信息表)
| 列名 | 数据类型 | 长度 | 小数点 | 是否可空 | 键 | 注释 |
| --- | --- | --- | --- | --- | --- | --- |
| verification_id | bigint | - | - | 否 | 主键 | 实名认证记录ID |
| user_id | bigint | - | - | 否 | 唯一键, 外键 | 关联用户ID |
| real_name | varchar | 100 | - | 否 | - | 真实姓名 |
| id_card_number | varchar | 100 | - | 否 | - | 身份证号（加密存储） |
| id_card_front_url | varchar | 255 | - | 是 | - | 身份证正面照URL |
| id_card_back_url | varchar | 255 | - | 是 | - | 身份证反面照URL |
| verification_status | tinyint | - | - | 否 | - | 认证状态：0.待审核，1.已通过，2.被拒绝 |
| reject_reason | varchar | 255 | - | 是 | - | 审核拒绝原因 |
| reviewer_id | bigint | - | - | 是 | 唯一键 | 审核管理员ID |
| reviewed_time | datetime | - | - | 是 | - | 审核时间 |
| created_time | timestamp | - | - | 是 | - | 认证提交时间 |
| updated_time | timestamp | - | - | 是 | - | 更新时间 |

## 3. `legal_category` (法规分类表)
| 列名 | 数据类型 | 长度 | 小数点 | 是否可空 | 键 | 注释 |
| --- | --- | --- | --- | --- | --- | --- |
| id | bigint | - | - | 否 | 主键 | 分类ID |
| name | varchar | 50 | - | 否 | - | 分类名称，如民法、行政法、刑法 |
| parent_id | bigint | - | - | 否 | - | 父分类ID，0表示一级分类 |
| sort_order | int | - | - | 否 | - | 排序号 |
| status | tinyint | - | - | 否 | - | 状态: 1启用, 0禁用 |
| created_at | datetime | - | - | 否 | - | 创建时间 |
| updated_at | datetime | - | - | 否 | - | 更新时间 |

## 4. `legal_document` (法规文件表)
| 列名 | 数据类型 | 长度 | 小数点 | 是否可空 | 键 | 注释 |
| --- | --- | --- | --- | --- | --- | --- |
| id | bigint | - | - | 否 | 主键 | 法规文件ID |
| category_id | bigint | - | - | 否 | 外键 | 所属法规分类ID |
| title | varchar | 200 | - | 否 | - | 法规文件名称，如中华人民共和国民法典 |
| short_title | varchar | 100 | - | 是 | - | 简称，如民法典 |
| issuing_authority | varchar | 100 | - | 是 | - | 发布机关 |
| publish_date | date | - | - | 是 | - | 发布日期 |
| effective_date | date | - | - | 是 | - | 生效日期 |
| status | tinyint | - | - | 否 | - | 状态: 1现行有效, 0失效 |
| sort_order | int | - | - | 否 | - | 排序号 |
| created_at | datetime | - | - | 否 | - | 创建时间 |
| updated_at | datetime | - | - | 否 | - | 更新时间 |

## 5. `legal_article` (法条表)
| 列名 | 数据类型 | 长度 | 小数点 | 是否可空 | 键 | 注释 |
| --- | --- | --- | --- | --- | --- | --- |
| id | bigint | - | - | 否 | 主键 | 法条ID |
| document_id | bigint | - | - | 否 | 外键 | 所属法规文件ID |
| article_no | varchar | 50 | - | 否 | - | 条文编号，如第一条、第二条 |
| chapter_title | varchar | 100 | - | 是 | - | 章节标题，如第一章基本规定 |
| content | text | - | - | 否 | - | 条文内容 |
| sort_order | int | - | - | 否 | - | 条文排序 |
| created_at | datetime | - | - | 否 | - | 创建时间 |
| updated_at | datetime | - | - | 否 | - | 更新时间 |

## 6. `application` (律师认证申请表)
| 列名 | 数据类型 | 长度 | 小数点 | 是否可空 | 键 | 注释 |
| --- | --- | --- | --- | --- | --- | --- |
| application_id | bigint | - | - | 否 | 主键 | 主键ID |
| user_id | bigint | - | - | 否 | 外键 | 申请人用户ID |
| license_no | varchar | 64 | - | 否 | - | 律师执业证号 |
| license_url | varchar | 512 | - | 否 | - | 执业证照片URL |
| status | tinyint | - | - | 否 | - | 状态: 0待审核, 1通过, 2驳回 |
| audit_time | datetime | - | - | 是 | - | 审核时间 |
| reject_reason | varchar | 512 | - | 是 | - | 驳回原因 |
| create_time | datetime | - | - | 否 | - | 申请时间 |
| apply_type | tinyint | - | - | 否 | - | 0-首次入驻, 1-资料变更 |
| law_firm | varchar | 128 | - | 否 | - | 所属律所 |
| practice_start_year | int | - | - | 否 | - | 执业开始年份 |

## 7. `lawyer` (律师信息表)
| 列名 | 数据类型 | 长度 | 小数点 | 是否可空 | 键 | 注释 |
| --- | --- | --- | --- | --- | --- | --- |
| lawyer_id | bigint | - | - | 否 | 主键 | 主键ID |
| user_id | bigint | - | - | 否 | 唯一键, 外键 | 关联用户ID |
| license_no | varchar | 64 | - | 否 | 唯一键 | 律师执业证号 |
| law_firm | varchar | 128 | - | 否 | - | 所属律所 |
| bio | text | - | - | 是 | - | 个人简介 |
| status | tinyint | - | - | 否 | - | 用户状态: 0.冻结, 1.正常, 2.待审核 |
| create_time | datetime | - | - | 否 | - | 创建时间 |
| practice_years | int | - | - | 是 | - | 执业年限 |
| rating | decimal | - | 1 | 是 | - | 评分 |

## 8. `specialty` (法律擅长领域表)
| 列名 | 数据类型 | 长度 | 小数点 | 是否可空 | 键 | 注释 |
| --- | --- | --- | --- | --- | --- | --- |
| specialty_id | int | - | - | 否 | 主键 | 领域ID |
| name | varchar | 50 | - | 否 | 唯一键 | 领域名称 |

## 9. `lawyer_specialty` (律师与擅长领域关联表)
| 列名 | 数据类型 | 长度 | 小数点 | 是否可空 | 键 | 注释 |
| --- | --- | --- | --- | --- | --- | --- |
| id | bigint | - | - | 否 | 主键 | 主键ID |
| lawyer_id | bigint | - | - | 否 | 唯一复合键, 索引, 外键 | 律师ID |
| specialty_id | int | - | - | 否 | 唯一复合键, 索引, 外键 | 领域ID |

## 10. `service_type` (服务类型表)
| 列名 | 数据类型 | 长度 | 小数点 | 是否可空 | 键 | 注释 |
| --- | --- | --- | --- | --- | --- | --- |
| service_type_id | int | - | - | 否 | 主键 | 服务类型ID |
| name | varchar | 50 | - | 否 | - | 服务名称 |
| description | varchar | 255 | - | 是 | - | 服务描述 |

## 11. `order` (订单表)
| 列名 | 数据类型 | 长度 | 小数点 | 是否可空 | 键 | 注释 |
| --- | --- | --- | --- | --- | --- | --- |
| order_id | bigint | - | - | 否 | 主键 | 订单ID |
| user_id | bigint | - | - | 否 | 外键 | 用户ID |
| lawyer_id | bigint | - | - | 是 | 外键 | 律师ID |
| service_type_id | int | - | - | 否 | 外键 | 服务类型ID |
| total_amount | decimal | - | 2 | 是 | - | 订单总金额 |
| status | varchar | 20 | - | 否 | - | 订单状态 |
| created_time | datetime | - | - | 否 | - | 创建时间 |
| updated_time | datetime | - | - | 否 | - | 更新时间 |

## 12. `order_service_detail` (订单服务详情表)
| 列名 | 数据类型 | 长度 | 小数点 | 是否可空 | 键 | 注释 |
| --- | --- | --- | --- | --- | --- | --- |
| detail_id | bigint | - | - | 否 | 主键 | 详情ID |
| order_id | bigint | - | - | 否 | 外键 | 订单ID |
| form_data | json | - | - | 否 | - | 表单数据 |

## 13. `payment` (支付表)
| 列名 | 数据类型 | 长度 | 小数点 | 是否可空 | 键 | 注释 |
| --- | --- | --- | --- | --- | --- | --- |
| payment_id | bigint | - | - | 否 | 主键 | 支付ID |
| order_id | bigint | - | - | 否 | 外键 | 订单ID |
| amount | decimal | - | 2 | 否 | - | 支付金额 |
| status | varchar | 20 | - | 是 | - | 支付状态 |
| create_time | datetime | - | - | 否 | - | 支付创建时间 |
| payment_time | datetime | - | - | 是 | - | 支付时间 |

## 14. `service_result` (服务结果表)
| 列名 | 数据类型 | 长度 | 小数点 | 是否可空 | 键 | 注释 |
| --- | --- | --- | --- | --- | --- | --- |
| id | bigint | - | - | 否 | 主键 | 服务结果ID |
| order_id | bigint | - | - | 否 | 外键 | 订单ID |
| lawyer_id | bigint | - | - | 否 | 外键 | 提交结果的律师ID |
| title | varchar | 100 | - | 否 | - | 服务结果标题 |
| content | text | - | - | 是 | - | 服务结果说明 |
| status | tinyint | - | - | 否 | - | 状态：0已提交，1用户确认，2需修改 |
| created_at | datetime | - | - | 否 | - | 提交时间 |
| updated_at | datetime | - | - | 否 | - | 更新时间 |

## 15. `service_result_attachment` (服务结果附件表)
| 列名 | 数据类型 | 长度 | 小数点 | 是否可空 | 键 | 注释 |
| --- | --- | --- | --- | --- | --- | --- |
| id | bigint | - | - | 否 | 主键 | 附件ID |
| result_id | bigint | - | - | 否 | 外键 | 服务结果ID |
| file_name | varchar | 100 | - | 否 | - | 文件名 |
| file_url | varchar | 255 | - | 否 | - | 文件路径 |
| file_type | varchar | 255 | - | 是 | - | 文件类型 |
| created_at | datetime | - | - | 否 | - | 上传时间 |

## 16. `lawyer_settlement` (律师结算表)
| 列名 | 数据类型 | 长度 | 小数点 | 是否可空 | 键 | 注释 |
| --- | --- | --- | --- | --- | --- | --- |
| id | bigint | - | - | 否 | 主键 | 结算ID |
| order_id | bigint | - | - | 否 | 唯一键, 外键 | 订单ID |
| lawyer_id | bigint | - | - | 否 | 外键 | 律师ID |
| amount | decimal | - | 2 | 否 | - | 结算金额 |
| status | tinyint | - | - | 否 | - | 状态：0待结算，1已结算 |
| settled_at | datetime | - | - | 是 | - | 结算时间 |
| created_at | datetime | - | - | 否 | - | 创建时间 |

## 17. `evaluation` (服务评价表)
| 列名 | 数据类型 | 长度 | 小数点 | 是否可空 | 键 | 注释 |
| --- | --- | --- | --- | --- | --- | --- |
| evaluation_id | bigint | - | - | 否 | 主键 | 评价唯一ID |
| order_id | bigint | - | - | 否 | 唯一键, 索引, 外键 | 关联订单ID |
| user_id | bigint | - | - | 否 | 外键 | 评价者用户ID |
| lawyer_id | bigint | - | - | 否 | 索引, 外键 | 被评价律师ID |
| professional_score | tinyint | - | - | 否 | - | 专业度评分(1-5分) |
| responsiveness_score | tinyint | - | - | 否 | - | 响应速度评分(1-5分) |
| attitude_score | tinyint | - | - | 否 | - | 服务态度评分(1-5分) |
| total_score | decimal | - | 1 | 否 | - | 综合评分 |
| content | varchar | 500 | - | 是 | - | 评价文字内容（选填） |
| is_anonymous | boolean | - | - | 是 | - | 是否匿名评价 |
| status | enum | - | - | 是 | 索引 | 状态 |
| report_count | int | - | - | 是 | - | 被举报次数 |
| is_featured | boolean | - | - | 是 | 索引 | 是否精选评价 |
| created_time | datetime | - | - | 是 | - | 创建时间 |
| updated_time | datetime | - | - | 是 | - | 修改时间 |

## 18. `evaluation_report` (评价举报表)
| 列名 | 数据类型 | 长度 | 小数点 | 是否可空 | 键 | 注释 |
| --- | --- | --- | --- | --- | --- | --- |
| report_id | bigint | - | - | 否 | 主键 | 举报记录ID |
| evaluation_id | bigint | - | - | 否 | 索引, 外键 | 被举报评价ID |
| reporter_id | bigint | - | - | 否 | 外键 | 举报人用户ID |
| reason | enum | - | - | 否 | - | 举报原因 |
| description | varchar | 300 | - | 是 | - | 举报详细说明 |
| status | enum | - | - | 是 | 索引 | 处理状态 |
| created_time | datetime | - | - | 是 | 索引 | 举报时间 |

## 19. `admin_evaluation_log` (管理员评价处理表)
| 列名 | 数据类型 | 长度 | 小数点 | 是否可空 | 键 | 注释 |
| --- | --- | --- | --- | --- | --- | --- |
| log_id | bigint | - | - | 否 | 主键 | 管理记录ID |
| evaluation_id | bigint | - | - | 否 | 索引, 外键 | 被处理评价ID |
| admin_id | bigint | - | - | 否 | 外键 | 管理员ID |
| action_type | enum | - | - | 否 | 索引 | 操作类型 |
| reason | varchar | 300 | - | 是 | - | 操作原因说明 |
| created_time | datetime | - | - | 是 | 索引 | 操作时间 |

## 20. `region` (全国省市地区表)
| 列名 | 数据类型 | 长度 | 小数点 | 是否可空 | 键 | 注释 |
| --- | --- | --- | --- | --- | --- | --- |
| region_id | bigint | - | - | 否 | 主键 | 地区ID |
| region_name | varchar | 50 | - | 否 | - | 地区名称 |
| parent_id | bigint | - | - | 是 | - | 父级ID，省级为NULL |
| region_level | tinyint | - | - | 否 | - | 层级：1省级 2市级（完整全国城市版） |
| sort_order | int | - | - | 是 | - | 排序 |
| status | tinyint | - | - | 是 | - | 状态：1启用 0禁用 |
| created_time | datetime | - | - | 是 | - | 创建时间 |
