package com.xiaohong.lease.web.app.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaohong.lease.model.entity.UserBehaviorLog;
import com.xiaohong.lease.web.app.mapper.UserBehaviorLogMapper;
import com.xiaohong.lease.web.app.service.UserBehaviorLogService;
import com.xiaohong.lease.web.app.vo.log.BehaviorStatisticsVo;
import com.xiaohong.lease.web.app.vo.log.BehaviorTrendVo;
import com.xiaohong.lease.web.app.vo.log.HotApartmentVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * @author 小红
 * @description 针对表【user_behavior_log(用户行为日志)】的数据库操作Service实现
 */
@Service
@Slf4j
public class UserBehaviorLogServiceImpl extends ServiceImpl<UserBehaviorLogMapper, UserBehaviorLog>
        implements UserBehaviorLogService {

    @Override
    @Async("behaviorLogExecutor")
    public void asyncSave(UserBehaviorLog behaviorLog) {
        try {
            this.save(behaviorLog);
        } catch (Exception e) {
            log.error("异步保存用户行为日志失败", e);
        }
    }

    @Override
    public BehaviorStatisticsVo getStatistics(LocalDateTime startTime, LocalDateTime endTime) {
        BehaviorStatisticsVo vo = new BehaviorStatisticsVo();

        Long totalPv = baseMapper.selectPvCount(startTime, endTime);
        Long totalUv = baseMapper.selectUvCount(startTime, endTime);

        vo.setTotalPv(totalPv != null ? totalPv : 0L);
        vo.setTotalUv(totalUv != null ? totalUv : 0L);

        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        Long todayPv = baseMapper.selectPvCount(todayStart, todayEnd);
        Long todayUv = baseMapper.selectUvCount(todayStart, todayEnd);

        vo.setTodayPv(todayPv != null ? todayPv : 0L);
        vo.setTodayUv(todayUv != null ? todayUv : 0L);

        vo.setDistribution(getBehaviorDistribution(startTime, endTime));
        vo.setHotApartments(getHotApartments(startTime, endTime, 10));
        vo.setTrend(getBehaviorTrend(startTime, endTime));

        return vo;
    }

    @Override
    public List<HotApartmentVo> getHotApartments(LocalDateTime startTime, LocalDateTime endTime, Integer limit) {
        return baseMapper.selectHotApartments(startTime, endTime, limit);
    }

    @Override
    public List<BehaviorTrendVo> getBehaviorTrend(LocalDateTime startTime, LocalDateTime endTime) {
        return baseMapper.selectBehaviorTrend(startTime, endTime);
    }

    @Override
    public List<BehaviorTrendVo> getBehaviorDistribution(LocalDateTime startTime, LocalDateTime endTime) {
        return baseMapper.selectBehaviorDistribution(startTime, endTime);
    }
}
