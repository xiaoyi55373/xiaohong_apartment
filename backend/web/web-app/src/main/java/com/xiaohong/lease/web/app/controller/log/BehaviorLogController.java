package com.xiaohong.lease.web.app.controller.log;

import com.xiaohong.lease.common.result.Result;
import com.xiaohong.lease.web.app.service.UserBehaviorLogService;
import com.xiaohong.lease.web.app.vo.log.BehaviorStatisticsVo;
import com.xiaohong.lease.web.app.vo.log.BehaviorTrendVo;
import com.xiaohong.lease.web.app.vo.log.HotApartmentVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户行为日志分析控制器
 *
 * @author 小红
 */
@RestController
@Tag(name = "用户行为分析")
@RequestMapping("/app/behavior")
public class BehaviorLogController {

    @Autowired
    private UserBehaviorLogService userBehaviorLogService;

    @Operation(summary = "获取行为统计数据")
    @GetMapping("/statistics")
    public Result<BehaviorStatisticsVo> getStatistics(
            @Parameter(description = "开始时间")
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间")
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {

        if (startTime == null) {
            startTime = LocalDateTime.now().minusDays(30);
        }
        if (endTime == null) {
            endTime = LocalDateTime.now();
        }

        BehaviorStatisticsVo statistics = userBehaviorLogService.getStatistics(startTime, endTime);
        return Result.ok(statistics);
    }

    @Operation(summary = "获取热门房源排行")
    @GetMapping("/hot-apartments")
    public Result<List<HotApartmentVo>> getHotApartments(
            @Parameter(description = "开始时间")
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间")
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @Parameter(description = "限制数量")
            @RequestParam(defaultValue = "10") Integer limit) {

        if (startTime == null) {
            startTime = LocalDateTime.now().minusDays(30);
        }
        if (endTime == null) {
            endTime = LocalDateTime.now();
        }

        List<HotApartmentVo> list = userBehaviorLogService.getHotApartments(startTime, endTime, limit);
        return Result.ok(list);
    }

    @Operation(summary = "获取行为趋势")
    @GetMapping("/trend")
    public Result<List<BehaviorTrendVo>> getBehaviorTrend(
            @Parameter(description = "开始时间")
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间")
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {

        if (startTime == null) {
            startTime = LocalDateTime.now().minusDays(30);
        }
        if (endTime == null) {
            endTime = LocalDateTime.now();
        }

        List<BehaviorTrendVo> list = userBehaviorLogService.getBehaviorTrend(startTime, endTime);
        return Result.ok(list);
    }

    @Operation(summary = "获取行为分布")
    @GetMapping("/distribution")
    public Result<List<BehaviorTrendVo>> getBehaviorDistribution(
            @Parameter(description = "开始时间")
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间")
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {

        if (startTime == null) {
            startTime = LocalDateTime.now().minusDays(30);
        }
        if (endTime == null) {
            endTime = LocalDateTime.now();
        }

        List<BehaviorTrendVo> list = userBehaviorLogService.getBehaviorDistribution(startTime, endTime);
        return Result.ok(list);
    }
}
