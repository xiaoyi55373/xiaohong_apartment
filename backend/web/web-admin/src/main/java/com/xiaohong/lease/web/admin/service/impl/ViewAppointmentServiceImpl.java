package com.xiaohong.lease.web.admin.service.impl;

import com.xiaohong.lease.common.wx.WxMiniProgramClient;
import com.xiaohong.lease.model.entity.ApartmentInfo;
import com.xiaohong.lease.model.entity.UserInfo;
import com.xiaohong.lease.model.entity.ViewAppointment;
import com.xiaohong.lease.model.enums.AppointmentStatus;
import com.xiaohong.lease.web.admin.mapper.ApartmentInfoMapper;
import com.xiaohong.lease.web.admin.mapper.UserInfoMapper;
import com.xiaohong.lease.web.admin.mapper.ViewAppointmentMapper;
import com.xiaohong.lease.web.admin.service.ViewAppointmentService;
import com.xiaohong.lease.web.admin.vo.appointment.AppointmentQueryVo;
import com.xiaohong.lease.web.admin.vo.appointment.AppointmentVo;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 小红
 * @description 针对表【view_appointment(预约看房信息表)】的数据库操作Service实现
 * @createDate 2023-07-24 15:48:00
 */
@Slf4j
@Service
public class ViewAppointmentServiceImpl extends ServiceImpl<ViewAppointmentMapper, ViewAppointment>
        implements ViewAppointmentService {

    @Autowired
    private ViewAppointmentMapper viewAppointmentMapper;

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private ApartmentInfoMapper apartmentInfoMapper;

    @Autowired
    private WxMiniProgramClient wxMiniProgramClient;

    @Value("${wx.mini-program.subscribe-template-id.appointment-status:}")
    private String appointmentStatusTemplateId;

    @Override
    public IPage<AppointmentVo> pageItem(Page<AppointmentVo> page, AppointmentQueryVo queryVo) {
        return viewAppointmentMapper.pageItem(page,queryVo);
    }

    @Override
    public List<AppointmentVo> listAll(AppointmentQueryVo queryVo) {
        // 使用分页查询所有数据（设置一个较大的页面大小）
        Page<AppointmentVo> page = new Page<>(1, 10000);
        IPage<AppointmentVo> result = viewAppointmentMapper.pageItem(page, queryVo);
        return result.getRecords();
    }

    @Override
    public boolean updateStatusByIdWithNotify(Long id, AppointmentStatus status) {
        // 1. 查询原预约信息
        ViewAppointment appointment = viewAppointmentMapper.selectById(id);
        if (appointment == null) {
            log.warn("预约不存在，id: {}", id);
            return false;
        }

        // 2. 更新状态
        LambdaUpdateWrapper<ViewAppointment> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ViewAppointment::getId, id);
        updateWrapper.set(ViewAppointment::getAppointmentStatus, status);
        boolean updated = update(updateWrapper);

        if (!updated) {
            return false;
        }

        // 3. 发送微信订阅消息通知
        sendAppointmentStatusNotification(appointment, status);

        return true;
    }

    /**
     * 发送预约状态变更订阅消息
     */
    private void sendAppointmentStatusNotification(ViewAppointment appointment, AppointmentStatus newStatus) {
        if (!wxMiniProgramClient.hasConfig()) {
            log.debug("微信小程序未配置，跳过订阅消息发送");
            return;
        }
        if (appointmentStatusTemplateId == null || appointmentStatusTemplateId.isEmpty()) {
            log.debug("订阅消息模板 ID 未配置，跳过发送");
            return;
        }

        UserInfo userInfo = userInfoMapper.selectById(appointment.getUserId());
        if (userInfo == null || userInfo.getOpenid() == null || userInfo.getOpenid().isEmpty()) {
            log.debug("用户未绑定微信 openid，跳过订阅消息发送，userId: {}", appointment.getUserId());
            return;
        }

        String accessToken = wxMiniProgramClient.getAccessToken();
        if (accessToken == null) {
            log.warn("获取 access_token 失败，无法发送订阅消息");
            return;
        }

        ApartmentInfo apartmentInfo = apartmentInfoMapper.selectById(appointment.getApartmentId());
        String apartmentName = apartmentInfo != null ? apartmentInfo.getName() : "未知公寓";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String appointmentTimeStr = appointment.getAppointmentTime() != null
                ? sdf.format(appointment.getAppointmentTime())
                : "";

        Map<String, Map<String, String>> data = new HashMap<>();
        data.put("thing1", Map.of("value", truncate(apartmentName, 20)));
        data.put("time2", Map.of("value", appointmentTimeStr));
        data.put("phrase3", Map.of("value", newStatus.getName()));
        String remark = appointment.getAdditionalInfo();
        data.put("thing4", Map.of("value", truncate(remark != null && !remark.isEmpty() ? remark : "无", 20)));

        boolean success = wxMiniProgramClient.sendSubscribeMessage(
                accessToken,
                userInfo.getOpenid(),
                appointmentStatusTemplateId,
                "/pages/appointment/list",
                data
        );

        if (success) {
            log.info("预约状态变更订阅消息发送成功，appointmentId: {}, userId: {}, status: {}",
                    appointment.getId(), appointment.getUserId(), newStatus.getName());
        } else {
            log.warn("预约状态变更订阅消息发送失败，appointmentId: {}, userId: {}",
                    appointment.getId(), appointment.getUserId());
        }
    }

    private String truncate(String str, int maxLen) {
        if (str == null) {
            return "";
        }
        if (str.length() > maxLen) {
            return str.substring(0, maxLen);
        }
        return str;
    }
}
