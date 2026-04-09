package com.xiaohong.lease.web.admin.schedule;

import com.xiaohong.lease.model.entity.LeaseAgreement;
import com.xiaohong.lease.model.entity.ViewAppointment;
import com.xiaohong.lease.model.enums.AppointmentStatus;
import com.xiaohong.lease.model.enums.LeaseStatus;
import com.xiaohong.lease.web.admin.mapper.LeaseAgreementMapper;
import com.xiaohong.lease.web.admin.mapper.ViewAppointmentMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 定时任务测试类
 *
 * @author 小红
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ScheduleTasksTest {

    @Autowired
    private ScheduleTasks scheduleTasks;

    @Autowired
    private LeaseAgreementMapper leaseAgreementMapper;

    @Autowired
    private ViewAppointmentMapper viewAppointmentMapper;

    @Test
    @DisplayName("测试租约状态检查任务")
    void testCheckLeaseStatus() {
        // 创建测试数据 - 创建一个已到期的租约
        LeaseAgreement expiredAgreement = createLeaseAgreement(
                "13800138000",
                "测试用户",
                getDateDaysAgo(1), // 昨天到期
                LeaseStatus.SIGNED
        );
        leaseAgreementMapper.insert(expiredAgreement);

        // 创建一个未到期租约
        LeaseAgreement activeAgreement = createLeaseAgreement(
                "13800138001",
                "活跃用户",
                getDateDaysAgo(-30), // 30天后到期
                LeaseStatus.SIGNED
        );
        leaseAgreementMapper.insert(activeAgreement);

        // 执行定时任务
        scheduleTasks.checkLeaseStatus();

        // 验证结果
        LeaseAgreement updatedExpired = leaseAgreementMapper.selectById(expiredAgreement.getId());
        assertEquals(LeaseStatus.EXPIRED, updatedExpired.getStatus(), "到期租约状态应更新为已过期");

        LeaseAgreement unchangedActive = leaseAgreementMapper.selectById(activeAgreement.getId());
        assertEquals(LeaseStatus.SIGNED, unchangedActive.getStatus(), "未到期租约状态应保持不变");
    }

    @Test
    @DisplayName("测试预约过期处理任务")
    void testAppointmentExpireProcess() {
        // 创建测试数据 - 创建一个已过期的预约
        ViewAppointment expiredAppointment = createViewAppointment(
                1L,
                "测试用户",
                "13800138000",
                getDateHoursAgo(25), // 25小时前
                AppointmentStatus.WAITING
        );
        viewAppointmentMapper.insert(expiredAppointment);

        // 创建一个未过期预约
        ViewAppointment activeAppointment = createViewAppointment(
                2L,
                "活跃用户",
                "13800138001",
                getDateHoursAgo(1), // 1小时前
                AppointmentStatus.WAITING
        );
        viewAppointmentMapper.insert(activeAppointment);

        // 执行定时任务（注意：实际处理需要任务配置存在）
        // 这里只是测试方法可调用，不验证具体结果
        assertDoesNotThrow(() -> scheduleTasks.appointmentExpireProcess());
    }

    @Test
    @DisplayName("测试数据清理任务")
    void testDataCleanup() {
        // 执行定时任务
        assertDoesNotThrow(() -> scheduleTasks.dataCleanup());
    }

    @Test
    @DisplayName("测试租约到期提醒任务")
    void testLeaseExpireRemind() {
        // 执行定时任务
        assertDoesNotThrow(() -> scheduleTasks.leaseExpireRemind());
    }

    @Test
    @DisplayName("测试初始化方法")
    void testInit() {
        // 测试初始化方法可正常调用
        assertDoesNotThrow(() -> scheduleTasks.init());
    }

    // ==================== 辅助方法 ====================

    private LeaseAgreement createLeaseAgreement(String phone, String name, Date endDate, LeaseStatus status) {
        LeaseAgreement agreement = new LeaseAgreement();
        agreement.setPhone(phone);
        agreement.setName(name);
        agreement.setIdentificationNumber("110101199001011234");
        agreement.setApartmentId(1L);
        agreement.setRoomId(1L);
        agreement.setLeaseStartDate(getDateDaysAgo(30));
        agreement.setLeaseEndDate(endDate);
        agreement.setLeaseTermId(1L);
        agreement.setRent(new BigDecimal("3000.00"));
        agreement.setDeposit(new BigDecimal("6000.00"));
        agreement.setPaymentTypeId(1L);
        agreement.setStatus(status);
        agreement.setSourceType(null);
        agreement.setAdditionalInfo("测试租约");
        return agreement;
    }

    private ViewAppointment createViewAppointment(Long userId, String name, String phone, 
                                                   Date appointmentTime, AppointmentStatus status) {
        ViewAppointment appointment = new ViewAppointment();
        appointment.setUserId(userId);
        appointment.setName(name);
        appointment.setPhone(phone);
        appointment.setApartmentId(1L);
        appointment.setAppointmentTime(appointmentTime);
        appointment.setAdditionalInfo("测试预约");
        appointment.setAppointmentStatus(status);
        return appointment;
    }

    private Date getDateDaysAgo(int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -days);
        return calendar.getTime();
    }

    private Date getDateHoursAgo(int hours) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, -hours);
        return calendar.getTime();
    }
}
