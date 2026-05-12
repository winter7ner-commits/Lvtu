# Lvtu 项目开发规范指南

## 项目概述

**Lvtu** 是一个全栈律师服务平台，采用前后端分离架构：
- **前端**: Vue 3 + TypeScript + Vite
- **后端**: Spring Boot 3 + MyBatis + MySQL
- **数据库**: MySQL 8.0+

---

## 快速导航

### 📋 模块文档

1. **[lvtu-front 前端开发规范](./lvtu-front/DEVELOPMENT.md)** - Vue 3 项目的文件结构、命名规范和最佳实践
2. **[lvtu-server 后端开发规范](./lvtu-server/DEVELOPMENT.md)** - Spring Boot 项目的分层架构、命名规范和最佳实践

---

## 项目架构图

```
┌─────────────────────────────────────────────────────────────┐
│                        Lvtu 律师服务平台                      │
└─────────────────────────────────────────────────────────────┘
         │
         ├─ 前端应用 (lvtu-front)          后端应用 (lvtu-server)
         │   ├─ Vue 3 页面层                  ├─ Spring Boot
         │   ├─ 组件层                        ├─ REST API
         │   ├─ 状态管理 (Pinia)              ├─ 业务逻辑层
         │   └─ API 调用 (Axios)              ├─ 数据访问层 (MyBatis)
         │                                    └─ 数据库 (MySQL)
         │
         └─ 通信协议: HTTP/REST API (JSON)
```

---

## 核心概念和工作流

### 功能模块划分

项目主要包含以下功能模块：

| 模块 | 描述 | 前端路径 | 后端包名 |
|-----|------|--------|---------|
| **律师管理** | 律师信息管理、查询、筛选 | `views/lawyer/` | `com.bitzh.lvtu.lawyer` |
| **案件管理** | 案件创建、跟进、结案 | `views/case/` | `com.bitzh.lvtu.case` |
| **用户管理** | 用户认证、权限管理 | `views/user/` | `com.bitzh.lvtu.user` |
| **认证授权** | 登录、注册、权限控制 | `views/auth/` | `com.bitzh.lvtu.auth` |

### 前后端交互流程

```
前端                      后端
  │                       │
  ├─ 发起请求 ─ HTTP → ─ Controller
  │  (JSON)              │
  │                      ├─ Service (业务逻辑)
  │                      │
  │                      ├─ Mapper (数据访问)
  │                      │
  │                      └─ Database (MySQL)
  │                      
  ├─ ← 返回响应 ─ HTTP ─ │
  │  (JSON)              │
  └─ 更新 UI             │
```

---

## 统一标准

### 命名规范

#### 后端 (Java)

- **类**: PascalCase (如 `LawyerService.java`)
- **方法**: camelCase (如 `getLawyerList()`)
- **常量**: UPPER_SNAKE_CASE (如 `MAX_PAGE_SIZE`)
- **包名**: 全小写 + 点分隔 (如 `com.bitzh.lvtu.service`)

#### 前端 (JavaScript/TypeScript/Vue)

- **组件**: PascalCase (如 `LawyerCard.vue`)
- **函数**: camelCase (如 `formatDate()`)
- **常量**: UPPER_SNAKE_CASE (如 `API_BASE_URL`)
- **文件**: kebab-case 或 camelCase (如 `user-store.js` 或 `userStore.js`)
- **类/接口**: PascalCase (如 `UserDTO`, `APIResponse`)

### API 接口规范

#### RESTful API 设计

**资源和操作对应**:

| 操作 | HTTP 方法 | URL 示例 | 说明 |
|-----|---------|---------|------|
| 创建 | POST | `/api/lawyers` | 新增律师 |
| 查询列表 | GET | `/api/lawyers?page=1&size=10` | 获取律师列表 |
| 查询单个 | GET | `/api/lawyers/{id}` | 获取律师详情 |
| 更新 | PUT/PATCH | `/api/lawyers/{id}` | 更新律师信息 |
| 删除 | DELETE | `/api/lawyers/{id}` | 删除律师 |

#### API 响应格式

**统一响应体** (JSON):

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "name": "张三",
    "email": "zhangsan@example.com"
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

**分页响应**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 100,
    "page": 1,
    "size": 10,
    "records": [
      { "id": 1, "name": "律师1" },
      { "id": 2, "name": "律师2" }
    ]
  }
}
```

**错误响应**:

```json
{
  "code": 400,
  "message": "参数验证失败",
  "errors": [
    {
      "field": "email",
      "message": "邮箱格式不正确"
    }
  ],
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 错误代码规范

| 代码 | 含义 | 说明 |
|-----|------|------|
| 200 | Success | 请求成功 |
| 400 | Bad Request | 请求参数错误 |
| 401 | Unauthorized | 未授权（需要登录） |
| 403 | Forbidden | 禁止访问（无权限） |
| 404 | Not Found | 资源不存在 |
| 500 | Internal Server Error | 服务器错误 |

---

## 开发流程

### 新功能开发流程

1. **需求分析**
   - 确定功能模块
   - 设计 API 接口
   - 规划数据库表结构

2. **数据库设计**
   - 在 `/sql` 目录创建迁移脚本
   - 遵循命名规范（`[序号]_[描述].sql`）
   - 执行脚本创建表结构

3. **后端开发** (lvtu-server)
   - 创建 Entity 类（对应数据库表）
   - 创建 Mapper 接口和 XML 配置
   - 创建 Service 业务逻辑类
   - 创建 Controller 提供 REST API
   - 编写单元测试

4. **前端开发** (lvtu-front)
   - 创建 API 调用模块
   - 创建页面组件
   - 创建可复用的子组件
   - 集成状态管理（Pinia）
   - 编写功能测试

5. **集成测试**
   - 前后端接口联调
   - 功能完整性测试
   - 错误处理测试

6. **部署**
   - 代码审查
   - 打包构建
   - 部署到测试/生产环境

---

## 代码审查清单

### 后端代码审查

- [ ] 是否遵循分层架构（Controller → Service → Mapper）
- [ ] 是否使用了正确的异常处理
- [ ] 是否添加了 `@Transactional` 事务注解
- [ ] 是否进行了参数验证
- [ ] 是否有适当的日志记录
- [ ] 是否编写了单元测试
- [ ] SQL 查询是否高效（避免 N+1）
- [ ] 是否遵循了命名规范
- [ ] 是否处理了并发问题

### 前端代码审查

- [ ] 是否遵循了组件分层（页面 → 组件 → 子组件）
- [ ] 是否正确处理了 API 响应
- [ ] 是否使用了 TypeScript 类型注解
- [ ] 是否处理了加载和错误状态
- [ ] 是否避免了不必要的重新渲染
- [ ] 是否编写了测试用例
- [ ] CSS 是否模块化（Scoped）
- [ ] 是否遵循了命名规范
- [ ] 是否有对用户的友好提示

---

## 常见任务指南

### 添加新的业务模块

**后端步骤**:
1. 在 `src/main/java/com/bitzh/lvtu/` 下创建新包
2. 创建相应的 Entity、Mapper、Service、Controller
3. 在 `src/main/resources/mapper/` 下创建 XML 配置
4. 在 `sql/` 下创建数据库迁移脚本

**前端步骤**:
1. 在 `src/api/` 下创建 API 模块文件
2. 在 `src/views/` 下创建页面文件夹
3. 创建必要的组件
4. 在 `src/router/index.js` 中添加路由
5. 创建相应的 Pinia store (如需要)

### 修改数据库结构

1. 创建新的 SQL 迁移文件：`sql/[序号]_[描述].sql`
2. 在文件中编写 ALTER TABLE 语句
3. 执行迁移脚本
4. 更新相应的 Entity 类
5. 更新 MyBatis Mapper XML
6. 如需要，更新前后端的 DTO

### 添加新的 API 端点

**后端**:
1. 在 Service 中添加业务方法
2. 在 Controller 中添加请求映射
3. 定义请求和响应 DTO
4. 添加参数验证注解
5. 编写单元测试

**前端**:
1. 在 API 模块中添加调用方法
2. 在页面中调用 API
3. 处理响应和错误
4. 更新 UI 状态

---

## 环境配置

### 开发环境要求

| 工具 | 版本 | 用途 |
|-----|------|------|
| Java | 17+ | 后端开发 |
| Node.js | 18+ | 前端开发 |
| MySQL | 8.0+ | 数据库 |
| Maven | 3.8+ | Java 构建工具 |
| npm | 9+ | 前端包管理 |

### 快速启动

**启动数据库**:
```bash
# MySQL (使用 Docker)
docker run --name mysql8 -e MYSQL_ROOT_PASSWORD=root -p 3306:3306 -d mysql:8.0
```

**启动后端**:
```bash
cd lvtu-server
./mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

**启动前端**:
```bash
cd lvtu-front
npm install
npm run dev
```

访问前端应用: `http://localhost:5173`

访问后端 API: `http://localhost:8080`

---

## 调试技巧

### 后端调试

- 使用 IDE (IntelliJ IDEA) 的断点调试功能
- 查看应用日志：`target/` 目录下的日志文件
- 使用 Postman 或 Insomnia 测试 API
- 启用 MyBatis SQL 日志查看执行的 SQL

### 前端调试

- 使用浏览器开发者工具 (F12)
- 使用 Vue DevTools 浏览器扩展
- 使用 `console.log()` 或专业日志工具
- 使用 Network 标签检查 API 请求

---

## 部署指南

### 后端部署

```bash
# 构建
cd lvtu-server
./mvnw clean package -DskipTests

# 运行 JAR
java -jar target/lvtu-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

### 前端部署

```bash
# 构建
cd lvtu-front
npm run build

# 将 dist 目录部署到静态文件服务器或集成到后端
```

---

## 常见问题 (FAQ)

**Q: 如何添加新的 npm 依赖？**
A: 运行 `npm install package-name`，然后在代码中导入使用。

**Q: 如何解决 CORS 错误？**
A: 在后端 WebConfig 中配置 CORS，或前端改用代理。

**Q: 如何管理敏感信息（如数据库密码）？**
A: 使用环境变量或配置文件，将敏感信息存储在 `.env` 或 `application-prod.properties` 中，不要提交到版本控制。

**Q: 如何处理认证和授权？**
A: 使用 Spring Security 进行认证，通过 JWT 或 Session 管理用户状态。

**Q: 前端如何处理 API 错误？**
A: 在 API 响应拦截器中统一处理，根据状态码显示相应的错误提示。

---

## 联系方式

- **项目经理**: [联系方式]
- **架构师**: [联系方式]
- **技术支持**: [联系方式]

---

## 变更历史

| 版本 | 日期 | 变更说明 |
|-----|------|---------|
| 1.0 | 2024-01-15 | 初始版本，包含前后端开发规范 |

---

## 相关文档

- [lvtu-front 详细开发规范](./lvtu-front/DEVELOPMENT.md)
- [lvtu-server 详细开发规范](./lvtu-server/DEVELOPMENT.md)
- [数据库文档](./sql/README.md) (待创建)
- [API 文档](./docs/API.md) (待创建)
- [部署指南](./docs/DEPLOYMENT.md) (待创建)
