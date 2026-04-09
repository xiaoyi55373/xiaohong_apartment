# 小红公寓 (Xiaohong Apartment)

一个现代化的公寓租赁管理系统，包含管理后台、用户端服务和测试框架。

## 项目结构

```
xiaohong-apartment/
├── backend/              # 后端服务 (Spring Boot)
│   ├── common/           # 公共模块
│   ├── model/            # 数据模型
│   └── web/              # Web 应用
│       ├── web-admin     # 后台管理系统 API
│       └── web-app       # 用户端 API 服务
├── frontend/             # 前端 (Vue 3 + TypeScript)
│   ├── src/              # 源代码
│   ├── mock/             # Mock 数据
│   └── public/           # 静态资源
├── testing-framework/    # 测试框架与脚本
├── database/             # 数据库初始化脚本
├── docs/                 # 项目文档
└── docker-compose.yml    # Docker 环境编排
```

## 技术栈

### 后端
- **框架**: Spring Boot 3.x
- **数据库**: MySQL 8.0 + Redis
- **ORM**: MyBatis-Plus
- **API 文档**: SpringDoc OpenAPI + Knife4j
- **构建工具**: Maven

### 前端
- **框架**: Vue 3 + TypeScript
- **构建工具**: Vite
- **UI 组件库**: Element Plus
- **状态管理**: Pinia

## 快速开始

### 环境要求
- JDK 17+
- Node.js 18+
- MySQL 8.0+
- Redis 6.0+

### 方式一：Docker 启动（推荐）

```bash
# 启动 MySQL 和 Redis
docker-compose up -d
```

### 方式二：本地启动

**后端启动**

```bash
cd backend

# 1. 创建数据库并导入初始化脚本
mysql -u root -p < database/init.sql

# 2. 配置数据库连接（复制示例配置后修改）
# backend/web/web-admin/src/main/resources/application-dev.yml

# 3. 编译并运行
mvn clean install
mvn spring-boot:run -pl web/web-admin
```

**前端启动**

```bash
cd frontend
npm install
npm run dev
```

## 功能特性

- 🏠 公寓信息管理
- 📋 租赁合同管理
- 📅 预约看房管理
- 👤 用户管理
- 📊 数据统计分析
- 🔐 JWT 权限认证
- 📱 响应式设计

## 文档

项目相关文档已整理到 `docs/` 目录：

- `design-system.md` - 设计系统规范
- `environment-config.md` - 环境配置说明
- `cache-optimization.md` - 缓存优化方案
- `performance/` - 性能优化相关文档
- `schedule-tasks.md` - 定时任务管理
- `export-guide.md` - 数据导出功能说明

## 开发规范

- 后端包名: `com.xiaohong.lease`
- 前端组件: PascalCase 命名
- API 接口: RESTful 风格
- Git 提交信息遵循 [Conventional Commits](https://www.conventionalcommits.org/)

## 配置说明

### 数据库配置

编辑 `backend/web/web-admin/src/main/resources/application-dev.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/xiaohong?useUnicode=true&characterEncoding=utf-8
    username: your_username
    password: your_password
```

### 阿里云短信配置（可选）

```bash
export ALIYUN_ACCESS_KEY_ID=your_key_id
export ALIYUN_ACCESS_KEY_SECRET=your_key_secret
```

### MinIO 文件存储配置（可选）

```bash
export MINIO_ENDPOINT=http://localhost:9000
export MINIO_ACCESS_KEY=your_access_key
export MINIO_SECRET_KEY=your_secret_key
export MINIO_BUCKET=xiaohong
```

## 许可证

MIT License

---

**小红公寓** - 让租房更简单 🏡
