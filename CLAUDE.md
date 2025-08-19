# CLAUDE.md

本文件为Claude Code (claude.ai/code) 在此代码仓库中工作时提供指导。

## Claude rules
1. First think through the problem, read the codebase for relevant files, and write a plan to tasks/todo.md.
2. The plan should have a list of todo items that you can check off as you complete them
3. Before you begin working, check in with me and I will verify the plan.
4. Then, begin working on the todo items, marking them as complete as you go.
5. Please every step of the way just give me a high level explanation of what changes you made
6. Make every task and code change you do as simple as possible. We want to avoid making any massive or complex changes. Every change should impact as little code as possible. Everything is about simplicity.
7. Finally, add a review section to the todo.md file with a summary of the changes you made and any other relevant information.

## 开发命令

### 构建和运行
```bash
# 清理并编译项目
mvn clean compile

# 运行应用程序
mvn spring-boot:run

# 运行测试
mvn test

# 打包应用程序
mvn clean package -DskipTests

# 构建Docker镜像
docker build -t jwt-demo:1.0.0 .

# 使用Docker Compose运行
docker-compose up -d
```

### 数据库设置
- 执行 `src/main/resources/sql/jwt_demo.sql` 初始化数据库
- 在 `application.yml` 中配置数据库连接

## 架构概览

这是一个基于Spring Boot 2.7.18的JWT认证授权系统，实现了完整的RBAC（基于角色的访问控制）模型。

### 核心组件

**安全层**：
- `SecurityConfig`：Spring Security配置，包含JWT认证
- `JwtAuthenticationFilter`：JWT令牌验证的自定义过滤器
- `UserDetailsServiceImpl`：为认证加载用户详情

**认证流程**：
1. 登录请求 → `AuthController.login()` → `AuthService.login()`
2. 通过 `JwtService` 生成JWT令牌（访问令牌 + 刷新令牌）
3. 后续请求通过 `JwtAuthenticationFilter` 过滤
4. 通过 `JwtService.validateAccessToken()` 进行令牌验证

**数据层**：
- **RBAC模型**：用户 ↔ 角色 ↔ 权限关系
- **MyBatis Plus** 用于数据访问，配合XML映射器
- **Redis** 用于令牌存储和登录失败跟踪

**安全特性**：
- BCrypt密码加密
- 登录失败锁定（5次尝试 → 30分钟锁定）
- 登出时的令牌黑名单
- 刷新令牌轮换

### 配置结构

**JWT设置** (`application.yml`)：
- 访问令牌：15分钟过期时间
- 刷新令牌：7天过期时间
- HS256算法，可配置密钥

**数据库架构**：
- `user`：核心用户数据，包含登录失败跟踪
- `role` + `permission`：RBAC权限
- `user_role` + `role_permission`：多对多关系

## 关键文件及其作用

- `AuthService.java`：核心认证逻辑，处理登录/注册
- `JwtService.java`：令牌生成、验证和刷新
- `SecurityConfig.java`：安全链配置和CORS设置
- `JwtAuthenticationFilter.java`：每个请求的令牌提取和验证
- `UserDetailsServiceImpl.java`：连接Spring Security与自定义User实体

## API端点

**公开端点**（无需认证）：
- `/auth/login`, `/auth/register`, `/auth/refresh`
- Swagger UI：`/swagger-ui.html`
- 健康检查：`/actuator/health`

**受保护端点**：
- `/user/profile`：用户信息（USER/ADMIN角色）
- `/user/admin/*`：仅管理员端点

## 环境依赖

- **JDK 8+**
- **MySQL 8.0**（数据库）
- **Redis 6.0+**（令牌存储，会话管理）
- **Maven 3.6+**（构建工具）

## 测试账户（默认）
- 用户名：`admin` / 密码：`admin123`（ADMIN角色）
- 用户名：`user` / 密码：`admin123`（USER角色）

## 常见开发任务

**添加新权限**：
1. 在 `permission` 表中插入数据
2. 通过 `role_permission` 表关联
3. 在控制器方法上添加 `@PreAuthorize` 注解

**扩展User实体**：
1. 修改 `User.java` 实体
2. 更新数据库架构
3. 更新相关DTO和映射器

**自定义JWT声明**：修改 `JwtUtil.createToken()` 方法
- 总是使用中文回答问题