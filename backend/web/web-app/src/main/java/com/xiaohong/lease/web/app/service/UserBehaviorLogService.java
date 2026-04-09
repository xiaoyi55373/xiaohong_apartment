package com.xiaohong.lease.web.app.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaohong.lease.model.entity.UserBehaviorLog;
import com.xiaohong.lease.web.app.vo.log.BehaviorStatisticsVo;
import com.xiaohong.lease.web.app.vo.log.BehaviorTrendVo;
import com.xiaohong.lease.web.app.vo.log.HotApartmentVo;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author 小红
 * @description 针对表【user_behavior_log(用户行为日志)】的数据库操作Service
 */
public interface UserBehaviorLogService extends IService<UserBehaviorLog> {

    /**
     * 异步保存行为日志
     */
    void asyncSave(UserBehaviorLog behaviorLog);

    /**
     * 获取行为统计数据
     */
    BehaviorStatisticsVo getStatistics(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取热门房源排行
     */
    List<HotApartmentVo> getHotApartments(LocalDateTime startTime, LocalDateTime endTime, Integer limit);

    /**
     * 获取行为趋势
     */
    List<BehaviorTrendVo> getBehaviorTrend(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取行为分布
     */
    List<BehaviorTrendVo> getBehaviorDistribution(LocalDateTime startTime, LocalDateTime endTime);
}
