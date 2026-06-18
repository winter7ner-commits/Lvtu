# Lvtu 自动化测试

## 1. 基础验证

```powershell
cd C:\Users\yuzukii\Desktop\Lvtu\lvtu-server
mvn test

cd C:\Users\yuzukii\Desktop\Lvtu\lvtu-front
npm run build

cd C:\Users\yuzukii\Desktop\Lvtu\lvtu-master-front
npm run build
```

## 2. 接口契约测试

不需要启动数据库，用来确认每个功能点的前后端 API 路径都有后端接口承接。

```powershell
cd C:\Users\yuzukii\Desktop\Lvtu
node --test .\qa\api-contract.test.mjs
```

## 3. 业务闭环冒烟测试

需要先启动 MySQL 并执行根目录 `sql` 下的建表和测试数据脚本，然后启动后端。
当前根目录 SQL 默认创建并使用数据库 `lvtu`，与 `lvtu-server/src/main/resources/application.yml` 一致：

```powershell
cd C:\Users\yuzukii\Desktop\Lvtu\lvtu-server
mvn spring-boot:run
```

如果 8080 已被占用，可以临时指定端口：

```powershell
mvn spring-boot:run "-Dspring-boot.run.arguments=--server.port=18080"
$env:LVTU_API_BASE="http://localhost:18080"
```

另开终端执行：

```powershell
cd C:\Users\yuzukii\Desktop\Lvtu
node .\qa\business-flow-smoke.mjs
```

该脚本会创建真实测试订单、支付记录、服务结果、评价和结算记录，不会自动清理数据；建议只在测试库运行。

默认测试账号来自 `sql/02_users.sql`：

| 角色 | 默认账号 | 默认 ID |
| --- | --- | --- |
| 普通用户 | `user04` | `500001` |
| 律师 | `lawyA` | `500003` / 律师ID `700001` |
| 超级管理员 | `admin_super` | `500005` |

可用环境变量覆盖：

```powershell
$env:LVTU_API_BASE="http://localhost:8080"
$env:LVTU_TEST_USER="user04"
$env:LVTU_TEST_LAWYER="lawyA"
$env:LVTU_TEST_ADMIN="admin_super"
$env:LVTU_TEST_PASSWORD="Test123456"
node .\qa\business-flow-smoke.mjs
```
