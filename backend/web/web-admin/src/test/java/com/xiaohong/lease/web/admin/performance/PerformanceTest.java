package com.xiaohong.lease.web.admin.performance;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * 性能测试类
 * 使用多线程模拟并发请求，测试接口性能
 * 
 * @author 小红
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PerformanceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // 测试配置
    private static final int WARMUP_ITERATIONS = 10;
    private static final int[] CONCURRENT_USERS = {10, 50, 100, 200};
    private static final int REQUESTS_PER_USER = 20;
    
    // 性能指标阈值（毫秒）
    private static final long P95_THRESHOLD = 1000;
    private static final long P99_THRESHOLD = 2000;
    private static final long AVG_THRESHOLD = 500;
    private static final double ERROR_RATE_THRESHOLD = 0.01; // 1%

    private ExecutorService executorService;

    @BeforeAll
    void setUp() {
        executorService = Executors.newCachedThreadPool();
    }

    @AfterAll
    void tearDown() {
        if (executorService != null) {
            executorService.shutdown();
        }
    }

    /**
     * 性能测试结果类
     */
    public static class PerformanceResult {
        private final String apiName;
        private final int concurrentUsers;
        private final int totalRequests;
        private final int successfulRequests;
        private final int failedRequests;
        private final double errorRate;
        private final long minTime;
        private final long maxTime;
        private final double avgTime;
        private final long p50Time;
        private final long p95Time;
        private final long p99Time;
        private final double throughput;

        public PerformanceResult(String apiName, int concurrentUsers, int totalRequests,
                                 int successfulRequests, int failedRequests, double errorRate,
                                 long minTime, long maxTime, double avgTime,
                                 long p50Time, long p95Time, long p99Time, double throughput) {
            this.apiName = apiName;
            this.concurrentUsers = concurrentUsers;
            this.totalRequests = totalRequests;
            this.successfulRequests = successfulRequests;
            this.failedRequests = failedRequests;
            this.errorRate = errorRate;
            this.minTime = minTime;
            this.maxTime = maxTime;
            this.avgTime = avgTime;
            this.p50Time = p50Time;
            this.p95Time = p95Time;
            this.p99Time = p99Time;
            this.throughput = throughput;
        }

        @Override
        public String toString() {
            return String.format(
                "\n========== %s 性能测试结果 ==========\n" +
                "并发用户数: %d\n" +
                "总请求数: %d\n" +
                "成功请求数: %d\n" +
                "失败请求数: %d\n" +
                "错误率: %.2f%%\n" +
                "最小响应时间: %d ms\n" +
                "最大响应时间: %d ms\n" +
                "平均响应时间: %.2f ms\n" +
                "P50响应时间: %d ms\n" +
                "P95响应时间: %d ms\n" +
                "P99响应时间: %d ms\n" +
                "吞吐量: %.2f req/s\n" +
                "====================================\n",
                apiName, concurrentUsers, totalRequests, successfulRequests, failedRequests,
                errorRate * 100, minTime, maxTime, avgTime, p50Time, p95Time, p99Time, throughput
            );
        }

        public boolean isPassed() {
            return errorRate <= ERROR_RATE_THRESHOLD && 
                   avgTime <= AVG_THRESHOLD && 
                   p95Time <= P95_THRESHOLD;
        }
    }

    /**
     * 执行性能测试
     * 
     * @param apiName API名称
     * @param concurrentUsers 并发用户数
     * @param requestsPerUser 每个用户的请求数
     * @param requestTask 请求任务
     * @return 性能测试结果
     */
    private PerformanceResult runPerformanceTest(String apiName, int concurrentUsers, 
                                                  int requestsPerUser, Callable<Long> requestTask) 
            throws InterruptedException, ExecutionException {
        
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch completeLatch = new CountDownLatch(concurrentUsers);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);
        List<Long> responseTimes = new CopyOnWriteArrayList<>();
        AtomicLong totalStartTime = new AtomicLong(0);

        // 预热
        System.out.println("开始预热...");
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            try {
                requestTask.call();
            } catch (Exception e) {
                // 忽略预热错误
            }
        }
        System.out.println("预热完成");

        totalStartTime.set(System.currentTimeMillis());

        // 创建并发任务
        List<Future<?>> futures = new ArrayList<>();
        for (int i = 0; i < concurrentUsers; i++) {
            Future<?> future = executorService.submit(() -> {
                try {
                    startLatch.await(); // 等待所有线程准备就绪
                    
                    for (int j = 0; j < requestsPerUser; j++) {
                        long startTime = System.currentTimeMillis();
                        try {
                            requestTask.call();
                            successCount.incrementAndGet();
                        } catch (Exception e) {
                            failCount.incrementAndGet();
                        }
                        long endTime = System.currentTimeMillis();
                        responseTimes.add(endTime - startTime);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    completeLatch.countDown();
                }
            });
            futures.add(future);
        }

        // 同时开始所有请求
        startLatch.countDown();
        
        // 等待所有请求完成
        completeLatch.await();
        
        long totalEndTime = System.currentTimeMillis();
        long totalDuration = totalEndTime - totalStartTime.get();

        // 计算统计数据
        return calculateStatistics(apiName, concurrentUsers, successCount.get(), 
                                   failCount.get(), responseTimes, totalDuration);
    }

    /**
     * 计算统计数据
     */
    private PerformanceResult calculateStatistics(String apiName, int concurrentUsers,
                                                   int successCount, int failCount,
                                                   List<Long> responseTimes, long totalDuration) {
        int totalRequests = successCount + failCount;
        double errorRate = totalRequests > 0 ? (double) failCount / totalRequests : 0;
        
        if (responseTimes.isEmpty()) {
            return new PerformanceResult(apiName, concurrentUsers, totalRequests, successCount,
                    failCount, errorRate, 0, 0, 0, 0, 0, 0, 0);
        }

        // 排序计算百分位数
        List<Long> sortedTimes = responseTimes.stream().sorted().toList();
        
        long minTime = sortedTimes.get(0);
        long maxTime = sortedTimes.get(sortedTimes.size() - 1);
        double avgTime = sortedTimes.stream().mapToLong(Long::longValue).average().orElse(0);
        
        long p50Time = sortedTimes.get((int) (sortedTimes.size() * 0.5));
        long p95Time = sortedTimes.get((int) (sortedTimes.size() * 0.95));
        long p99Time = sortedTimes.get((int) (sortedTimes.size() * 0.99));
        
        double throughput = totalDuration > 0 ? (double) totalRequests / (totalDuration / 1000.0) : 0;

        return new PerformanceResult(apiName, concurrentUsers, totalRequests, successCount,
                failCount, errorRate, minTime, maxTime, avgTime, p50Time, p95Time, p99Time, throughput);
    }

    // ==================== 具体接口性能测试 ====================

    @Test
    @Order(1)
    @DisplayName("测试登录接口性能")
    void testLoginPerformance() throws Exception {
        System.out.println("\n开始测试登录接口性能...");
        
        for (int concurrentUsers : new int[]{10, 50}) {
            PerformanceResult result = runPerformanceTest(
                "登录接口",
                concurrentUsers,
                REQUESTS_PER_USER,
                () -> {
                    long startTime = System.currentTimeMillis();
                    MvcResult captchaResult = mockMvc.perform(get("/admin/login/captcha"))
                            .andReturn();
                    
                    String captchaResponse = captchaResult.getResponse().getContentAsString();
                    String captchaKey = extractJsonValue(captchaResponse, "key");
                    
                    String loginJson = "{" +
                            "\"username\":\"admin\"," +
                            "\"password\":\"admin\"," +
                            "\"captchaKey\":\"" + captchaKey + "\"," +
                            "\"captchaCode\":\"TEST\"" +
                            "}";
                    
                    mockMvc.perform(post("/admin/login")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(loginJson))
                            .andReturn();
                    
                    return System.currentTimeMillis() - startTime;
                }
            );
            
            System.out.println(result);
            Assertions.assertTrue(result.isPassed(), 
                "登录接口性能测试未通过: " + concurrentUsers + "并发用户");
        }
    }

    @Test
    @Order(2)
    @DisplayName("测试公寓列表查询接口性能")
    void testApartmentListPerformance() throws Exception {
        System.out.println("\n开始测试公寓列表查询接口性能...");
        
        for (int concurrentUsers : CONCURRENT_USERS) {
            PerformanceResult result = runPerformanceTest(
                "公寓列表查询接口",
                concurrentUsers,
                REQUESTS_PER_USER,
                () -> {
                    long start = System.currentTimeMillis();
                    mockMvc.perform(get("/admin/apartment/pageItem")
                                    .param("current", "1")
                                    .param("size", "10"))
                            .andReturn();
                    return System.currentTimeMillis() - start;
                }
            );
            
            System.out.println(result);
        }
    }

    @Test
    @Order(3)
    @DisplayName("测试房间列表查询接口性能")
    void testRoomListPerformance() throws Exception {
        System.out.println("\n开始测试房间列表查询接口性能...");
        
        for (int concurrentUsers : CONCURRENT_USERS) {
            PerformanceResult result = runPerformanceTest(
                "房间列表查询接口",
                concurrentUsers,
                REQUESTS_PER_USER,
                () -> {
                    long start = System.currentTimeMillis();
                    mockMvc.perform(get("/admin/room/pageItem")
                                    .param("current", "1")
                                    .param("size", "10"))
                            .andReturn();
                    return System.currentTimeMillis() - start;
                }
            );
            
            System.out.println(result);
        }
    }

    @Test
    @Order(4)
    @DisplayName("测试租约列表查询接口性能")
    void testLeaseAgreementListPerformance() throws Exception {
        System.out.println("\n开始测试租约列表查询接口性能...");
        
        for (int concurrentUsers : CONCURRENT_USERS) {
            PerformanceResult result = runPerformanceTest(
                "租约列表查询接口",
                concurrentUsers,
                REQUESTS_PER_USER,
                () -> {
                    long start = System.currentTimeMillis();
                    mockMvc.perform(get("/admin/agreement/page")
                                    .param("current", "1")
                                    .param("size", "10"))
                            .andReturn();
                    return System.currentTimeMillis() - start;
                }
            );
            
            System.out.println(result);
        }
    }

    @Test
    @Order(5)
    @DisplayName("测试预约列表查询接口性能")
    void testAppointmentListPerformance() throws Exception {
        System.out.println("\n开始测试预约列表查询接口性能...");
        
        for (int concurrentUsers : CONCURRENT_USERS) {
            PerformanceResult result = runPerformanceTest(
                "预约列表查询接口",
                concurrentUsers,
                REQUESTS_PER_USER,
                () -> {
                    long start = System.currentTimeMillis();
                    mockMvc.perform(get("/admin/appointment/page")
                                    .param("current", "1")
                                    .param("size", "10"))
                            .andReturn();
                    return System.currentTimeMillis() - start;
                }
            );
            
            System.out.println(result);
        }
    }

    /**
     * 从JSON字符串中提取字段值
     */
    private String extractJsonValue(String json, String key) {
        try {
            com.fasterxml.jackson.databind.JsonNode root = objectMapper.readTree(json);
            if (root.has("data")) {
                com.fasterxml.jackson.databind.JsonNode dataNode = root.get("data");
                if (dataNode.has(key)) {
                    return dataNode.get(key).asText();
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
