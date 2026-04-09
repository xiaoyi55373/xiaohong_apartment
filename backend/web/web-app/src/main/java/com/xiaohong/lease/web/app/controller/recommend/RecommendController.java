package com.xiaohong.lease.web.app.controller.recommend;

import com.xiaohong.lease.common.circuitbreaker.annotation.CircuitBreaker;
import com.xiaohong.lease.common.context.LoginUserContext;
import com.xiaohong.lease.common.result.Result;
import com.xiaohong.lease.web.app.service.RecommendService;
import com.xiaohong.lease.web.app.vo.recommend.RecommendQueryVo;
import com.xiaohong.lease.web.app.vo.recommend.RecommendRoomVo;
import com.xiaohong.lease.web.app.vo.recommend.UserPreferenceVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 推荐控制器
 * 提供个性化房源推荐相关接口
 * 
 * @author 小红
 * @createDate 2026-04-03
 */
@Tag(name = "房源推荐", description = "个性化房源推荐相关接口")
@RestController
@RequestMapping("/app/recommend")
public class RecommendController {

    private static final Logger log = LoggerFactory.getLogger(RecommendController.class);

    @Autowired
    private RecommendService recommendService;

    /**
     * 获取个性化推荐房源列表
     */
    @Operation(summary = "获取个性化推荐房源", description = "根据用户浏览历史和行为数据，返回个性化推荐的房源列表")
    @GetMapping("/rooms")
    @CircuitBreaker(name = "recommendRooms", failureThreshold = 5, timeoutDuration = 30000, fallbackMethod = "getHotRecommendationsFallback")
    public Result<List<RecommendRoomVo>> getRecommendRooms(
            @Parameter(description = "推荐数量，默认10") @RequestParam(defaultValue = "10") Integer limit,
            @Parameter(description = "是否包含已浏览，默认false") @RequestParam(defaultValue = "false") Boolean includeViewed,
            @Parameter(description = "推荐类型：ALL/CONTENT/COLLABORATIVE/HOT，默认ALL") @RequestParam(defaultValue = "ALL") String type) {
        
        Long userId = LoginUserContext.getLoginUser().getUserId();
        log.info("获取个性化推荐房源, userId: {}, limit: {}, type: {}", userId, limit, type);

        RecommendQueryVo queryVo = new RecommendQueryVo();
        queryVo.setLimit(limit);
        queryVo.setIncludeViewed(includeViewed);
        queryVo.setType(type);

        List<RecommendRoomVo> recommendations = recommendService.getRecommendRooms(userId, queryVo);
        return Result.ok(recommendations);
    }

    /**
     * 获取基于内容的推荐
     */
    @Operation(summary = "获取基于内容的推荐", description = "根据用户历史浏览偏好，推荐相似房源")
    @GetMapping("/content-based")
    public Result<List<RecommendRoomVo>> getContentBasedRecommendations(
            @Parameter(description = "推荐数量，默认10") @RequestParam(defaultValue = "10") Integer limit) {
        
        Long userId = LoginUserContext.getLoginUser().getUserId();
        log.info("获取基于内容的推荐, userId: {}, limit: {}", userId, limit);

        List<RecommendRoomVo> recommendations = recommendService.getContentBasedRecommendations(userId, limit);
        return Result.ok(recommendations);
    }

    /**
     * 获取基于协同过滤的推荐
     */
    @Operation(summary = "获取协同过滤推荐", description = "根据相似用户的行为，推荐房源")
    @GetMapping("/collaborative")
    public Result<List<RecommendRoomVo>> getCollaborativeRecommendations(
            @Parameter(description = "推荐数量，默认10") @RequestParam(defaultValue = "10") Integer limit) {
        
        Long userId = LoginUserContext.getLoginUser().getUserId();
        log.info("获取协同过滤推荐, userId: {}, limit: {}", userId, limit);

        List<RecommendRoomVo> recommendations = recommendService.getCollaborativeRecommendations(userId, limit);
        return Result.ok(recommendations);
    }

    /**
     * 获取热门推荐
     */
    @Operation(summary = "获取热门推荐", description = "获取当前热门的房源列表")
    @GetMapping("/hot")
    public Result<List<RecommendRoomVo>> getHotRecommendations(
            @Parameter(description = "推荐数量，默认10") @RequestParam(defaultValue = "10") Integer limit) {
        
        log.info("获取热门推荐, limit: {}", limit);

        List<RecommendRoomVo> recommendations = recommendService.getHotRecommendations(limit);
        return Result.ok(recommendations);
    }

    /**
     * 获取相似房源推荐
     */
    @Operation(summary = "获取相似房源", description = "获取与指定房间相似的房源推荐")
    @GetMapping("/similar/{roomId}")
    public Result<List<RecommendRoomVo>> getSimilarRooms(
            @Parameter(description = "房间ID") @PathVariable Long roomId,
            @Parameter(description = "推荐数量，默认6") @RequestParam(defaultValue = "6") Integer limit) {
        
        log.info("获取相似房源, roomId: {}, limit: {}", roomId, limit);

        List<RecommendRoomVo> recommendations = recommendService.getSimilarRooms(roomId, limit);
        return Result.ok(recommendations);
    }

    /**
     * 获取用户偏好分析
     */
    @Operation(summary = "获取用户偏好", description = "获取当前用户的偏好分析数据")
    @GetMapping("/preference")
    public Result<UserPreferenceVo> getUserPreference() {
        
        Long userId = LoginUserContext.getLoginUser().getUserId();
        log.info("获取用户偏好, userId: {}", userId);

        UserPreferenceVo preference = recommendService.getUserPreference(userId);
        return Result.ok(preference);
    }

    /**
     * 刷新推荐缓存
     */
    @Operation(summary = "刷新推荐缓存", description = "手动刷新当前用户的推荐缓存数据")
    @PostMapping("/refresh")
    public Result<Void> refreshRecommendations() {
        
        Long userId = LoginUserContext.getLoginUser().getUserId();
        log.info("刷新推荐缓存, userId: {}", userId);

        recommendService.refreshUserRecommendations(userId);
        return Result.ok();
    }

    /**
     * 个性化推荐降级方法
     * 当推荐服务不可用时，返回热门推荐作为降级方案
     */
    private Result<List<RecommendRoomVo>> getHotRecommendationsFallback(
            Integer limit, Boolean includeViewed, String type) {
        log.warn("个性化推荐服务熔断，降级为热门推荐, limit: {}", limit);
        return getHotRecommendations(limit);
    }
}
