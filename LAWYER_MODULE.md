# 律师接单及业务处理模块说明

## 一、模块概述

本模块对应“律途”法律服务平台中的律师接单及业务处理流程，覆盖从律师浏览待接订单、接单、处理订单、提交服务结果，到用户确认完成后生成律师结算记录的完整业务链路。

本模块后端基于 Spring Boot 实现，核心涉及以下数据表：

- `order`
- `order_service_detail`
- `service_result`
- `service_result_attachment`
- `lawyer_settlement`

## 二、已完成功能

### 1. 律师浏览可接订单

已实现功能：

- 查询 `order` 表中状态为 `待接单` 的订单
- 支持按 `service_type_id` 进行筛选
- 返回字段包括：
  - `orderId`
  - `userId`
  - `serviceTypeId`
  - `totalAmount`
  - `status`
  - `createdTime`
- 已接单订单不会出现在待接列表中

对应接口：

- `GET /api/lawyer/orders/available`

### 2. 律师查看订单详情

已实现功能：

- 根据 `orderId` 查询订单主信息
- 同时查询 `order_service_detail.form_data`
- 当订单不存在时返回错误提示
- 无论订单当前状态是 `待接单`、`处理中`、`待评价` 还是 `已完成`，后端均可返回详情，由前端决定按钮显示逻辑

对应接口：

- `GET /api/lawyer/orders/{orderId}`

### 3. 律师接单

已实现功能：

- 接单前校验：
  - 订单必须存在
  - 订单状态必须为 `待接单`
  - `lawyer_id` 必须为空
- 接单成功后：
  - 更新 `order.lawyer_id`
  - 更新 `order.status = '处理中'`
  - 自动更新 `updated_time`
- 已实现重复接单拦截
- 已实现并发场景下的条件更新控制

对应接口：

- `POST /api/lawyer/orders/{orderId}/accept`

### 4. 律师查看自己已接订单

已实现功能：

- 根据 `lawyerId` 查询当前律师名下订单
- 支持按订单状态筛选
- 当前支持查询状态：
  - `处理中`
  - `待评价`
  - `已完成`
- 返回字段包括：
  - `orderId`
  - `serviceTypeId`
  - `totalAmount`
  - `status`
  - `createdTime`
  - `updatedTime`

对应接口：

- `GET /api/lawyer/orders/my`

### 5. 律师提交服务结果

已实现功能：

- 提交参数包括：
  - `orderId`
  - `lawyerId`
  - `title`
  - `content`
  - `attachments`
- 提交前校验：
  - 订单存在
  - 订单属于当前律师
  - 订单状态必须为 `处理中`
- 提交成功后：
  - 写入 `service_result`
  - 写入 `service_result_attachment`
  - 更新订单状态为 `待评价`
- 整体流程已使用事务控制

对应接口：

- `POST /api/lawyer/orders/{orderId}/result`

### 6. 律师查看已提交服务结果

已实现功能：

- 根据 `orderId` 查询服务结果
- 同时返回附件列表
- 无附件时返回空数组
- 仅该订单的接单律师可以查看结果

对应接口：

- `GET /api/lawyer/orders/{orderId}/result`

### 7. 律师修改服务结果

已实现功能：

- 仅在以下状态允许修改：
  - `0`：已提交
  - `2`：需修改
- 若状态为 `1`（用户已确认），禁止修改
- 支持修改：
  - `title`
  - `content`
  - `attachments`
- 更新时同步刷新 `updated_at`

对应接口：

- `PUT /api/lawyer/results/{resultId}`

### 8. 用户确认完成并生成律师结算记录

已实现功能：

- 当订单从 `待评价` 变为 `已完成` 时：
  - 更新订单状态
  - 更新 `service_result.status = 1`
  - 自动生成 `lawyer_settlement`
- 结算金额取 `order.total_amount`
- 已做重复结算记录校验，避免重复生成

对应接口：

- `POST /api/orders/{orderId}/confirm`

### 9. 管理员查询待结算记录

已实现功能：

- 查询 `lawyer_settlement` 中 `status = 0` 的记录

对应接口：

- `GET /api/settlements/pending`

### 10. 管理员结算给律师

已实现功能：

- 将待结算记录更新为已结算
- 设置 `settled_at` 为当前时间
- 已结算记录不能重复结算

对应接口：

- `PUT /api/settlements/{settlementId}/pay`

## 三、当前实现说明

### 1. 已完成联调验证

以下主链路已完成实际接口联调：

- 待接单查询
- 订单详情查看
- 律师接单
- 我的订单查询
- 提交服务结果
- 查看服务结果
- 修改服务结果
- 用户确认完成
- 生成结算记录
- 管理员结算

### 2. 当前实现中的说明项

- 当前联调阶段，`lawyerId` 和 `userId` 仍通过请求参数或请求体传入
- 后续若接入统一登录鉴权，应改为从登录态中获取当前用户身份
- `formData` 当前返回形式为 JSON 字符串，后续可根据前端需要进一步优化为对象结构

## 四、总结

本模块后端核心业务链路已实现并完成联调验证，已满足律师接单及业务处理模块的主要功能要求，可支持后续前端联调、演示和进一步完善登录鉴权逻辑。
