package com.xiaohong.lease.web.admin.schedule;

import com.xiaohong.lease.model.vo.schedule.TaskExecuteResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 任务执行结果单元测试
 *
 * @author 小红
 */
public class TaskExecuteResultTest {

    @Test
    public void testSuccessResult() {
        TaskExecuteResult result = TaskExecuteResult.success("执行成功", 10);
        
        assertTrue(result.getSuccess());
        assertEquals("执行成功", result.getMessage());
        assertEquals(10, result.getAffectedCount());
        assertNull(result.getDetails());
    }

    @Test
    public void testSuccessResultWithDetails() {
        TaskExecuteResult result = TaskExecuteResult.success("执行成功", 5, "详细结果");
        
        assertTrue(result.getSuccess());
        assertEquals("执行成功", result.getMessage());
        assertEquals(5, result.getAffectedCount());
        assertEquals("详细结果", result.getDetails());
    }

    @Test
    public void testFailResult() {
        TaskExecuteResult result = TaskExecuteResult.fail("执行失败");
        
        assertFalse(result.getSuccess());
        assertEquals("执行失败", result.getMessage());
        assertEquals(0, result.getAffectedCount());
        assertNull(result.getDetails());
    }

    @Test
    public void testFailResultWithDetails() {
        TaskExecuteResult result = TaskExecuteResult.fail("执行失败", "错误详情");
        
        assertFalse(result.getSuccess());
        assertEquals("执行失败", result.getMessage());
        assertEquals(0, result.getAffectedCount());
        assertEquals("错误详情", result.getDetails());
    }

    @Test
    public void testBuilderPattern() {
        TaskExecuteResult result = TaskExecuteResult.builder()
                .success(true)
                .message("自定义消息")
                .affectedCount(100)
                .details("自定义详情")
                .build();
        
        assertTrue(result.getSuccess());
        assertEquals("自定义消息", result.getMessage());
        assertEquals(100, result.getAffectedCount());
        assertEquals("自定义详情", result.getDetails());
    }
}
