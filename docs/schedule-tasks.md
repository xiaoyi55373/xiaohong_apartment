# 小红公寓定时任务管理文档

## 概述

本文档描述了小红公寓系统的定时任务管理功能，包括租约到期提醒、预约过期处理、数据统计等定时任务的配置和使用方法。

## 定时任务列表

### 1. 租约状态检查任务

- **任务名称**: `leaseStatusCheck`
- **执行时间**: 每天凌晨 0:00
- **Cron表达式**: `0 0 0 * * ?`
- **功能描述**: 检查所有租约，将已到期（lease_end_date <= 当前日期）且状态为"已签约"或"退租待确认"的租约状态更新为"已过期"
- **启用状态**: 默认启用

### 2. 租约到期提醒任务

- **任务名称**: `leaseReminder`
- **执行时间**: 每天早上 9:00
- **Cron表达式**: `0 0 9 * * ?`
- **功能描述**: 检查即将到期的租约，提前7天、3天、1天发送提醒通知
- **通知方式**: 短信 + 邮件
- **启用状态**: 可通过配置启用/禁用

### 3. 预约过期处理任务

- **任务名称**: `appointmentExpire`
- **执行时间**: 每天晚上 23:00
- **Cron表达式**: `0 0 23 * * ?`
- **功能描述**: 将超过24小时未看房的预约状态自动标记为"已过期"
- **启用状态**: 可通过配置启用/禁用

### 4. 每日数据统计任务

- **任务名称**: `dailyStatistics`
- **执行时间**: 每天凌晨 1:00
- **Cron表达式**: `0 0 1 * * ?`
- **功能描述**: 统计昨日新增租约数、新增预约数、即将到期租约数等数据
- **启用状态**: 可通过配置启用/禁用

### 5. 数据清理任务

- **任务名称**: `dataCleanup`
- **执行时间**: 每天凌晨 2:00
- **Cron表达式**: `0 0 2 * * ?`
- **功能描述**: 清理过期的临时文件、历史日志等数据
- **启用状态**: 可通过配置启用/禁用

## 配置说明

### application.yml 配置示例

```yaml
schedule:
  # 是否启用定时任务总开关
  enabled: true
  
  # 租约到期提醒配置
  lease-reminder:
    enabled: true
    # 提前提醒天数
    reminder-days: [7, 3, 1]
    # 提醒时间
    reminder-time: "09:00"
    # 是否发送短信
    send-sms: true
    # 是否发送邮件
    send-email: true
  
  # 预约过期处理配置
  appointment-expire:
    enabled: true
    # 预约过期时间（小时）
    expire-hours: 24
    # 处理时间
    process-time: "23:00"
  
  # 数据统计配置
  statistics:
    enabled: true
    # 统计时间
    statistics-time: "01:00"
    # 保留天数
    retention-days: 30
  
  # 数据清理配置
  data-cleanup:
    enabled: true
    # 清理时间
    cleanup-time: "02:00"
    # 操作日志保留天数
    operation-log-retention-days: 180
    # 登录日志保留天数
    login-log-retention-days: 90
    # 行为日志保留天数
    behavior-log-retention-days: 30
    # 临时文件保留天数
    temp-file-retention-days: 7
```

## API 接口

### 1. 获取定时任务列表

```http
GET /admin/schedule/list
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "name": "leaseStatusCheck",
      "description": "租约状态检查",
      "detail": "每天凌晨0点执行，将到期租约状态更新为已过期",
      "cron": "0 0 0 * * ?",
      "enabled": true,
      "lastExecutionTime": "2026-04-03T00:00:00",
      "successCount": 100,
      "failCount": 0,
      "successRate": "100.00%",
      "avgDurationMs": "150.00",
      "lastDurationMs": 145
    }
  ]
}
```

### 2. 获取定时任务配置

```http
GET /admin/schedule/config
```

### 3. 获取任务执行指标

```http
GET /admin/schedule/metrics/{taskName}
```

### 4. 获取所有任务执行指标

```http
GET /admin/schedule/metrics
```

### 5. 重置任务指标

```http
POST /admin/schedule/metrics/{taskName}/reset
```

### 6. 重置所有任务指标

```http
POST /admin/schedule/metrics/reset
```

### 7. 手动触发任务执行

```http
POST /admin/schedule/trigger/{taskName}
```

## 分布式锁

为了防止多实例部署时定时任务重复执行，系统使用 Redis 实现了分布式锁机制：

- **锁前缀**: `schedule:lock:`
- **锁过期时间**: 5分钟
- **获取锁失败**: 任务跳过执行

## 任务执行指标

系统会自动收集每个定时任务的执行指标：

- **successCount**: 成功执行次数
- **failCount**: 失败执行次数
- **totalCount**: 总执行次数
- **successRate**: 成功率
- **runningCount**: 正在运行的任务数
- **lastStartTime**: 最后开始时间
- **lastEndTime**: 最后结束时间
- **lastDurationMs**: 最后执行时长（毫秒）
- **avgDurationMs**: 平均执行时长（毫秒）

## 日志记录

定时任务执行日志会记录在 Redis 中，键名为 `schedule:log:{taskName}`，最多保留最近100条记录。

日志格式：
```
[2026-04-03T00:00:00] 开始: 2026-04-03T00:00:00, 结束: 2026-04-03T00:00:01, 结果: 成功, 消息: 更新了 10 条到期租约状态
```

## 注意事项

1. **时区问题**: 定时任务使用服务器本地时区，请确保服务器时区设置正确
2. **分布式部署**: 多实例部署时会自动使用分布式锁防止重复执行
3. **任务超时**: 单个任务执行时间超过5分钟会自动释放锁
4. **异常处理**: 任务执行异常会记录失败指标，但不会阻塞其他任务执行
5. **配置热更新**: 修改配置后需要重启服务才能生效

## 测试

运行单元测试：

```bash
cd /Users/dream3.14/Desktop/90/1208158461/公寓项目/代码/代码/后端/lease
mvn test -Dtest=ScheduleTasksTest
```

## 作者

- **作者**: 小红
- **创建时间**: 2026-04-03
