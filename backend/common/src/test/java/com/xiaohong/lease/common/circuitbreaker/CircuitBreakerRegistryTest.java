package com.xiaohong.lease.common.circuitbreaker;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 熔断器注册表单元测试
 *
 * @author 小红
 */
class CircuitBreakerRegistryTest {

    @AfterEach
    void tearDown() {
        CircuitBreakerRegistry.clear();
    }

    @Test
    void testGetBreaker() {
        CircuitBreakerRegistry.CircuitBreakerInstance breaker = CircuitBreakerRegistry.getBreaker(
                "test-breaker", 5, 3, 30000, 3);

        assertNotNull(breaker);
        assertEquals("test-breaker", breaker.getName());
        assertEquals(CircuitBreakerState.CLOSED, breaker.getState());
    }

    @Test
    void testGetBreakerSameInstance() {
        CircuitBreakerRegistry.CircuitBreakerInstance breaker1 = CircuitBreakerRegistry.getBreaker(
                "same-breaker", 5, 3, 30000, 3);
        CircuitBreakerRegistry.CircuitBreakerInstance breaker2 = CircuitBreakerRegistry.getBreaker(
                "same-breaker", 5, 3, 30000, 3);

        assertSame(breaker1, breaker2);
    }

    @Test
    void testCircuitBreakerClosedToOpen() {
        CircuitBreakerRegistry.CircuitBreakerInstance breaker = CircuitBreakerRegistry.getBreaker(
                "failure-breaker", 3, 2, 30000, 2);

        assertTrue(breaker.allowRequest());
        breaker.recordFailure();
        assertEquals(CircuitBreakerState.CLOSED, breaker.getState());

        breaker.recordFailure();
        assertEquals(CircuitBreakerState.CLOSED, breaker.getState());

        breaker.recordFailure();
        assertEquals(CircuitBreakerState.OPEN, breaker.getState());
        assertFalse(breaker.allowRequest());
    }

    @Test
    void testCircuitBreakerOpenToHalfOpen() throws InterruptedException {
        CircuitBreakerRegistry.CircuitBreakerInstance breaker = CircuitBreakerRegistry.getBreaker(
                "timeout-breaker", 1, 1, 100, 1);

        breaker.recordFailure();
        assertEquals(CircuitBreakerState.OPEN, breaker.getState());
        assertFalse(breaker.allowRequest());

        Thread.sleep(150);
        assertTrue(breaker.allowRequest());
        assertEquals(CircuitBreakerState.HALF_OPEN, breaker.getState());
    }

    @Test
    void testCircuitBreakerHalfOpenToClosed() throws InterruptedException {
        CircuitBreakerRegistry.CircuitBreakerInstance breaker = CircuitBreakerRegistry.getBreaker(
                "recovery-breaker", 1, 2, 100, 3);

        breaker.recordFailure();
        Thread.sleep(150);

        assertTrue(breaker.allowRequest());
        breaker.recordSuccess();
        assertEquals(CircuitBreakerState.HALF_OPEN, breaker.getState());

        assertTrue(breaker.allowRequest());
        breaker.recordSuccess();
        assertEquals(CircuitBreakerState.CLOSED, breaker.getState());
    }

    @Test
    void testCircuitBreakerHalfOpenToOpen() throws InterruptedException {
        CircuitBreakerRegistry.CircuitBreakerInstance breaker = CircuitBreakerRegistry.getBreaker(
                "reopen-breaker", 1, 2, 100, 3);

        breaker.recordFailure();
        Thread.sleep(150);

        assertTrue(breaker.allowRequest());
        breaker.recordFailure();
        assertEquals(CircuitBreakerState.OPEN, breaker.getState());
        assertFalse(breaker.allowRequest());
    }

    @Test
    void testRecordSuccessInClosedState() {
        CircuitBreakerRegistry.CircuitBreakerInstance breaker = CircuitBreakerRegistry.getBreaker(
                "success-breaker", 5, 3, 30000, 3);

        // 连续失败2次，应该保持在CLOSED状态
        breaker.recordFailure();
        breaker.recordFailure();
        assertEquals(CircuitBreakerState.CLOSED, breaker.getState());

        // 记录成功后，失败计数应该被重置
        breaker.recordSuccess();
        // 再失败5次应该才能打开（因为被重置了）
        breaker.recordFailure();
        breaker.recordFailure();
        breaker.recordFailure();
        breaker.recordFailure();
        breaker.recordFailure();
        assertEquals(CircuitBreakerState.OPEN, breaker.getState());
    }

    @Test
    void testRemoveBreaker() {
        CircuitBreakerRegistry.getBreaker("remove-breaker", 5, 3, 30000, 3);
        CircuitBreakerRegistry.removeBreaker("remove-breaker");

        CircuitBreakerRegistry.CircuitBreakerInstance newBreaker = CircuitBreakerRegistry.getBreaker(
                "remove-breaker", 5, 3, 30000, 3);
        assertEquals(CircuitBreakerState.CLOSED, newBreaker.getState());
    }

    @Test
    void testHalfOpenMaxCalls() throws InterruptedException {
        CircuitBreakerRegistry.removeBreaker("limited-breaker");
        CircuitBreakerRegistry.CircuitBreakerInstance breaker = CircuitBreakerRegistry.getBreaker(
                "limited-breaker", 1, 2, 500, 2);

        breaker.recordFailure();
        assertEquals(CircuitBreakerState.OPEN, breaker.getState());
        assertFalse(breaker.allowRequest());

        Thread.sleep(600);
        // 超过500ms后应该进入HALF_OPEN
        assertTrue(breaker.allowRequest());
        assertEquals(CircuitBreakerState.HALF_OPEN, breaker.getState());

        // 半开状态下允许请求通过
        assertTrue(breaker.allowRequest());
        // 记录一次成功后仍然在HALF_OPEN（successThreshold=2）
        breaker.recordSuccess();
        assertEquals(CircuitBreakerState.HALF_OPEN, breaker.getState());
        // 再记录一次成功后应该关闭
        breaker.recordSuccess();
        assertEquals(CircuitBreakerState.CLOSED, breaker.getState());
    }


}
