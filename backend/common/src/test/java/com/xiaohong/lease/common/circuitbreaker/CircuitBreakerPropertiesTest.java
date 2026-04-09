package com.xiaohong.lease.common.circuitbreaker;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 熔断配置属性单元测试
 *
 * @author 小红
 */
class CircuitBreakerPropertiesTest {

    @Test
    void testDefaultValues() {
        CircuitBreakerProperties properties = new CircuitBreakerProperties();

        assertTrue(properties.isEnabled());
        assertEquals(5, properties.getDefaultFailureThreshold());
        assertEquals(3, properties.getDefaultSuccessThreshold());
        assertEquals(30000, properties.getDefaultTimeoutDuration());
        assertEquals(3, properties.getDefaultHalfOpenMaxCalls());
    }

    @Test
    void testCustomValues() {
        CircuitBreakerProperties properties = new CircuitBreakerProperties();
        properties.setEnabled(false);
        properties.setDefaultFailureThreshold(10);
        properties.setDefaultSuccessThreshold(5);
        properties.setDefaultTimeoutDuration(60000);
        properties.setDefaultHalfOpenMaxCalls(5);

        assertFalse(properties.isEnabled());
        assertEquals(10, properties.getDefaultFailureThreshold());
        assertEquals(5, properties.getDefaultSuccessThreshold());
        assertEquals(60000, properties.getDefaultTimeoutDuration());
        assertEquals(5, properties.getDefaultHalfOpenMaxCalls());
    }
}
