package com.xiaohong.lease.web.app.service;

import com.xiaohong.lease.web.app.vo.recommend.RecommendQueryVo;
import com.xiaohong.lease.web.app.vo.recommend.RecommendRoomVo;
import com.xiaohong.lease.web.app.vo.recommend.UserPreferenceVo;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 推荐服务测试类
 * 
 * @author 小红
 * @createDate 2026-04-03
 */
@SpringBootTest
public class RecommendServiceTest {

    private static final Logger log = LoggerFactory.getLogger(RecommendServiceTest.class);

    @Autowired
    private RecommendService recommendService;

    /**
     * 测试获取热门推荐
     */
    @Test
    public void testGetHotRecommendations() {
        log.info("测试获取热门推荐");
        
        List<RecommendRoomVo> recommendations = recommendService.getHotRecommendations(10);
        
        assertNotNull(recommendations);
        log.info("热门推荐数量: {}", recommendations.size());
        
        for (RecommendRoomVo room : recommendations) {
            log.info("房间ID: {}, 得分: {}, 类型: {}, 原因: {}",
                    room.getId(), room.getRecommendScore(), room.getRecommendType(), room.getRecommendReason());
            assertNotNull(room.getId());
            assertNotNull(room.getRecommendScore());
            assertNotNull(room.getRecommendType());
        }
    }

    /**
     * 测试获取相似房源
     */
    @Test
    public void testGetSimilarRooms() {
        log.info("测试获取相似房源");
        
        // 假设房间ID为1
        Long roomId = 1L;
        List<RecommendRoomVo> recommendations = recommendService.getSimilarRooms(roomId, 6);
        
        assertNotNull(recommendations);
        log.info("相似房源数量: {}", recommendations.size());
        
        for (RecommendRoomVo room : recommendations) {
            log.info("房间ID: {}, 得分: {}, 原因: {}",
                    room.getId(), room.getRecommendScore(), room.getRecommendReason());
            assertNotNull(room.getId());
            assertNotEquals(roomId, room.getId()); // 不应包含自身
        }
    }

    /**
     * 测试用户偏好分析
     */
    @Test
    public void testGetUserPreference() {
        log.info("测试用户偏好分析");
        
        // 假设用户ID为1
        Long userId = 1L;
        UserPreferenceVo preference = recommendService.getUserPreference(userId);
        
        assertNotNull(preference);
        log.info("用户ID: {}, 浏览数量: {}", preference.getUserId(), preference.getBrowseCount());
        
        if (preference.getBrowseCount() > 0) {
            log.info("平均租金: {}", preference.getAvgRent());
            log.info("偏好区域: {}", preference.getPreferredDistrictIds());
            log.info("偏好标签: {}", preference.getPreferredLabels());
            log.info("区域权重: {}", preference.getDistrictWeights());
            log.info("标签权重: {}", preference.getLabelWeights());
        }
    }

    /**
     * 测试基于内容的推荐
     */
    @Test
    public void testGetContentBasedRecommendations() {
        log.info("测试基于内容的推荐");
        
        Long userId = 1L;
        List<RecommendRoomVo> recommendations = recommendService.getContentBasedRecommendations(userId, 10);
        
        assertNotNull(recommendations);
        log.info("内容推荐数量: {}", recommendations.size());
        
        for (RecommendRoomVo room : recommendations) {
            log.info("房间ID: {}, 得分: {}, 类型: {}, 原因: {}",
                    room.getId(), room.getRecommendScore(), room.getRecommendType(), room.getRecommendReason());
            assertNotNull(room.getId());
            assertEquals("CONTENT", room.getRecommendType());
        }
    }

    /**
     * 测试基于协同过滤的推荐
     */
    @Test
    public void testGetCollaborativeRecommendations() {
        log.info("测试基于协同过滤的推荐");
        
        Long userId = 1L;
        List<RecommendRoomVo> recommendations = recommendService.getCollaborativeRecommendations(userId, 10);
        
        assertNotNull(recommendations);
        log.info("协同过滤推荐数量: {}", recommendations.size());
        
        for (RecommendRoomVo room : recommendations) {
            log.info("房间ID: {}, 得分: {}, 类型: {}, 原因: {}",
                    room.getId(), room.getRecommendScore(), room.getRecommendType(), room.getRecommendReason());
            assertNotNull(room.getId());
        }
    }

    /**
     * 测试混合推荐
     */
    @Test
    public void testGetHybridRecommendations() {
        log.info("测试混合推荐");
        
        Long userId = 1L;
        RecommendQueryVo queryVo = new RecommendQueryVo();
        queryVo.setLimit(10);
        queryVo.setType("ALL");
        queryVo.setIncludeViewed(false);
        
        List<RecommendRoomVo> recommendations = recommendService.getRecommendRooms(userId, queryVo);
        
        assertNotNull(recommendations);
        log.info("混合推荐数量: {}", recommendations.size());
        
        // 统计各类推荐数量
        long contentCount = recommendations.stream()
                .filter(r -> r.getRecommendType() != null && r.getRecommendType().contains("CONTENT"))
                .count();
        long collabCount = recommendations.stream()
                .filter(r -> r.getRecommendType() != null && r.getRecommendType().contains("COLLABORATIVE"))
                .count();
        long hotCount = recommendations.stream()
                .filter(r -> r.getRecommendType() != null && r.getRecommendType().contains("HOT"))
                .count();
        
        log.info("内容推荐: {}, 协同过滤: {}, 热门: {}", contentCount, collabCount, hotCount);
        
        // 验证推荐按得分排序
        for (int i = 0; i < recommendations.size() - 1; i++) {
            assertTrue(recommendations.get(i).getRecommendScore() >= recommendations.get(i + 1).getRecommendScore());
        }
    }

    /**
     * 测试刷新推荐缓存
     */
    @Test
    public void testRefreshUserRecommendations() {
        log.info("测试刷新推荐缓存");
        
        Long userId = 1L;
        
        // 先获取一次推荐，生成缓存
        List<RecommendRoomVo> recommendations1 = recommendService.getRecommendRooms(userId, new RecommendQueryVo());
        assertNotNull(recommendations1);
        
        // 刷新缓存
        recommendService.refreshUserRecommendations(userId);
        
        // 再次获取推荐
        List<RecommendRoomVo> recommendations2 = recommendService.getRecommendRooms(userId, new RecommendQueryVo());
        assertNotNull(recommendations2);
        
        log.info("刷新前推荐数量: {}, 刷新后推荐数量: {}", recommendations1.size(), recommendations2.size());
    }
}
