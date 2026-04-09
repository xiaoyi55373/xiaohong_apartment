package com.xiaohong.lease.common.log;

import com.xiaohong.lease.common.log.aspect.BehaviorLogAspect;
import com.xiaohong.lease.common.log.properties.BehaviorLogProperties;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 用户行为日志切面单元测试
 *
 * @author 小红
 */
class BehaviorLogAspectTest {

    @Test
    void testBehaviorLogAspectCreation() {
        BehaviorLogProperties properties = new BehaviorLogProperties();
        BehaviorLogAspect aspect = new BehaviorLogAspect(properties);

        assertNotNull(aspect);
        assertTrue(properties.isEnabled());
        assertEquals(2000, properties.getParamMaxLength());
    }

    @Test
    void testBehaviorLogPropertiesDefaults() {
        BehaviorLogProperties properties = new BehaviorLogProperties();
        assertTrue(properties.isEnabled());
        assertEquals(2000, properties.getParamMaxLength());
        assertNotNull(properties.getIgnoreUrls());
        assertNotNull(properties.getIgnoreParams());
        assertTrue(properties.getIgnoreParams().contains("password"));
        assertTrue(properties.getIgnoreParams().contains("token"));
    }
}
