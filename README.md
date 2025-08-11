# Spring Boot 2.7.18 JWT 认证授权完整示例

这是一个基于Spring Boot 2.7.18的JWT认证授权完整实现项目，包含用户注册、登录、权限控制、Token管理等功能。

## 🚀 项目特性

- **完整的JWT认证流程**: Access Token + Refresh Token机制
- **基于角色的权限控制**: RBAC权限模型，支持用户-角色-权限三级关系
- **安全防护**: 密码加密、登录失败锁定、Token黑名单机制
- **Redis缓存**: Token存储、登录失败记录等
- **MyBatis Plus**: 高效的数据访问层
- **全局异常处理**: 统一的错误响应格式
- **API文档**: Swagger自动生成接口文档
- **Docker支持**: 一键部署运行

## 🛠 技术栈

- **后端框架**: Spring Boot 2.7.18
- **安全框架**: Spring Security 5.7.x
- **JWT库**: jjwt-api 0.11.5
- **数据库**: MySQL 8.0
- **ORM框架**: MyBatis Plus 3.5.x
- **缓存**: Redis 7.x
- **构建工具**: Maven
- **容器化**: Docker & Docker Compose

## 📁 项目结构

```
src/main/java/com/example/jwt/
├── config/                 # 配置类
│   ├── SecurityConfig.java     # Spring Security配置
│   └── RedisConfig.java        # Redis配置
├── controller/             # 控制器层
│   ├── AuthController.java     # 认证相关接口
│   └── UserController.java     # 用户管理接口
├── entity/                 # 实体类
│   ├── User.java              # 用户实体
│   ├── Role.java              # 角色实体
│   ├── Permission.java        # 权限实体
│   ├── UserRole.java          # 用户角色关联
│   └── RolePermission.java    # 角色权限关联
├── dto/                    # 数据传输对象
│   ├── LoginRequest.java      # 登录请求
│   ├── LoginResponse.java     # 登录响应
│   ├── RegisterRequest.java   # 注册请求
│   └── ApiResponse.java       # 统一响应格式
├── service/                # 服务层
│   ├── AuthService.java       # 认证服务
│   └── JwtService.java        # JWT服务
├── security/               # 安全相关
│   ├── JwtAuthenticationFilter.java  # JWT认证过滤器
│   ├── UserDetailsServiceImpl.java   # 用户详情服务
│   └── UserDetailsImpl.java          # 用户详情实现
├── mapper/                 # 数据访问层
│   ├── UserMapper.java        # 用户数据访问
│   ├── RoleMapper.java        # 角色数据访问
│   └── PermissionMapper.java  # 权限数据访问
├── exception/              # 异常处理
│   ├── GlobalExceptionHandler.java # 全局异常处理
│   └── CustomException.java        # 自定义异常
├── util/                   # 工具类
│   ├── JwtUtil.java           # JWT工具类
│   ├── RedisUtil.java         # Redis工具类
│   └── ResponseUtil.java      # 响应工具类
└── JwtDemoApplication.java # 主应用类
```

## 🚀 快速开始

### 环境要求

- JDK 8+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+
- Docker (可选)

### 1. 克隆项目

```bash
git clone <repository-url>
cd springboot-jwt-demo
```

### 2. 数据库初始化

执行 `src/main/resources/sql/jwt_demo.sql` 脚本初始化数据库。

### 3. 配置文件

修改 `src/main/resources/application.yml` 中的数据库和Redis连接信息。

### 4. 运行项目

#### 方式一：Maven运行

```bash
mvn clean compile
mvn spring-boot:run
```

#### 方式二：Docker Compose运行

```bash
# 构建并启动所有服务
docker-compose up -d

# 查看日志
docker-compose logs -f jwt-demo-app
```

### 5. 访问应用

- 应用地址: http://localhost:8080/api
- Swagger文档: http://localhost:8080/api/swagger-ui/
- 健康检查: http://localhost:8080/api/actuator/health

## 📚 API接口

### 认证接口

| 接口 | 方法 | 描述 |
|------|------|------|
| `/auth/register` | POST | 用户注册 |
| `/auth/login` | POST | 用户登录 |
| `/auth/refresh` | POST | 刷新Token |
| `/auth/logout` | POST | 用户登出 |

### 用户接口

| 接口 | 方法 | 描述 | 权限要求 |
|------|------|------|----------|
| `/user/profile` | GET | 获取用户信息 | USER/ADMIN |
| `/user/password` | PUT | 修改密码 | USER/ADMIN |
| `/user/admin/users` | GET | 获取所有用户 | ADMIN |

## 🔧 使用示例

### 1. 用户注册

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "Test123456",
    "confirmPassword": "Test123456",
    "email": "test@example.com"
  }'
```

### 2. 用户登录

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

响应示例：
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer",
    "expiresIn": 900000,
    "userInfo": {
      "id": 1,
      "username": "admin"
    }
  },
  "timestamp": 1704067200000
}
```

### 3. 访问受保护的接口

```bash
curl -X GET http://localhost:8080/api/user/profile \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

### 4. 刷新Token

```bash
curl -X POST http://localhost:8080/api/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
  }'
```

## 🔐 安全特性

### JWT配置
- **算法**: HS256
- **Access Token过期时间**: 15分钟
- **Refresh Token过期时间**: 7天
- **Token传递方式**: HTTP Header `Authorization: Bearer <token>`

### 安全防护
- **密码加密**: BCrypt算法
- **登录失败锁定**: 5次失败后锁定30分钟
- **Token黑名单**: 支持主动登出，Token加入黑名单
- **CORS支持**: 跨域资源共享配置
- **参数验证**: 请求参数自动验证

### 权限控制
- **RBAC模型**: 用户-角色-权限三级关系
- **方法级权限**: `@PreAuthorize`注解支持
- **URL级权限**: Spring Security配置

## 🧪 测试账户

系统预置了两个测试账户：

| 用户名 | 密码 | 角色 | 权限 |
|--------|------|------|------|
| admin | admin123 | ADMIN | 所有权限 |
| user | admin123 | USER | 基础用户权限 |

## 📊 监控和日志

### 健康检查
- 端点: `/actuator/health`
- 支持数据库、Redis连接状态检查

### 日志配置
- 日志文件: `logs/jwt-demo.log`
- 日志级别: DEBUG(开发), INFO(生产)
- 日志格式: 包含时间戳、线程、级别、类名等信息

## 🐳 Docker部署

### 构建镜像

```bash
# 构建项目
mvn clean package -DskipTests

# 构建Docker镜像
docker build -t jwt-demo:1.0.0 .
```

### Docker Compose部署

```bash
# 启动所有服务
docker-compose up -d

# 停止所有服务
docker-compose down

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f jwt-demo-app
```

## 🛠 开发指南

### 添加新的权限

1. 在`permission`表中插入新权限
2. 在`role_permission`表中关联角色和权限
3. 在Controller方法上添加`@PreAuthorize`注解

### 自定义JWT Claims

在`JwtUtil.java`中的`createToken`方法中添加自定义claims：

```java
claims.put("customField", "customValue");
```

### 扩展用户字段

1. 修改`User`实体类
2. 更新数据库表结构
3. 修改相关的DTO和Mapper

## ❓ 常见问题

### Q: Token过期后如何处理？
A: 客户端收到401错误后，使用refresh token调用`/auth/refresh`接口获取新的access token。

### Q: 如何实现单设备登录？
A: 在登录时清除该用户之前的refresh token，或在Redis中维护用户的唯一session。

### Q: 如何添加图片验证码？
A: 可以集成Google Kaptcha，在登录接口中增加验证码验证逻辑。

### Q: 如何实现密码复杂度验证？
A: 在`RegisterRequest`的`@Pattern`注解中修改正则表达式，或实现自定义验证器。

## 📝 更新日志

### v1.0.0 (2024-01-01)
- ✨ 初始版本发布
- ✅ 完整的JWT认证授权功能
- ✅ RBAC权限控制模型
- ✅ Redis缓存支持
- ✅ Docker容器化支持
- ✅ Swagger API文档

## 📄 许可证

本项目采用 [MIT License](LICENSE) 许可证。

## 🤝 贡献指南

欢迎提交Issue和Pull Request来改进这个项目！

1. Fork 本项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开Pull Request

## 📞 联系方式

如有问题或建议，请通过以下方式联系：

- 邮箱: example@example.com
- GitHub Issues: [项目Issues页面]

---

⭐ 如果这个项目对您有帮助，请给个Star支持一下！