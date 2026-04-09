package com.xiaohong.lease.common.ratelimit;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 限流配置属性单元测试
 *
 * @author 小红
 */
class RateLimitPropertiesTest {

    @Test
    void testDefaultValues() {
        RateLimitProperties properties = new RateLimitProperties();

        assertTrue(properties.isEnabled());
        assertEquals(60, properties.getDefaultWindow());
        assertEquals(100, properties.getDefaultMaxRequests());
        assertEquals(60, properties.getLoginWindow());
        assertEquals(5, properties.getLoginMaxRequests());
        assertEquals(60, properties.getSmsWindow());
        assertEquals(3, properties.getSmsMaxRequests());
    }

    @Test
    void testCustomValues() {
        RateLimitProperties properties = new RateLimitProperties();
        properties.setEnabled(false);
        properties.setDefaultWindow(120);
        properties.setDefaultMaxRequests(200);
        properties.setLoginWindow(300);
        properties.setLoginMaxRequests(10);

        assertFalse(properties.isEnabled());
        assertEquals(120, properties.getDefaultWindow());
        assertEquals(200, properties.getDefaultMaxRequests());
        assertEquals(300, properties.getLoginWindow());
        assertEquals(10, properties.getLoginMaxRequests());
    }
}
