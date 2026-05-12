# lvtu-server 开发规范文档

## 项目概述

**lvtu-server** 是一个基于 Spring Boot 3 + MyBatis 的后端应用，为律师服务平台提供 REST API 接口。

### 技术栈
- **框架**: Spring Boot 3.3.5
- **ORM/数据持久化**: MyBatis
- **数据库**: MySQL 8.0+
- **构建工具**: Maven
- **Java 版本**: JDK 17+
- **前端集成**: Vaadin 24.10.1 (Web 前端框架)

---

## 项目结构与规范

### 根目录文件说明

| 文件/文件夹 | 作用 | 说明 |
|-----------|------|------|
| `pom.xml` | Maven 配置 | 定义项目元数据、依赖版本、构建配置 |
| `mvnw` / `mvnw.cmd` | Maven Wrapper | 使用特定 Maven 版本，无需本地安装 |
| `README.md` | 项目说明 | 项目简介与快速开始指南 |
| `.gitignore` | Git 忽略 | 定义不提交到版本控制的文件 |
| `.github/` | GitHub 配置 | GitHub Actions、workflow 等 |
| `.idea/` | IDE 配置 | IntelliJ IDEA 项目配置 |
| `.mvn/` | Maven Wrapper 配置 | Maven 版本管理 |
| `target/` | 编译输出 | Maven 构建输出目录，不提交到版本控制 |

---

## 文件夹结构详解

### `/sql` - 数据库脚本

**作用**: 存放数据库初始化和迁移脚本

**文件命名规范**:
- **格式**: `[序号]_[描述].sql` (如 `01_database.sql`, `02_tables.sql`)
- **序号**: 两位数字，表示执行顺序
- **描述**: 小写英文，用下划线分隔

**内容分类**:
```
sql/
  ├── 01_database.sql         # 创建数据库
  ├── 02_schema.sql           # 创建表结构
  ├── 03_indexes.sql          # 创建索引
  ├── 04_business.sql         # 初始业务数据
  ├── 05_procedures.sql       # 存储过程 (如需要)
  └── migration/              # 迁移脚本
      ├── 001_add_column.sql
      └── 002_rename_table.sql
```

**示例** (`01_database.sql`):
```sql
-- 创建数据库
CREATE DATABASE IF NOT EXISTS lvtu_db DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE lvtu_db;
```

---

### `/src/main` - 主程序源代码

#### `/src/main/java/com/bitzh` - Java 源代码根

**包名规范**:
- **格式**: `com.bitzh.[项目名].[模块名]`
- **示例**: `com.bitzh.lvtu.lawyer`, `com.bitzh.lvtu.auth`, `com.bitzh.lvtu.user`

##### 主要目录结构

```
src/main/java/com/bitzh/lvtu/
├── LvtuApplication.java       # Spring Boot 主应用程序类
│
├── controller/                # 控制层 (HTTP 请求处理)
│   ├── LawyerController.java
│   ├── CaseController.java
│   ├── UserController.java
│   └── ...
│
├── service/                   # 业务逻辑层
│   ├── LawyerService.java
│   ├── CaseService.java
│   ├── UserService.java
│   ├── impl/                  # 服务实现类
│   │   ├── LawyerServiceImpl.java
│   │   ├── CaseServiceImpl.java
│   │   └── UserServiceImpl.java
│   └── ...
│
├── mapper/                    # MyBatis 数据访问层
│   ├── LawyerMapper.java
│   ├── CaseMapper.java
│   ├── UserMapper.java
│   └── ...
│
├── entity/                    # 数据模型 (PO/DO)
│   ├── Lawyer.java           # 律师实体
│   ├── Case.java             # 案件实体
│   ├── User.java             # 用户实体
│   └── ...
│
├── dto/                       # 数据传输对象
│   ├── LawyerDTO.java        # 律师 DTO
│   ├── CaseDTO.java
│   ├── UserDTO.java
│   ├── request/              # 请求 DTO
│   │   ├── LawyerCreateRequest.java
│   │   └── LawyerUpdateRequest.java
│   └── response/             # 响应 DTO
│       ├── LawyerResponse.java
│       └── CaseResponse.java
│
├── exception/                 # 自定义异常
│   ├── BusinessException.java
│   ├── ResourceNotFoundException.java
│   └── ...
│
├── config/                    # 配置类
│   ├── WebConfig.java        # Web 配置 (CORS, 拦截器等)
│   ├── MybatisConfig.java
│   └── ...
│
├── util/                      # 工具类
│   ├── DateUtils.java
│   ├── StringUtils.java
│   ├── JsonUtils.java
│   └── ...
│
├── enums/                     # 枚举类
│   ├── LawyerStatus.java
│   ├── CaseStatus.java
│   └── ...
│
├── constant/                  # 常量类
│   ├── AppConstants.java
│   ├── ErrorCode.java
│   └── ...
│
└── vo/                        # 视图对象 (可选)
    └── PageVO.java
```

---

## 各层详细说明

### 1. Controller 层（控制层）

**文件命名**: `[业务模块名]Controller.java` 

**示例**: `LawyerController.java`

**职责**:
- 处理 HTTP 请求和响应
- 参数验证
- 调用 Service 层
- 返回统一格式的响应

**示例代码**:
```java
package com.bitzh.lvtu.controller;

import com.bitzh.lvtu.service.LawyerService;
import com.bitzh.lvtu.dto.LawyerDTO;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lawyers")
public class LawyerController {
    
    private final LawyerService lawyerService;
    
    // 构造函数注入
    public LawyerController(LawyerService lawyerService) {
        this.lawyerService = lawyerService;
    }
    
    // 获取律师列表
    @GetMapping
    public PageVO<LawyerDTO> list(@RequestParam int page, @RequestParam int size) {
        return lawyerService.getLawyerList(page, size);
    }
    
    // 获取律师详情
    @GetMapping("/{id}")
    public LawyerDTO detail(@PathVariable Long id) {
        return lawyerService.getLawyerDetail(id);
    }
    
    // 创建律师
    @PostMapping
    public LawyerDTO create(@RequestBody LawyerCreateRequest request) {
        return lawyerService.createLawyer(request);
    }
}
```

---

### 2. Service 层（业务逻辑层）

**文件命名**: 
- 接口: `[业务模块名]Service.java`
- 实现类: `[业务模块名]ServiceImpl.java`

**示例**: `LawyerService.java`, `LawyerServiceImpl.java`

**职责**:
- 实现核心业务逻辑
- 事务管理
- 调用 Mapper 层
- 业务验证和异常处理


---

### 3. Mapper 层（数据访问层）

**文件命名**: `[业务模块名]Mapper.java`

**对应 XML**: `/src/main/resources/mapper/[业务模块名]Mapper.xml`

**示例**: `LawyerMapper.java`, `LawyerMapper.xml`

**职责**:
- 定义数据库操作接口
- SQL 注解或 XML 映射
- CRUD 操作




---

### 4. Entity 层（数据模型）

**文件命名**: `[业务模块名].java` (通常为单数)

**示例**: `Lawyer.java`, `Case.java`

**职责**:
- 对应数据库表的 Java 类
- 持久化对象 (PO)
- 包含数据库字段和 ORM 注解



---

### 5. DTO 层（数据传输对象）

**文件命名**:
- 通用 DTO: `[业务模块名]DTO.java`
- 请求 DTO: `[业务模块名]CreateRequest.java` / `[业务模块名]UpdateRequest.java`
- 响应 DTO: `[业务模块名]Response.java`

**示例**: `LawyerDTO.java`, `LawyerCreateRequest.java`

**职责**:
- 用于 HTTP 请求/响应的数据容器
- 数据验证注解
- 不同接口的数据需求映射



---

### 6. Exception 层（异常处理）

**文件命名**: `[异常名]Exception.java`

**示例**: `BusinessException.java`, `ResourceNotFoundException.java`



---

### 7. Config 层（配置）

**文件命名**: `[功能名]Config.java`

**示例**: `WebConfig.java`, `MybatisConfig.java`



---

### `/src/main/resources` - 资源文件目录

```
resources/
├── application.properties       # Spring Boot 基础配置
├── application.yml             # Spring Boot YAML 配置 (可选)
├── application-dev.properties  # 开发环境配置
├── application-prod.properties # 生产环境配置
├── mapper/                      # MyBatis XML 映射文件
│   ├── LawyerMapper.xml
│   ├── CaseMapper.xml
│   └── ...
├── static/                      # 静态资源 (前端构建文件)
│   ├── css/
│   ├── js/
│   └── images/
└── templates/                   # Thymeleaf 或其他模板文件
    └── error/
        ├── 404.html
        └── 500.html
```

---

### `/src/test` - 测试代码

**结构**: 镜像 `src/main/java` 的包结构

```
test/java/com/bitzh/lvtu/
├── controller/
│   ├── LawyerControllerTest.java
│   └── ...
├── service/
│   ├── LawyerServiceTest.java
│   └── ...
├── mapper/
│   ├── LawyerMapperTest.java
│   └── ...
└── util/
    └── DateUtilsTest.java
```

**命名规范**: `[被测试类名]Test.java`



---

## 文件命名规范总结

### Java 文件命名

| 文件类型 | 命名格式 | 示例 | 说明 |
|---------|--------|------|------|
| Controller | `[Module]Controller.java` | `LawyerController.java` | 控制层类 |
| Service 接口 | `[Module]Service.java` | `LawyerService.java` | 服务接口 |
| Service 实现 | `[Module]ServiceImpl.java` | `LawyerServiceImpl.java` | 服务实现类 |
| Mapper 接口 | `[Module]Mapper.java` | `LawyerMapper.java` | 数据访问接口 |
| Entity | `[Module].java` | `Lawyer.java` | 数据实体（单数） |
| DTO | `[Module]DTO.java` | `LawyerDTO.java` | 数据传输对象 |
| Request DTO | `[Module]CreateRequest.java` | `LawyerCreateRequest.java` | 创建请求 |
| Response DTO | `[Module]Response.java` | `LawyerResponse.java` | 响应对象 |
| Exception | `[Name]Exception.java` | `BusinessException.java` | 自定义异常 |
| Config | `[Name]Config.java` | `WebConfig.java` | 配置类 |
| Util | `[Name]Utils.java` | `DateUtils.java` | 工具类 |
| Enum | `[Name].java` | `LawyerStatus.java` | 枚举类 |
| Constant | `[Name]Constant.java` | `AppConstants.java` | 常量类 |
| Test | `[ClassName]Test.java` | `LawyerServiceTest.java` | 测试类 |

### XML 文件命名

| 文件类型 | 命名格式 | 示例 | 位置 |
|---------|--------|------|------|
| MyBatis Mapper XML | `[Module]Mapper.xml` | `LawyerMapper.xml` | `src/main/resources/mapper/` |

### Properties 文件命名

| 文件名 | 作用 |
|-------|------|
| `application.properties` | 默认配置 |
| `application-dev.properties` | 开发环境配置 |
| `application-prod.properties` | 生产环境配置 |
| `application-test.properties` | 测试环境配置 |

---

## 包命名规范

### 标准包名结构
```
com.bitzh.lvtu                  # 根包名
  ├── controller               # 控制层
  ├── service                  # 业务逻辑层
  │   └── impl                 # 服务实现
  ├── mapper                   # 数据访问层
  ├── entity                   # 数据实体
  ├── dto                      # 数据传输对象
  │   ├── request
  │   └── response
  ├── exception                # 异常定义
  ├── config                   # 配置类
  ├── util                     # 工具类
  ├── constant                 # 常量类
  ├── enums                    # 枚举类
  ├── vo                       # 视图对象
  └── interceptor              # 拦截器 (可选)
```

---

## 开发工作流

### 本地开发

```bash
# 使用 Maven Wrapper 运行命令
./mvnw clean install           # 编译并安装到本地仓库
./mvnw spring-boot:run        # 运行应用
./mvnw test                    # 运行测试
./mvnw clean package          # 打包成 JAR
```

### 环境配置

**开发环境** (`application-dev.properties`):
```properties
spring.application.name=lvtu
server.port=8080
spring.datasource.url=jdbc:mysql://localhost:3306/lvtu_db
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
mybatis.mapper-locations=classpath:mapper/*.xml
mybatis.type-aliases-package=com.bitzh.lvtu.entity
```

### 运行应用

```bash
# 激活开发环境配置
java -jar target/lvtu-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
```

---

## 数据库相关

### MyBatis 配置

**pom.xml 依赖**:
```xml
<dependency>
    <groupId>org.mybatis.spring.boot</groupId>
    <artifactId>mybatis-spring-boot-starter</artifactId>
    <version>3.0.5</version>
</dependency>
```

### 数据库初始化

1. 创建数据库：运行 `sql/01_database.sql`
2. 创建表结构：运行 `sql/04_business.sql`（或相应的 schema 文件）
3. 配置数据源：在 `application.properties` 中配置数据库连接

---

## 代码组织原则

1. **分层架构**: 严格遵循 MVC/分层思想
2. **单一责任**: 每个类只做一件事
3. **依赖注入**: 使用 Spring 的 DI 机制
4. **异常处理**: 使用统一的异常处理器
5. **代码复用**: 提取通用逻辑到 util 类
6. **事务管理**: 在 Service 层添加 `@Transactional`
7. **验证**: 在 DTO 上使用 JSR-303 注解进行参数验证

---

## 最佳实践

1. **常数定义**: 使用 Constant 类而不是硬编码
2. **日志记录**: 使用 SLF4J + Logback，避免 System.out.println
3. **异常处理**: 区分业务异常和系统异常，统一处理
4. **CRUD 规范**:
   - 创建: `createXxx()` 或 `addXxx()`
   - 查询: `getXxx()` 或 `findXxx()` 或 `queryXxx()`
   - 更新: `updateXxx()` 或 `modifyXxx()`
   - 删除: `deleteXxx()` 或 `removeXxx()`
5. **查询优化**: 避免 N+1 问题，使用连接查询
6. **分页**: 提供分页查询接口，避免一次查询大量数据
7. **API 版本**: 考虑 API 版本控制（如 `/api/v1/lawyers`）
8. **API 文档**: 使用 Swagger/Knife4j 自动生成 API 文档

---

## 相关资源

- [Spring Boot 官方文档](https://spring.io/projects/spring-boot)
- [MyBatis 官方文档](https://mybatis.org/)
- [Spring 官方文档](https://docs.spring.io/spring-framework/reference/)
- [MySQL 官方文档](https://dev.mysql.com/doc/)
- [Maven 官方文档](https://maven.apache.org/)
