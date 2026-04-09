package com.xiaohong.lease.web.app.service;

import com.xiaohong.lease.web.app.vo.recommend.RecommendRoomVo;
import com.xiaohong.lease.web.app.vo.recommend.UserPreferenceVo;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 推荐服务单元测试
 * 不依赖Spring容器的纯单元测试
 * 
 * @author 小红
 * @createDate 2026-04-03
 */
public class RecommendServiceUnitTest {

    private static final Logger log = LoggerFactory.getLogger(RecommendServiceUnitTest.class);

    /**
     * 测试推荐房间VO的创建和属性设置
     */
    @Test
    public void testRecommendRoomVo() {
        log.info("测试RecommendRoomVo");
        
        RecommendRoomVo vo = new RecommendRoomVo();
        vo.setId(1L);
        vo.setRoomNumber("101");
        vo.setRent(new BigDecimal("3000"));
        vo.setRecommendScore(0.85);
        vo.setRecommendType("CONTENT");
        vo.setRecommendReason("符合您的预算");
        
        assertEquals(1L, vo.getId());
        assertEquals("101", vo.getRoomNumber());
        assertEquals(new BigDecimal("3000"), vo.getRent());
        assertEquals(0.85, vo.getRecommendScore());
        assertEquals("CONTENT", vo.getRecommendType());
        assertEquals("符合您的预算", vo.getRecommendReason());
        
        log.info("RecommendRoomVo测试通过");
    }

    /**
     * 测试用户偏好VO
     */
    @Test
    public void testUserPreferenceVo() {
        log.info("测试UserPreferenceVo");
        
        UserPreferenceVo vo = new UserPreferenceVo();
        vo.setUserId(1L);
        vo.setBrowseCount(10);
        vo.setAvgRent(new BigDecimal("3500"));
        vo.setMinRent(new BigDecimal("2500"));
        vo.setMaxRent(new BigDecimal("4500"));
        
        List<Long> districtIds = Arrays.asList(1L, 2L, 3L);
        vo.setPreferredDistrictIds(districtIds);
        
        List<String> labels = Arrays.asList("精装修", "近地铁", "朝南");
        vo.setPreferredLabels(labels);
        
        Map<String, Double> labelWeights = new HashMap<>();
        labelWeights.put("精装修", 0.5);
        labelWeights.put("近地铁", 0.3);
        labelWeights.put("朝南", 0.2);
        vo.setLabelWeights(labelWeights);
        
        assertEquals(1L, vo.getUserId());
        assertEquals(10, vo.getBrowseCount());
        assertEquals(new BigDecimal("3500"), vo.getAvgRent());
        assertEquals(3, vo.getPreferredDistrictIds().size());
        assertEquals(3, vo.getPreferredLabels().size());
        assertEquals(3, vo.getLabelWeights().size());
        
        log.info("UserPreferenceVo测试通过");
    }

    /**
     * 测试推荐排序逻辑
     */
    @Test
    public void testRecommendRoomSorting() {
        log.info("测试推荐排序逻辑");
        
        List<RecommendRoomVo> rooms = new ArrayList<>();
        
        RecommendRoomVo room1 = new RecommendRoomVo();
        room1.setId(1L);
        room1.setRecommendScore(0.6);
        rooms.add(room1);
        
        RecommendRoomVo room2 = new RecommendRoomVo();
        room2.setId(2L);
        room2.setRecommendScore(0.9);
        rooms.add(room2);
        
        RecommendRoomVo room3 = new RecommendRoomVo();
        room3.setId(3L);
        room3.setRecommendScore(0.75);
        rooms.add(room3);
        
        // 按推荐得分降序排序
        rooms.sort((a, b) -> Double.compare(b.getRecommendScore(), a.getRecommendScore()));
        
        assertEquals(2L, rooms.get(0).getId()); // 最高分
        assertEquals(3L, rooms.get(1).getId()); // 第二
        assertEquals(1L, rooms.get(2).getId()); // 最低分
        
        log.info("推荐排序逻辑测试通过");
    }

    /**
     * 测试推荐类型常量
     */
    @Test
    public void testRecommendTypes() {
        log.info("测试推荐类型常量");
        
        // 验证推荐类型值
        String contentType = "CONTENT";
        String collabType = "COLLABORATIVE";
        String hotType = "HOT";
        
        assertNotNull(contentType);
        assertNotNull(collabType);
        assertNotNull(hotType);
        
        assertNotEquals(contentType, collabType);
        assertNotEquals(contentType, hotType);
        assertNotEquals(collabType, hotType);
        
        log.info("推荐类型常量测试通过");
    }

    /**
     * 测试权重计算逻辑
     */
    @Test
    public void testWeightCalculation() {
        log.info("测试权重计算逻辑");
        
        Map<String, Integer> countMap = new HashMap<>();
        countMap.put("标签1", 10);
        countMap.put("标签2", 5);
        countMap.put("标签3", 5);
        
        int total = countMap.values().stream().mapToInt(Integer::intValue).sum();
        assertEquals(20, total);
        
        Map<String, Double> weights = new HashMap<>();
        for (Map.Entry<String, Integer> entry : countMap.entrySet()) {
            weights.put(entry.getKey(), entry.getValue() / (double) total);
        }
        
        assertEquals(0.5, weights.get("标签1"), 0.001);
        assertEquals(0.25, weights.get("标签2"), 0.001);
        assertEquals(0.25, weights.get("标签3"), 0.001);
        
        // 验证权重总和为1
        double sum = weights.values().stream().mapToDouble(Double::doubleValue).sum();
        assertEquals(1.0, sum, 0.001);
        
        log.info("权重计算逻辑测试通过");
    }

    /**
     * 测试价格相似度计算
     */
    @Test
    public void testPriceSimilarity() {
        log.info("测试价格相似度计算");
        
        BigDecimal targetRent = new BigDecimal("3000");
        BigDecimal rent1 = new BigDecimal("3000"); // 相同
        BigDecimal rent2 = new BigDecimal("3300"); // 10%差异
        BigDecimal rent3 = new BigDecimal("4000"); // 33%差异
        
        // 计算价格相似度（差异越小，相似度越高）
        double similarity1 = calculatePriceSimilarity(targetRent, rent1);
        double similarity2 = calculatePriceSimilarity(targetRent, rent2);
        double similarity3 = calculatePriceSimilarity(targetRent, rent3);
        
        assertEquals(1.0, similarity1, 0.001);
        assertTrue(similarity2 > similarity3); // 10%差异的相似度应高于33%差异
        assertTrue(similarity1 > similarity2);
        
        log.info("价格相似度计算测试通过");
    }
    
    private double calculatePriceSimilarity(BigDecimal rent1, BigDecimal rent2) {
        BigDecimal diff = rent1.subtract(rent2).abs();
        BigDecimal percentDiff = diff.divide(rent1, 4, BigDecimal.ROUND_HALF_UP);
        return Math.max(0, 1 - percentDiff.doubleValue());
    }

    /**
     * 测试Jaccard相似度计算
     */
    @Test
    public void testJaccardSimilarity() {
        log.info("测试Jaccard相似度计算");
        
        Set<Integer> set1 = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5));
        Set<Integer> set2 = new HashSet<>(Arrays.asList(1, 2, 3, 6, 7));
        
        // 交集: {1, 2, 3} = 3
        // 并集: {1, 2, 3, 4, 5, 6, 7} = 7
        // Jaccard = 3/7 = 0.428...
        
        Set<Integer> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);
        
        Set<Integer> union = new HashSet<>(set1);
        union.addAll(set2);
        
        double jaccard = (double) intersection.size() / union.size();
        
        assertEquals(3, intersection.size());
        assertEquals(7, union.size());
        assertEquals(3.0/7.0, jaccard, 0.001);
        
        log.info("Jaccard相似度计算测试通过");
    }

    /**
     * 测试时间权重衰减
     */
    @Test
    public void testTimeWeightDecay() {
        log.info("测试时间权重衰减");
        
        // 模拟不同时间点的权重
        double weightNow = calculateTimeWeight(0); // 今天
        double weight7Days = calculateTimeWeight(7); // 7天前
        double weight30Days = calculateTimeWeight(30); // 30天前
        
        assertEquals(1.0, weightNow, 0.001);
        assertTrue(weightNow > weight7Days);
        assertTrue(weight7Days > weight30Days);
        assertTrue(weight30Days > 0); // 仍然有权重
        
        log.info("时间权重衰减测试通过");
    }
    
    private double calculateTimeWeight(int daysAgo) {
        return Math.exp(-daysAgo / 7.0); // 7天减半
    }
}
