package com.xiaohong.lease.web.app.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaohong.lease.model.entity.UserBehaviorLog;
import com.xiaohong.lease.model.enums.BehaviorType;
import com.xiaohong.lease.web.app.vo.log.BehaviorTrendVo;
import com.xiaohong.lease.web.app.vo.log.HotApartmentVo;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author 小红
 * @description 针对表【user_behavior_log(用户行为日志)】的数据库操作Mapper
 */
public interface UserBehaviorLogMapper extends BaseMapper<UserBehaviorLog> {

    /**
     * 查询指定日期范围内的PV数
     */
    Long selectPvCount(@Param("startTime") LocalDateTime startTime,
                       @Param("endTime") LocalDateTime endTime);

    /**
     * 查询指定日期范围内的UV数
     */
    Long selectUvCount(@Param("startTime") LocalDateTime startTime,
                       @Param("endTime") LocalDateTime endTime);

    /**
     * 查询热门房源排行
     */
    List<HotApartmentVo> selectHotApartments(@Param("startTime") LocalDateTime startTime,
                                             @Param("endTime") LocalDateTime endTime,
                                             @Param("limit") Integer limit);

    /**
     * 查询行为趋势
     */
    List<BehaviorTrendVo> selectBehaviorTrend(@Param("startTime") LocalDateTime startTime,
                                              @Param("endTime") LocalDateTime endTime);

    /**
     * 查询行为分布统计
     */
    List<BehaviorTrendVo> selectBehaviorDistribution(@Param("startTime") LocalDateTime startTime,
                                                     @Param("endTime") LocalDateTime endTime);
}
