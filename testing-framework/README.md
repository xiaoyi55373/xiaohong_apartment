# 测试框架

项目测试系统，支持任务领取、进度追踪、自动重试等功能。

## 功能特点

- **任务管理**: 将测试流程拆分为可独立执行的任务单元
- **自动调度**: 每次执行自动选择最高优先级的未完成任务
- **依赖检查**: 自动检查任务依赖关系，确保按正确顺序执行
- **进度追踪**: 详细的执行日志和任务完成统计
- **自动重试**: 失败任务自动重试，提高测试成功率

## 快速开始

### 1. 查看任务状态

```bash
./scripts/task-manager.sh status
```

### 2. 运行测试任务

```bash
# 执行 1 个任务
./scripts/run-test-loop.sh 1

# 执行 5 个任务
./scripts/run-test-loop.sh 5

# 执行 10 个任务，每个任务最多重试 5 次
./scripts/run-test-loop.sh 10 5
```

### 3. 查看执行进度

```bash
# 查看任务列表
./scripts/task-manager.sh list

# 查看待完成任务
./scripts/task-manager.sh pending

# 查看下一个可执行任务
./scripts/task-manager.sh next
```

## 任务列表

当前测试任务分为以下阶段：

### 阶段一：环境检查
- T001: 检查 Java 环境
- T002: 检查 Maven 环境
- T003: 检查 Node.js 环境

### 阶段二：后端测试
- T010: 编译后端项目
- T011: 运行单元测试
- T012: 运行 API 集成测试
- T013: 后端代码质量检查

### 阶段三：前端测试
- T020: 安装前端依赖
- T021: 构建前端项目
- T022: 前端代码语法检查

### 阶段四：综合测试
- T030: 生成测试报告
- T031: 检查测试覆盖率

## 文件结构

```
testing-framework/
├── README.md                 # 本文件
├── tasks.md                  # 任务列表（可编辑）
├── test-logs/
│   └── progress.md          # 执行进度日志
├── scripts/
│   ├── run-test-loop.sh     # 主调度脚本
│   ├── task-manager.sh      # 任务管理工具
│   └── utils.sh             # 工具函数
└── config/
    └── test.conf            # 配置文件
```

## 任务管理

### 标记任务完成

```bash
./scripts/task-manager.sh complete T001
```

### 重置任务状态

```bash
# 重置单个任务
./scripts/task-manager.sh reset T001

# 重置所有任务
./scripts/task-manager.sh reset-all
```

## 自定义任务

编辑 `tasks.md` 文件添加自定义任务，格式如下：

```markdown
- [ ] **TASK_ID** - 任务名称
  - 优先级: P0/P1/P2
  - 依赖任务: T001, T002
  - 命令: `要执行的命令`
  - 验收标准: 成功的标志
  - 工作目录: /path/to/workdir
  - 最大重试次数: 3
```

### 优先级说明
- **P0**: 关键任务，必须优先完成
- **P1**: 重要任务，影响主要功能
- **P2**: 次要任务，优化项

## 注意事项

1. 确保 Java 17+、Maven 3.6+、Node.js 18+ 已安装
2. 后端项目路径：`../backend/`
3. 前端项目路径：`../frontend/`
4. 首次运行建议先执行 `./scripts/task-manager.sh status` 查看任务状态

## 故障排除

### 任务执行失败
```bash
# 查看详细日志
cat test-logs/progress.md

# 手动执行单个任务命令验证
cd <工作目录>
<任务命令>
```

### 依赖关系错误
```bash
# 查看待完成任务及其依赖
./scripts/task-manager.sh pending
```

## 许可

MIT License
