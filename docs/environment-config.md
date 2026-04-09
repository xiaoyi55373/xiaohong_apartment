# 小红公寓claw - 多环境配置指南

## 📋 环境概述

本项目支持三种运行环境：

| 环境 | 配置文件 | 用途 | 数据库 |
|------|---------|------|--------|
| **dev** | `application-dev.yml` | 本地开发环境 | lease (本地MySQL) |
| **test** | `application-test.yml` | 测试环境 | lease_test (测试服务器) |
| **prod** | `application-prod.yml` | 生产环境 | lease_prod (生产服务器) |

## 🚀 快速切换环境

### 1. 命令行方式（推荐）

```bash
# 开发环境（默认）
java -jar web-admin.jar

# 测试环境
java -jar web-admin.jar --spring.profiles.active=test

# 生产环境
java -jar web-admin.jar --spring.profiles.active=prod
```

### 2. 环境变量方式

```bash
# Linux/Mac
export SPRING_PROFILES_ACTIVE=prod
java -jar web-admin.jar

# Windows
set SPRING_PROFILES_ACTIVE=prod
java -jar web-admin.jar
```

### 3. IDE 配置方式

在 IntelliJ IDEA 中：
1. 打开 `Run/Debug Configurations`
2. 在 `VM options` 或 `Program arguments` 中添加：
   ```
   -Dspring.profiles.active=prod
   ```

## ⚙️ 配置详解

### 公共配置 (application.yml)

所有环境共享的配置：
- 应用名称和端口
- Jackson 序列化配置
- 日志配置
- 业务功能开关（缓存、限流、熔断）

### 环境特定配置

#### 开发环境 (dev)
- **数据库**: 本地 MySQL (127.0.0.1)
- **Redis**: 本地 (127.0.0.1:6379)
- **日志级别**: DEBUG，打印 SQL
- **MinIO**: 本地或开发服务器
- **短信**: 测试模式（验证码输出到控制台）

#### 测试环境 (test)
- **数据库**: 测试服务器 MySQL (192.168.10.101)
- **Redis**: 测试服务器 (192.168.10.101:6379)，使用 database 1
- **日志级别**: INFO，使用 Slf4j 记录
- **MinIO**: 测试服务器，bucket: xiaohong-test
- **短信**: 测试模式

#### 生产环境 (prod)
- **数据库**: 生产服务器 MySQL (49.235.121.95)
- **Redis**: 生产服务器 (49.235.121.95:6379)
- **日志级别**: WARN，关闭 SQL 打印
- **连接池**: 更大连接数 (max: 20, min: 10)
- **限流**: 更宽松的限制 (200 req/min)
- **MinIO**: 生产服务器，bucket: xiaohong-prod
- **短信**: 真实发送模式
- **API 文档**: 可禁用

## 🔐 生产环境环境变量

生产环境支持通过环境变量覆盖配置：

```bash
# 数据库配置
export MYSQL_HOST=49.235.121.95
export MYSQL_PORT=3306
export MYSQL_USERNAME=root
export MYSQL_PASSWORD=your-password

# Redis 配置
export REDIS_HOST=49.235.121.95
export REDIS_PORT=6379
export REDIS_PASSWORD=your-redis-password

# MinIO 配置
export MINIO_ENDPOINT=http://49.235.121.95:9000
export MINIO_ACCESS_KEY=minioadmin
export MINIO_SECRET_KEY=minioadmin
export MINIO_BUCKET=xiaohong-prod

# 阿里云短信
export ALIYUN_SMS_ACCESS_KEY_ID=your-access-key
export ALIYUN_SMS_ACCESS_KEY_SECRET=your-secret

# 日志配置
export LOG_LEVEL=INFO
export LOG_PATH=/var/log/xiaohong

# API 文档
export API_DOCS_ENABLED=false
export SWAGGER_UI_ENABLED=false
```

## 📁 配置文件位置

### 后台管理系统 (web-admin)
```
web/web-admin/src/main/resources/
├── application.yml          # 主配置 + 公共配置
├── application-dev.yml      # 开发环境
├── application-test.yml     # 测试环境
└── application-prod.yml     # 生产环境
```

### 用户端服务 (web-app)
```
web/web-app/src/main/resources/
├── application.yml          # 主配置 + 公共配置
├── application-dev.yml      # 开发环境
├── application-test.yml     # 测试环境
└── application-prod.yml     # 生产环境
```

## 🐳 Docker 部署

使用 Docker 运行时可指定环境：

```bash
# 开发环境
docker run -e SPRING_PROFILES_ACTIVE=dev xiaohong-admin:latest

# 测试环境
docker run -e SPRING_PROFILES_ACTIVE=test xiaohong-admin:latest

# 生产环境
docker run -e SPRING_PROFILES_ACTIVE=prod \
  -e MYSQL_PASSWORD=secret \
  -e REDIS_PASSWORD=secret \
  xiaohong-admin:latest
```

## 📝 最佳实践

1. **本地开发**: 使用 `dev` 环境，连接本地数据库
2. **功能测试**: 使用 `test` 环境，连接测试服务器
3. **生产部署**: 使用 `prod` 环境，通过环境变量注入敏感信息
4. **敏感信息**: 生产环境的密码、密钥等应通过环境变量传入，不要硬编码
5. **日志管理**: 生产环境日志定期清理，避免磁盘占满
6. **监控告警**: 生产环境建议开启性能监控和限流熔断

## 🔧 常见问题

### Q: 如何查看当前激活的环境？
A: 启动日志中会显示：`The following 2 profiles are active: "prod", "common"`

### Q: 配置优先级是怎样的？
A: 命令行参数 > 环境变量 > application-{profile}.yml > application.yml

### Q: 如何添加自定义环境？
A: 创建 `application-{自定义}.yml`，并在 `application.yml` 的 `spring.profiles.group` 中添加配置组。

### Q: 生产环境如何禁用 API 文档？
A: 设置环境变量 `API_DOCS_ENABLED=false` 和 `SWAGGER_UI_ENABLED=false`
