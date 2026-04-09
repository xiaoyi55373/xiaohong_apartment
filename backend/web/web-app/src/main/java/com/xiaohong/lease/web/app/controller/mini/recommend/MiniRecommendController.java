package com.xiaohong.lease.web.app.controller.mini.recommend;

import com.xiaohong.lease.common.circuitbreaker.annotation.CircuitBreaker;
import com.xiaohong.lease.common.context.LoginUserContext;
import com.xiaohong.lease.common.result.Result;
import com.xiaohong.lease.model.entity.LabelInfo;
import com.xiaohong.lease.web.app.service.RecommendService;
import com.xiaohong.lease.web.app.vo.recommend.RecommendQueryVo;
import com.xiaohong.lease.web.app.vo.recommend.RecommendRoomVo;
import com.xiaohong.lease.web.app.vo.mini.recommend.MiniRecommendRoomVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 小程序端推荐控制器
 * 提供个性化房源推荐相关接口，返回字段适配小程序端展示
 *
 * @author 小红
 * @createDate 2026-04-07
 */
@RestController
@Tag(name = "小程序-房源推荐", description = "小程序端个性化房源推荐相关接口")
@RequestMapping("/mini/recommend")
public class MiniRecommendController {

    private static final Logger log = LoggerFactory.getLogger(MiniRecommendController.class);

    @Autowired
    private RecommendService recommendService;

    /**
     * 获取个性化推荐房源列表
     */
    @Operation(summary = "获取个性化推荐房源", description = "根据用户浏览历史和行为数据，返回个性化推荐的房源列表")
    @GetMapping("/rooms")
    @CircuitBreaker(name = "miniRecommendRooms", failureThreshold = 5, timeoutDuration = 30000, fallbackMethod = "getHotRecommendationsFallback")
    public Result<List<MiniRecommendRoomVo>> getRecommendRooms(
            @Parameter(description = "推荐数量，默认10") @RequestParam(defaultValue = "10") Integer limit,
            @Parameter(description = "是否包含已浏览，默认false") @RequestParam(defaultValue = "false") Boolean includeViewed,
            @Parameter(description = "推荐类型：ALL/CONTENT/COLLABORATIVE/HOT，默认ALL") @RequestParam(defaultValue = "ALL") String type) {

        Long userId = LoginUserContext.getLoginUser().getUserId();
        log.info("小程序获取个性化推荐房源, userId: {}, limit: {}, type: {}", userId, limit, type);

        RecommendQueryVo queryVo = new RecommendQueryVo();
        queryVo.setLimit(limit);
        queryVo.setIncludeViewed(includeViewed);
        queryVo.setType(type);

        List<RecommendRoomVo> recommendations = recommendService.getRecommendRooms(userId, queryVo);
        List<MiniRecommendRoomVo> miniList = recommendations.stream()
                .map(this::convertToMiniRecommendRoomVo)
                .collect(Collectors.toList());

        return Result.ok(miniList);
    }

    /**
     * 获取热门推荐
     */
    @Operation(summary = "获取热门推荐", description = "获取当前热门的房源列表")
    @GetMapping("/hot")
    public Result<List<MiniRecommendRoomVo>> getHotRecommendations(
            @Parameter(description = "推荐数量，默认10") @RequestParam(defaultValue = "10") Integer limit) {

        log.info("小程序获取热门推荐, limit: {}", limit);

        List<RecommendRoomVo> recommendations = recommendService.getHotRecommendations(limit);
        List<MiniRecommendRoomVo> miniList = recommendations.stream()
                .map(this::convertToMiniRecommendRoomVo)
                .collect(Collectors.toList());

        return Result.ok(miniList);
    }

    /**
     * 获取相似房源推荐
     */
    @Operation(summary = "获取相似房源", description = "获取与指定房间相似的房源推荐")
    @GetMapping("/similar/{roomId}")
    public Result<List<MiniRecommendRoomVo>> getSimilarRooms(
            @Parameter(description = "房间ID") @PathVariable Long roomId,
            @Parameter(description = "推荐数量，默认6") @RequestParam(defaultValue = "6") Integer limit) {

        log.info("小程序获取相似房源, roomId: {}, limit: {}", roomId, limit);

        List<RecommendRoomVo> recommendations = recommendService.getSimilarRooms(roomId, limit);
        List<MiniRecommendRoomVo> miniList = recommendations.stream()
                .map(this::convertToMiniRecommendRoomVo)
                .collect(Collectors.toList());

        return Result.ok(miniList);
    }

    /**
     * 将 RecommendRoomVo 转换为 MiniRecommendRoomVo
     */
    private MiniRecommendRoomVo convertToMiniRecommendRoomVo(RecommendRoomVo vo) {
        MiniRecommendRoomVo miniVo = new MiniRecommendRoomVo();
        miniVo.setId(vo.getId());
        miniVo.setRoomNumber(vo.getRoomNumber());
        miniVo.setRent(vo.getRent());
        miniVo.setRecommendScore(vo.getRecommendScore());
        miniVo.setRecommendReason(vo.getRecommendReason());
        miniVo.setRecommendType(vo.getRecommendType());

        // 首图
        if (vo.getGraphVoList() != null && !vo.getGraphVoList().isEmpty()) {
            miniVo.setFirstImage(vo.getGraphVoList().get(0).getUrl());
        }

        // 标签名称列表
        if (vo.getLabelInfoList() != null) {
            miniVo.setLabelNames(vo.getLabelInfoList().stream()
                    .map(LabelInfo::getName)
                    .collect(Collectors.toList()));
        }

        // 公寓信息裁剪
        if (vo.getApartmentInfo() != null) {
            miniVo.setApartmentId(vo.getApartmentInfo().getId());
            miniVo.setApartmentName(vo.getApartmentInfo().getName());
            miniVo.setDistrictName(vo.getApartmentInfo().getDistrictName());
        }

        return miniVo;
    }

    /**
     * 个性化推荐降级方法
     */
    private Result<List<MiniRecommendRoomVo>> getHotRecommendationsFallback(
            Integer limit, Boolean includeViewed, String type) {
        log.warn("小程序个性化推荐服务熔断，降级为热门推荐, limit: {}", limit);
        return getHotRecommendations(limit);
    }
}
