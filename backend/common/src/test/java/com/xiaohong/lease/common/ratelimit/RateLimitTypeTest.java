package com.xiaohong.lease.common.ratelimit;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 限流类型枚举单元测试
 *
 * @author 小红
 */
class RateLimitTypeTest {

    @Test
    void testEnumValues() {
        assertEquals(3, RateLimitType.values().length);
        assertNotNull(RateLimitType.GLOBAL);
        assertNotNull(RateLimitType.USER);
        assertNotNull(RateLimitType.IP);
    }

    @Test
    void testEnumValueOf() {
        assertEquals(RateLimitType.GLOBAL, RateLimitType.valueOf("GLOBAL"));
        assertEquals(RateLimitType.USER, RateLimitType.valueOf("USER"));
        assertEquals(RateLimitType.IP, RateLimitType.valueOf("IP"));
    }
}
