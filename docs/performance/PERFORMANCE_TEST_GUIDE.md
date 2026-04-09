# 小红公寓claw - 性能压测指南

> 本文档介绍如何使用JMeter和Java性能测试工具对小红公寓claw后端进行性能压测。

## 📋 目录

1. [测试环境准备](#测试环境准备)
2. [JMeter压测](#jmeter压测)
3. [Java性能测试](#java性能测试)
4. [性能指标说明](#性能指标说明)
5. [结果分析](#结果分析)

---

## 测试环境准备

### 1. 系统要求

- **JMeter**: Apache JMeter 5.6.2+
- **Java**: JDK 17+
- **Maven**: 3.8+
- **数据库**: MySQL 8.0+ 或 H2（测试环境）
- **Redis**: 6.0+ 或嵌入式Redis

### 2. 安装JMeter

```bash
# macOS
brew install jmeter

# 或下载安装包
wget https://dlcdn.apache.org//jmeter/binaries/apache-jmeter-5.6.2.tgz
tar -xzf apache-jmeter-5.6.2.tgz
cd apache-jmeter-5.6.2/bin
./jmeter
```

### 3. 启动后端服务

```bash
# 进入项目目录
cd /Users/dream3.14/Desktop/90/1208158461/公寓项目/代码/代码/后端/lease

# 编译项目
mvn clean compile -DskipTests

# 启动后台管理服务
mvn spring-boot:run -pl web/web-admin
```

---

## JMeter压测

### 1. 导入测试计划

1. 打开JMeter GUI
2. 选择 `File` -> `Open`
3. 选择文件：`web/web-admin/src/test/resources/jmeter/xiaohong-apartment-load-test.jmx`

### 2. 测试计划说明

测试计划包含以下线程组：

| 线程组 | 并发用户数 | 循环次数 | 压测场景 |
|--------|-----------|---------|---------|
| 登录接口压测 | 100 | 10 | 用户登录场景 |
| 公寓列表查询压测 | 200 | 20 | 公寓列表查询 |
| 房间列表查询压测 | 150 | 15 | 房间列表查询 |
| 租约查询压测 | 100 | 10 | 租约列表查询 |
| 混合场景压测 | 300 | 5 | 混合API调用 |

### 3. 运行压测

#### GUI模式（开发调试）

```bash
# 直接点击JMeter界面上的"Start"按钮
```

#### 命令行模式（正式压测）

```bash
# 进入JMeter目录
cd apache-jmeter-5.6.2/bin

# 运行压测
./jmeter -n -t /Users/dream3.14/Desktop/90/1208158461/公寓项目/代码/代码/后端/lease/web/web-admin/src/test/resources/jmeter/xiaohong-apartment-load-test.jmx \
  -l /tmp/test-results.jtl \
  -e -o /tmp/test-report

# 参数说明：
# -n: 非GUI模式
# -t: 测试计划文件
# -l: 结果日志文件
# -e: 生成报告
# -o: 报告输出目录
```

### 4. 查看结果

```bash
# 打开生成的HTML报告
open /tmp/test-report/index.html
```

---

## Java性能测试

### 1. 运行性能测试

```bash
# 进入项目目录
cd /Users/dream3.14/Desktop/90/1208158461/公寓项目/代码/代码/后端/lease

# 运行性能测试
mvn test -pl web/web-admin -Dtest=PerformanceTest

# 或运行单个测试方法
mvn test -pl web/web-admin -Dtest=PerformanceTest#testApartmentListPerformance
```

### 2. 性能测试类说明

`PerformanceTest.java` 提供了以下测试方法：

| 测试方法 | 测试内容 | 并发用户数 |
|---------|---------|-----------|
| testLoginPerformance | 登录接口 | 10, 50 |
| testApartmentListPerformance | 公寓列表查询 | 10, 50, 100, 200 |
| testRoomListPerformance | 房间列表查询 | 10, 50, 100, 200 |
| testLeaseAgreementListPerformance | 租约列表查询 | 10, 50, 100, 200 |
| testAppointmentListPerformance | 预约列表查询 | 10, 50, 100, 200 |

### 3. 自定义性能测试

```java
// 示例：自定义并发测试
@Test
void testCustomApi() throws Exception {
    PerformanceResult result = runPerformanceTest(
        "自定义接口",
        100,  // 并发用户数
        50,   // 每个用户的请求数
        () -> {
            long start = System.currentTimeMillis();
            mockMvc.perform(get("/admin/custom/api"))
                    .andReturn();
            return System.currentTimeMillis() - start;
        }
    );
    
    System.out.println(result);
    Assertions.assertTrue(result.isPassed());
}
```

---

## 性能指标说明

### 1. 关键指标

| 指标 | 说明 | 目标值 |
|------|------|--------|
| **吞吐量 (Throughput)** | 每秒处理的请求数 | ≥ 100 req/s |
| **平均响应时间 (Average)** | 所有请求的平均响应时间 | ≤ 500 ms |
| **P50响应时间** | 50%的请求响应时间 | ≤ 300 ms |
| **P95响应时间** | 95%的请求响应时间 | ≤ 1000 ms |
| **P99响应时间** | 99%的请求响应时间 | ≤ 2000 ms |
| **错误率 (Error Rate)** | 失败请求占总请求的百分比 | ≤ 1% |
| **最小响应时间 (Min)** | 最快请求的响应时间 | - |
| **最大响应时间 (Max)** | 最慢请求的响应时间 | - |

### 2. 性能等级

| 等级 | P95响应时间 | 说明 |
|------|-------------|------|
| 优秀 | < 200ms | 用户体验极佳 |
| 良好 | 200-500ms | 用户体验良好 |
| 一般 | 500-1000ms | 可接受，有优化空间 |
| 较差 | 1000-2000ms | 需要优化 |
| 极差 | > 2000ms | 必须立即优化 |

---

## 结果分析

### 1. 常见性能问题

#### 问题1：响应时间过长

**可能原因：**
- 数据库查询慢
- 缺少索引
- 查询返回数据量过大
- 网络延迟

**解决方案：**
- 查看慢查询日志
- 添加合适的索引
- 优化SQL语句
- 使用分页查询

#### 问题2：吞吐量低

**可能原因：**
- 连接池配置不当
- 线程池过小
- 数据库连接数不足
- 存在锁竞争

**解决方案：**
- 调整连接池大小
- 优化线程池配置
- 检查数据库连接数限制
- 减少锁粒度

#### 问题3：错误率高

**可能原因：**
- 数据库连接超时
- 内存溢出
- 并发冲突
- 第三方服务不可用

**解决方案：**
- 增加连接超时时间
- 增加内存配置
- 添加重试机制
- 添加熔断降级

### 2. 性能调优建议

#### 数据库优化

```sql
-- 1. 查看慢查询
SELECT * FROM mysql.slow_log 
WHERE start_time > DATE_SUB(NOW(), INTERVAL 1 HOUR)
ORDER BY query_time DESC;

-- 2. 查看查询执行计划
EXPLAIN SELECT * FROM apartment_info WHERE is_deleted = 0;

-- 3. 查看表索引
SHOW INDEX FROM apartment_info;
```

#### JVM优化

```bash
# 推荐JVM参数
java -Xms2g -Xmx2g \
     -XX:+UseG1GC \
     -XX:MaxGCPauseMillis=200 \
     -XX:+PrintGCDetails \
     -XX:+PrintGCTimeStamps \
     -jar xiaohong-apartment-claw.jar
```

#### 连接池优化

```yaml
# application.yml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 10
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
      leak-detection-threshold: 60000
```

---

## 📊 性能测试报告模板

### 测试概要

| 项目 | 内容 |
|------|------|
| 测试日期 | 2026-04-03 |
| 测试版本 | v1.0.0 |
| 测试环境 | 开发环境 |
| 测试工具 | JMeter 5.6.2 |

### 测试结果

| 接口 | 并发数 | 吞吐量 | 平均响应 | P95响应 | 错误率 | 状态 |
|------|--------|--------|----------|---------|--------|------|
| 登录接口 | 100 | - | - | - | - | - |
| 公寓列表 | 200 | - | - | - | - | - |
| 房间列表 | 150 | - | - | - | - | - |
| 租约列表 | 100 | - | - | - | - | - |

### 优化建议

1. ...
2. ...
3. ...

---

*文档版本: 1.0*  
*创建时间: 2026-04-03*  
*作者: 小红公寓claw开发团队*
