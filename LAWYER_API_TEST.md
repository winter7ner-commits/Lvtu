# 律师接单及业务处理模块接口测试记录

## 一、测试环境

- 项目：律途法律服务平台
- 模块：律师接单及业务处理模块
- 后端：Spring Boot
- 数据库：MySQL
- JDK：17
- 测试方式：终端使用 `curl` 调用接口
- 服务地址：`http://localhost:8080/api`

## 二、测试前提

测试前已完成以下准备：

- MySQL 数据库 `lvtu` 已建立
- `01` 到 `07` SQL 已按顺序执行
- 后端已成功启动
- 测试数据已成功写入

测试过程中使用的核心测试数据：

- 律师：
  - `700001`
  - `700002`
- 用户：
  - `500001`
  - `500002`
- 订单：
  - `800001`
  - `800002`
  - `800003`
  - `800004`
  - `800005`

## 三、接口测试过程与结果

### 1. 查询可接订单

接口：

```http
GET /api/lawyer/orders/available
```

测试命令：

```bash
curl "http://localhost:8080/api/lawyer/orders/available"
```

测试结果：

- 返回成功
- 返回两个待接单订单：
  - `800002`
  - `800001`

说明：

- 说明后端可正常查询 `待接单` 订单
- 说明数据库连接、接口路由、查询逻辑均正常

### 2. 查看订单详情

接口：

```http
GET /api/lawyer/orders/800001
```

测试命令：

```bash
curl "http://localhost:8080/api/lawyer/orders/800001"
```

测试结果：

- 返回成功
- 查询到订单 `800001`
- 返回了订单主信息
- 返回了 `formData`

说明：

- 说明订单详情查询正常
- 说明 `order_service_detail` 已正确关联查询

### 3. 律师接单

接口：

```http
POST /api/lawyer/orders/800001/accept
```

测试命令：

```bash
curl -X POST "http://localhost:8080/api/lawyer/orders/800001/accept" \
  -H "Content-Type: application/json" \
  -d '{"lawyerId":700001}'
```

测试结果：

- 返回成功
- 消息为“接单成功”

后续验证：

再次查询订单：

```bash
curl "http://localhost:8080/api/lawyer/orders/800001"
```

验证结果：

- `lawyerId` 由 `null` 变为 `700001`
- `status` 由 `待接单` 变为 `处理中`

说明：

- 说明接单逻辑正常
- 说明订单状态更新正常

### 4. 重复接单拦截

接口：

```http
POST /api/lawyer/orders/800001/accept
```

测试命令：

```bash
curl -X POST "http://localhost:8080/api/lawyer/orders/800001/accept" \
  -H "Content-Type: application/json" \
  -d '{"lawyerId":700002}'
```

测试结果：

- 返回失败
- 返回消息：“当前订单状态不允许接单”

说明：

- 说明已完成重复接单拦截
- 接单后其他律师不能再次接同一订单

### 5. 查询律师本人订单

接口：

```http
GET /api/lawyer/orders/my?lawyerId=700001
```

测试命令：

```bash
curl "http://localhost:8080/api/lawyer/orders/my?lawyerId=700001"
```

测试结果：

- 返回成功
- 返回 4 条订单：
  - `800001`
  - `800004`
  - `800003`
  - `800005`

说明：

- 说明查询“我的订单”接口正常

### 6. 按状态筛选本人订单

接口：

```http
GET /api/lawyer/orders/my?lawyerId=700001&status=处理中
```

测试命令：

```bash
curl --get "http://localhost:8080/api/lawyer/orders/my" \
  --data-urlencode "lawyerId=700001" \
  --data-urlencode "status=处理中"
```

测试结果：

- 返回成功
- 仅返回：
  - `800001`
  - `800003`

说明：

- 说明状态筛选逻辑正常
- 说明中文参数通过 URL 编码后可正常处理

### 7. 提交服务结果

接口：

```http
POST /api/lawyer/orders/800003/result
```

测试命令：

```bash
curl -X POST "http://localhost:8080/api/lawyer/orders/800003/result" \
  -H "Content-Type: application/json" \
  -d '{
    "lawyerId":700001,
    "title":"民间借贷起诉材料初稿",
    "content":"已完成起诉状初稿和证据清单整理。",
    "attachments":[
      {
        "fileName":"起诉状初稿.docx",
        "fileUrl":"/mock/files/complaint-draft.docx",
        "fileType":"application/vnd.openxmlformats-officedocument.wordprocessingml.document"
      }
    ]
  }'
```

测试结果：

- 返回成功
- 消息为“提交服务结果成功”

说明：

- 说明服务结果写入成功
- 说明附件写入成功

### 8. 查看服务结果

接口：

```http
GET /api/lawyer/orders/800003/result?lawyerId=700001
```

测试命令：

```bash
curl "http://localhost:8080/api/lawyer/orders/800003/result?lawyerId=700001"
```

测试结果：

- 返回成功
- 返回结果记录：
  - `id = 900003`
  - `orderId = 800003`
  - `lawyerId = 700001`
  - `title = 民间借贷起诉材料初稿`
- 返回附件列表，包含：
  - `起诉状初稿.docx`

说明：

- 说明结果查询接口正常
- 说明附件一并返回正常

### 9. 提交结果后订单状态变更验证

接口：

```http
GET /api/lawyer/orders/800003
```

测试命令：

```bash
curl "http://localhost:8080/api/lawyer/orders/800003"
```

测试结果：

- 返回成功
- `status = 待评价`

说明：

- 说明提交服务结果后订单状态更新正常

### 10. 非当前律师查看服务结果

接口：

```http
GET /api/lawyer/orders/800003/result?lawyerId=700002
```

测试命令：

```bash
curl "http://localhost:8080/api/lawyer/orders/800003/result?lawyerId=700002"
```

测试结果：

- 返回失败
- 消息为“只有该订单的接单律师可以查看服务结果”

说明：

- 说明结果查看权限控制正常

### 11. 修改服务结果

接口：

```http
PUT /api/lawyer/results/900001
```

测试命令：

```bash
curl -X PUT "http://localhost:8080/api/lawyer/results/900001" \
  -H "Content-Type: application/json" \
  -d '{
    "lawyerId":700001,
    "title":"劳动争议仲裁代理方案-修订版",
    "content":"补充了证据准备说明和开庭注意事项。",
    "attachments":[
      {
        "fileName":"修订版方案.pdf",
        "fileUrl":"/mock/files/updated-plan.pdf",
        "fileType":"application/pdf"
      }
    ]
  }'
```

测试结果：

- 返回成功
- 消息为“修改服务结果成功”

说明：

- 说明允许修改状态下的结果更新正常

### 12. 修改已确认结果

接口：

```http
PUT /api/lawyer/results/900002
```

测试命令：

```bash
curl -X PUT "http://localhost:8080/api/lawyer/results/900002" \
  -H "Content-Type: application/json" \
  -d '{
    "lawyerId":700001,
    "title":"不应成功",
    "content":"不应成功"
  }'
```

测试结果：

- 返回失败
- 消息为“当前服务结果状态不允许修改”

说明：

- 说明状态限制逻辑正常

### 13. 用户确认订单完成

接口：

```http
POST /api/orders/800004/confirm
```

测试命令：

```bash
curl -X POST "http://localhost:8080/api/orders/800004/confirm" \
  -H "Content-Type: application/json" \
  -d '{"userId":500002}'
```

测试结果：

- 返回成功
- 消息为“订单确认完成成功”

说明：

- 说明用户确认完成逻辑正常

### 14. 查询待结算记录

接口：

```http
GET /api/settlements/pending
```

测试命令：

```bash
curl "http://localhost:8080/api/settlements/pending"
```

测试结果：

- 返回成功
- 返回两条待结算记录：
  - `800005`
  - `800004`

其中新增记录：

- `id = 920002`
- `orderId = 800004`
- `lawyerId = 700001`
- `amount = 899.00`
- `status = 0`

说明：

- 说明用户确认后已成功生成律师结算记录

### 15. 管理员结算

接口：

```http
PUT /api/settlements/920001/pay
```

测试命令：

```bash
curl -X PUT "http://localhost:8080/api/settlements/920001/pay"
```

测试结果：

- 返回成功
- 消息为“结算成功”

说明：

- 说明管理员结算逻辑正常

### 16. 重复结算校验

接口：

```http
PUT /api/settlements/920001/pay
```

测试命令：

```bash
curl -X PUT "http://localhost:8080/api/settlements/920001/pay"
```

测试结果：

- 返回失败
- 消息为“该结算记录已结算”

说明：

- 说明已结算记录不能重复结算

## 四、测试结论

本次联调结果表明，律师接单及业务处理模块核心接口已全部跑通，完整业务链路可正常执行，包括：

- 律师浏览待接订单
- 查看订单详情
- 律师接单
- 查看本人订单
- 提交服务结果
- 查看服务结果
- 修改服务结果
- 用户确认完成
- 生成律师结算记录
- 管理员结算

## 五、补充说明

当前测试中有两个后续可优化点：

1. `formData` 当前返回的是 JSON 字符串，后续可优化为对象返回
2. 当前联调阶段 `lawyerId` / `userId` 为显式传参，后续接入登录鉴权后应改为从登录态获取
