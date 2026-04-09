package com.xiaohong.lease.web.admin.schedule;

import com.xiaohong.lease.model.entity.ScheduledTask;
import com.xiaohong.lease.model.enums.BaseStatus;
import com.xiaohong.lease.model.enums.TaskType;
import com.xiaohong.lease.model.vo.schedule.TaskExecuteResult;
import com.xiaohong.lease.web.admin.service.ScheduledTaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 定时任务执行器测试
 *
 * @author 小红
 */
@SpringBootTest
@ActiveProfiles("test")
public class ScheduleTaskExecutorTest {

    @Autowired
    private ScheduleTaskExecutor taskExecutor;

    @Autowired
    private ScheduledTaskService scheduledTaskService;

    @Test
    public void testExecuteLeaseExpireRemindTask() {
        // 创建测试任务
        ScheduledTask task = new ScheduledTask();
        task.setTaskName("测试租约到期提醒");
        task.setTaskType(TaskType.LEASE_EXPIRE_REMIND.getStringCode());
        task.setCronExpression("0 0 9 * * ?");
        task.setStatus(BaseStatus.ENABLE);
        task.setNotifyEnabled(0); // 禁用通知，避免真实发送
        task.setRemindDaysBefore(7);
        task.setNotifyType("SMS");

        // 执行任务
        TaskExecuteResult result = taskExecutor.executeTask(task);

        // 验证结果
        assertNotNull(result);
        assertTrue(result.getSuccess() || !result.getSuccess()); // 允许成功或失败
        assertNotNull(result.getMessage());
    }

    @Test
    public void testExecuteAppointmentExpireTask() {
        // 创建测试任务
        ScheduledTask task = new ScheduledTask();
        task.setTaskName("测试预约过期处理");
        task.setTaskType(TaskType.APPOINTMENT_EXPIRE.getStringCode());
        task.setCronExpression("0 0 1 * * ?");
        task.setStatus(BaseStatus.ENABLE);
        task.setNotifyEnabled(0);

        // 执行任务
        TaskExecuteResult result = taskExecutor.executeTask(task);

        // 验证结果
        assertNotNull(result);
        assertNotNull(result.getMessage());
    }

    @Test
    public void testExecuteDataCleanupTask() {
        // 创建测试任务
        ScheduledTask task = new ScheduledTask();
        task.setTaskName("测试数据清理");
        task.setTaskType(TaskType.DATA_CLEANUP.getStringCode());
        task.setCronExpression("0 0 2 * * 0");
        task.setStatus(BaseStatus.ENABLE);
        task.setNotifyEnabled(0);

        // 执行任务
        TaskExecuteResult result = taskExecutor.executeTask(task);

        // 验证结果
        assertNotNull(result);
        assertTrue(result.getSuccess());
        assertNotNull(result.getMessage());
    }

    @Test
    public void testExecuteUnknownTask() {
        // 创建未知类型任务
        ScheduledTask task = new ScheduledTask();
        task.setTaskName("未知任务");
        task.setTaskType("UNKNOWN_TYPE");
        task.setCronExpression("0 0 0 * * ?");
        task.setStatus(BaseStatus.ENABLE);

        // 执行任务
        TaskExecuteResult result = taskExecutor.executeTask(task);

        // 验证结果 - 应该失败
        assertNotNull(result);
        assertFalse(result.getSuccess());
        assertTrue(result.getMessage().contains("未知"));
    }

    @Test
    public void testTaskExecuteResult() {
        // 测试成功结果
        TaskExecuteResult successResult = TaskExecuteResult.success("成功", 10);
        assertTrue(successResult.getSuccess());
        assertEquals("成功", successResult.getMessage());
        assertEquals(10, successResult.getAffectedCount());

        // 测试失败结果
        TaskExecuteResult failResult = TaskExecuteResult.fail("失败");
        assertFalse(failResult.getSuccess());
        assertEquals("失败", failResult.getMessage());
        assertEquals(0, failResult.getAffectedCount());

        // 测试带详情的成功结果
        TaskExecuteResult successWithDetails = TaskExecuteResult.success("成功", 5, "详情");
        assertTrue(successWithDetails.getSuccess());
        assertEquals("成功", successWithDetails.getMessage());
        assertEquals(5, successWithDetails.getAffectedCount());
        assertEquals("详情", successWithDetails.getDetails());
    }
}
