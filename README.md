# Score Admin Java Backend

一个基于 Spring Boot 的后台项目基础框架，提供统一响应、全局异常处理、CORS、健康检查、JWT 登录认证与接口文档。

## 快速开始

- 先确保安装了 JDK `17+` 与 Maven `3.8+`
- 构建项目：

```bash
mvn clean package
```

- 运行应用（MySQL 配置）：

```bash
java -jar target/admin-java-0.0.1-SNAPSHOT.jar --spring.profiles.active=mysql
```

- 开发模式（端口默认 `8080`）：

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=mysql
```

## 主要功能

- 统一响应体：`ApiResponse`
- 全局异常处理：`GlobalExceptionHandler`
- CORS 配置：`WebConfig`
- Actuator 健康检查：`/actuator/health`
- 接口文档（SpringDoc）：`/swagger-ui.html`
- 登录认证（JWT）：
  - `POST /api/auth/login`，请求体：`{"username":"admin","password":"admin123"}`
  - 返回：`{"token":"<JWT>","username":"admin"}`
  - `POST /api/auth/register`，请求体：`{"username":"<name>","password":"<pwd>"}`
  - 返回：`{"token":"<JWT>","expiresAt":<timestamp>}`（统一响应体包裹）
- 示例接口：
  - `GET /api/hello` 返回连通性消息
  - `POST /api/hello` 接收 `name` 字段并校验

## 项目结构

```
src
├── main
│   ├── java/com/score/admin
│   │   ├── AdminApplication.java
│   │   ├── common
│   │   │   ├── ApiResponse.java
│   │   │   ├── BusinessException.java
│   │   │   ├── ErrorCode.java
│   │   │   └── GlobalExceptionHandler.java
│   │   ├── config
│   │   │   ├── SecurityConfig.java
│   │   │   └── WebConfig.java
│   │   ├── controller
│   │   │   ├── AuthController.java
│   │   │   └── HelloController.java
│   │   ├── dto
│   │   │   ├── GreetingRequest.java
│   │   │   ├── LoginRequest.java
│   │   │   └── LoginResponse.java
│   │   ├── entity
│   │   │   └── User.java
│   │   ├── repository
│   │   │   └── UserRepository.java
│   │   ├── service
│   │   │   └── UserService.java
│   │   └── util
│   │       ├── JwtAuthenticationFilter.java
│   │       └── JwtUtil.java
│   └── resources
│       ├── application.yml
│       ├── application-mysql.yml
│       └── logback-spring.xml
└── test/java/com/score/admin/AdminApplicationTests.java
```

## 运行与验证

- 认证策略：除 `POST /api/auth/login` 与 `POST /api/auth/register` 外，所有接口均需在请求头携带 `Authorization: Bearer <token>`。
- 健康检查：`GET /actuator/health`（需携带有效 token）
- Swagger UI：`GET /swagger-ui.html`（需携带有效 token）
- 登录并访问受保护接口（PowerShell 示例）：

```powershell
$body = @{ username = 'admin'; password = 'admin123' } | ConvertTo-Json
$resp = Invoke-RestMethod -Method Post -Uri 'http://localhost:8080/api/auth/login' -ContentType 'application/json' -Body $body
$token = $resp.data.token

# 携带 token 访问受保护接口
Invoke-RestMethod -Method Get -Uri 'http://localhost:8080/api/hello' -Headers @{ Authorization = "Bearer $token" } | ConvertTo-Json -Compress
```

- 注册后直接使用返回的 token（PowerShell 示例）：

```powershell
$regBody = @{ username = 'userX'; password = 'test1234' } | ConvertTo-Json
$regResp = Invoke-RestMethod -Method Post -Uri 'http://localhost:8080/api/auth/register' -ContentType 'application/json' -Body $regBody
$regToken = $regResp.data.token
Invoke-RestMethod -Method Get -Uri 'http://localhost:8080/api/hello' -Headers @{ Authorization = "Bearer $regToken" }
```

默认会在启动时初始化管理员账号：`admin/admin123`。如需修改，可在 `UserService` 初始化逻辑或数据库中调整。

## 后续拓展建议

- 按模块拆分 controller/service/repository 分层
- 增强持久化与迁移（Flyway/Liquibase）
- 完善权限模型与角色（RBAC）
- 统一错误码枚举与国际化
- 生产就绪的日志与链路追踪（如 Sleuth/Observability）
