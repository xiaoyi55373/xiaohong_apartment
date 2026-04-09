package com.xiaohong.lease.web.app.controller.mini.rating;

import com.xiaohong.lease.common.context.LoginUserContext;
import com.xiaohong.lease.common.log.annotation.BehaviorLog;
import com.xiaohong.lease.common.result.Result;
import com.xiaohong.lease.model.enums.BehaviorType;
import com.xiaohong.lease.web.app.service.ApartmentRatingService;
import com.xiaohong.lease.web.app.vo.rating.RatingItemVo;
import com.xiaohong.lease.web.app.vo.rating.RatingQueryVo;
import com.xiaohong.lease.web.app.vo.rating.RatingStatisticsVo;
import com.xiaohong.lease.web.app.vo.rating.RatingSubmitVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 小程序端公寓评分控制器
 * 返回字段适配小程序端展示
 *
 * @author 小红
 * @createDate 2026-04-07
 */
@RestController
@Tag(name = "小程序-公寓评分", description = "小程序端公寓多维度评分相关接口")
@RequestMapping("/mini/rating")
public class MiniRatingController {

    private static final Logger log = LoggerFactory.getLogger(MiniRatingController.class);

    @Autowired
    private ApartmentRatingService apartmentRatingService;

    /**
     * 提交评分
     */
    @Operation(summary = "提交评分", description = "对公寓进行多维度评分")
    @BehaviorLog(value = BehaviorType.SUBMIT, desc = "小程序提交公寓评分", targetType = "rating", targetId = "#submitVo.apartmentId")
    @PostMapping("/submit")
    public Result<Void> submitRating(@RequestBody RatingSubmitVo submitVo) {
        Long userId = LoginUserContext.getLoginUser().getUserId();
        log.info("小程序提交评分, userId: {}, apartmentId: {}", userId, submitVo.getApartmentId());
        apartmentRatingService.submitRating(userId, submitVo);
        return Result.ok();
    }

    /**
     * 获取公寓评分统计
     */
    @Operation(summary = "获取评分统计", description = "获取公寓的综合评分统计和各维度评分分布")
    @GetMapping("/statistics/{apartmentId}")
    public Result<RatingStatisticsVo> getRatingStatistics(
            @Parameter(description = "公寓ID") @PathVariable Long apartmentId) {
        Long userId = null;
        try {
            userId = LoginUserContext.getLoginUser().getUserId();
        } catch (Exception e) {
            // 未登录用户也可以查看评分统计
        }
        log.info("小程序获取评分统计, apartmentId: {}", apartmentId);
        RatingStatisticsVo statistics = apartmentRatingService.getRatingStatistics(userId, apartmentId);
        return Result.ok(statistics);
    }

    /**
     * 分页查询公寓评分列表
     */
    @Operation(summary = "获取评分列表", description = "分页查询公寓的评分列表")
    @GetMapping("/list/{apartmentId}")
    public Result<IPage<RatingItemVo>> pageRatingItem(
            @Parameter(description = "公寓ID") @PathVariable Long apartmentId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") long size,
            @Parameter(description = "筛选类型") @RequestParam(required = false) Integer filterType,
            @Parameter(description = "排序方式") @RequestParam(required = false) String sortType) {

        log.info("小程序获取评分列表, apartmentId: {}, current: {}, size: {}", apartmentId, current, size);

        Page<RatingItemVo> page = new Page<>(current, size);
        RatingQueryVo queryVo = new RatingQueryVo();
        queryVo.setApartmentId(apartmentId);
        queryVo.setFilterType(filterType);
        queryVo.setSortType(sortType);

        IPage<RatingItemVo> result = apartmentRatingService.pageRatingItem(page, queryVo);
        return Result.ok(result);
    }

    /**
     * 删除评分
     */
    @Operation(summary = "删除评分", description = "删除当前用户对公寓的评分")
    @BehaviorLog(value = BehaviorType.SUBMIT, desc = "小程序删除公寓评分", targetType = "rating", targetId = "#ratingId")
    @DeleteMapping("/delete/{ratingId}")
    public Result<Void> deleteRating(
            @Parameter(description = "评分ID") @PathVariable Long ratingId) {
        Long userId = LoginUserContext.getLoginUser().getUserId();
        log.info("小程序删除评分, userId: {}, ratingId: {}", userId, ratingId);
        apartmentRatingService.deleteRating(userId, ratingId);
        return Result.ok();
    }
}
