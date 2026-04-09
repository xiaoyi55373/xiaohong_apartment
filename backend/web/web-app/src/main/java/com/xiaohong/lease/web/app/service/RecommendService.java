package com.xiaohong.lease.web.app.service;

import com.xiaohong.lease.web.app.vo.recommend.RecommendQueryVo;
import com.xiaohong.lease.web.app.vo.recommend.RecommendRoomVo;
import com.xiaohong.lease.web.app.vo.recommend.UserPreferenceVo;

import java.util.List;

/**
 * 推荐服务接口
 * @author 小红
 * @createDate 2026-04-03
 */
public interface RecommendService {

    /**
     * 获取个性化推荐房间列表
     *
     * @param userId 用户ID
     * @param queryVo 查询条件
     * @return 推荐房间列表
     */
    List<RecommendRoomVo> getRecommendRooms(Long userId, RecommendQueryVo queryVo);

    /**
     * 获取基于内容的推荐
     *
     * @param userId 用户ID
     * @param limit 推荐数量
     * @return 推荐房间列表
     */
    List<RecommendRoomVo> getContentBasedRecommendations(Long userId, Integer limit);

    /**
     * 获取基于协同过滤的推荐
     *
     * @param userId 用户ID
     * @param limit 推荐数量
     * @return 推荐房间列表
     */
    List<RecommendRoomVo> getCollaborativeRecommendations(Long userId, Integer limit);

    /**
     * 获取热门推荐
     *
     * @param limit 推荐数量
     * @return 推荐房间列表
     */
    List<RecommendRoomVo> getHotRecommendations(Integer limit);

    /**
     * 获取相似房间推荐
     *
     * @param roomId 房间ID
     * @param limit 推荐数量
     * @return 推荐房间列表
     */
    List<RecommendRoomVo> getSimilarRooms(Long roomId, Integer limit);

    /**
     * 获取用户偏好分析
     *
     * @param userId 用户ID
     * @return 用户偏好
     */
    UserPreferenceVo getUserPreference(Long userId);

    /**
     * 刷新用户推荐缓存
     *
     * @param userId 用户ID
     */
    void refreshUserRecommendations(Long userId);
}
