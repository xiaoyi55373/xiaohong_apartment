package com.xiaohong.lease.web.admin.service;

import com.xiaohong.lease.model.entity.ScheduledTask;
import com.xiaohong.lease.model.enums.BaseStatus;
import com.xiaohong.lease.model.enums.TaskType;
import com.xiaohong.lease.model.vo.schedule.TaskExecuteResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 定时任务服务测试
 *
 * @author 小红
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ScheduledTaskServiceTest {

    @Autowired
    private ScheduledTaskService scheduledTaskService;

    @Test
    public void testInitDefaultTasks() {
        // 初始化默认任务
        scheduledTaskService.initDefaultTasks();

        // 验证任务已创建
        ScheduledTask leaseExpireTask = scheduledTaskService.getByTaskType(TaskType.LEASE_EXPIRE_REMIND.getStringCode());
        assertNotNull(leaseExpireTask);
        assertEquals("租约到期提醒（7天）", leaseExpireTask.getTaskName());
        assertEquals(BaseStatus.ENABLE, leaseExpireTask.getStatus());

        ScheduledTask appointmentExpireTask = scheduledTaskService.getByTaskType(TaskType.APPOINTMENT_EXPIRE.getStringCode());
        assertNotNull(appointmentExpireTask);
        assertEquals("预约过期处理", appointmentExpireTask.getTaskName());

        ScheduledTask dataCleanupTask = scheduledTaskService.getByTaskType(TaskType.DATA_CLEANUP.getStringCode());
        assertNotNull(dataCleanupTask);
        assertEquals("数据清理", dataCleanupTask.getTaskName());
    }

    @Test
    public void testCRUD() {
        // 创建任务
        ScheduledTask task = new ScheduledTask();
        task.setTaskName("测试任务");
        task.setTaskDescription("测试任务描述");
        task.setTaskType("TEST_TASK");
        task.setCronExpression("0 0 0 * * ?");
        task.setStatus(BaseStatus.ENABLE);
        task.setExecuteCount(0);
        task.setSuccessCount(0);
        task.setFailCount(0);

        boolean saved = scheduledTaskService.save(task);
        assertTrue(saved);
        assertNotNull(task.getId());

        // 查询任务
        ScheduledTask found = scheduledTaskService.getById(task.getId());
        assertNotNull(found);
        assertEquals("测试任务", found.getTaskName());

        // 更新任务
        found.setTaskName("更新后的任务名称");
        boolean updated = scheduledTaskService.updateById(found);
        assertTrue(updated);

        ScheduledTask updatedTask = scheduledTaskService.getById(task.getId());
        assertEquals("更新后的任务名称", updatedTask.getTaskName());

        // 删除任务
        boolean deleted = scheduledTaskService.removeById(task.getId());
        assertTrue(deleted);

        ScheduledTask deletedTask = scheduledTaskService.getById(task.getId());
        assertNull(deletedTask);
    }

    @Test
    public void testEnableDisableTask() {
        // 创建任务
        ScheduledTask task = new ScheduledTask();
        task.setTaskName("测试启用禁用");
        task.setTaskType("TEST_ENABLE_DISABLE");
        task.setCronExpression("0 0 0 * * ?");
        task.setStatus(BaseStatus.DISABLE);
        task.setExecuteCount(0);
        task.setSuccessCount(0);
        task.setFailCount(0);
        scheduledTaskService.save(task);

        // 启用任务
        scheduledTaskService.enableTask(task.getId());
        ScheduledTask enabledTask = scheduledTaskService.getById(task.getId());
        assertEquals(BaseStatus.ENABLE, enabledTask.getStatus());

        // 禁用任务
        scheduledTaskService.disableTask(task.getId());
        ScheduledTask disabledTask = scheduledTaskService.getById(task.getId());
        assertEquals(BaseStatus.DISABLE, disabledTask.getStatus());
    }

    @Test
    public void testListEnabledTasks() {
        // 初始化默认任务
        scheduledTaskService.initDefaultTasks();

        // 获取启用的任务列表
        List<ScheduledTask> enabledTasks = scheduledTaskService.listEnabledTasks();
        assertNotNull(enabledTasks);
        assertFalse(enabledTasks.isEmpty());

        // 验证所有任务都是启用状态
        for (ScheduledTask task : enabledTasks) {
            assertEquals(BaseStatus.ENABLE, task.getStatus());
        }
    }

    @Test
    public void testUpdateExecuteStatus() {
        // 创建任务
        ScheduledTask task = new ScheduledTask();
        task.setTaskName("测试更新状态");
        task.setTaskType("TEST_UPDATE_STATUS");
        task.setCronExpression("0 0 0 * * ?");
        task.setStatus(BaseStatus.ENABLE);
        task.setExecuteCount(0);
        task.setSuccessCount(0);
        task.setFailCount(0);
        scheduledTaskService.save(task);

        // 更新成功状态
        TaskExecuteResult successResult = TaskExecuteResult.success("执行成功", 10);
        scheduledTaskService.updateExecuteStatus(task.getId(), successResult);

        ScheduledTask updatedTask = scheduledTaskService.getById(task.getId());
        assertEquals(1, updatedTask.getExecuteCount());
        assertEquals(1, updatedTask.getSuccessCount());
        assertEquals(0, updatedTask.getFailCount());
        assertEquals("SUCCESS", updatedTask.getLastExecuteStatus());

        // 更新失败状态
        TaskExecuteResult failResult = TaskExecuteResult.fail("执行失败");
        scheduledTaskService.updateExecuteStatus(task.getId(), failResult);

        ScheduledTask updatedTask2 = scheduledTaskService.getById(task.getId());
        assertEquals(2, updatedTask2.getExecuteCount());
        assertEquals(1, updatedTask2.getSuccessCount());
        assertEquals(1, updatedTask2.getFailCount());
        assertEquals("FAIL", updatedTask2.getLastExecuteStatus());
    }

    @Test
    public void testGetByTaskType() {
        // 初始化默认任务
        scheduledTaskService.initDefaultTasks();

        // 根据类型查询任务
        ScheduledTask task = scheduledTaskService.getByTaskType(TaskType.LEASE_EXPIRE_REMIND.getStringCode());
        assertNotNull(task);
        assertEquals(TaskType.LEASE_EXPIRE_REMIND.getStringCode(), task.getTaskType());

        // 查询不存在的类型
        ScheduledTask notFound = scheduledTaskService.getByTaskType("NOT_EXIST");
        assertNull(notFound);
    }
}
