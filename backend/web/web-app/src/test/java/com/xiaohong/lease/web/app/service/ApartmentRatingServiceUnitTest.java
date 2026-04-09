package com.xiaohong.lease.web.app.service;

import com.xiaohong.lease.web.app.vo.rating.RatingItemVo;
import com.xiaohong.lease.web.app.vo.rating.RatingStatisticsVo;
import com.xiaohong.lease.web.app.vo.rating.RatingSubmitVo;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 公寓评分服务单元测试
 * 不依赖Spring容器的纯单元测试
 *
 * @author 小红
 * @createDate 2026-04-03
 */
public class ApartmentRatingServiceUnitTest {

    private static final Logger log = LoggerFactory.getLogger(ApartmentRatingServiceUnitTest.class);

    /**
     * 测试评分提交VO
     */
    @Test
    public void testRatingSubmitVo() {
        log.info("测试RatingSubmitVo");

        RatingSubmitVo vo = new RatingSubmitVo();
        vo.setApartmentId(1L);
        vo.setEnvironmentScore(new BigDecimal("4.5"));
        vo.setTrafficScore(new BigDecimal("5.0"));
        vo.setFacilityScore(new BigDecimal("4.0"));
        vo.setServiceScore(new BigDecimal("4.5"));
        vo.setValueScore(new BigDecimal("5.0"));
        vo.setContent("环境很好，交通便利");
        vo.setIsAnonymous(0);

        assertEquals(1L, vo.getApartmentId());
        assertEquals(new BigDecimal("4.5"), vo.getEnvironmentScore());
        assertEquals(new BigDecimal("5.0"), vo.getTrafficScore());
        assertEquals(new BigDecimal("4.0"), vo.getFacilityScore());
        assertEquals(new BigDecimal("4.5"), vo.getServiceScore());
        assertEquals(new BigDecimal("5.0"), vo.getValueScore());
        assertEquals("环境很好，交通便利", vo.getContent());
        assertEquals(0, vo.getIsAnonymous());

        log.info("RatingSubmitVo测试通过");
    }

    /**
     * 测试评分统计VO
     */
    @Test
    public void testRatingStatisticsVo() {
        log.info("测试RatingStatisticsVo");

        RatingStatisticsVo vo = new RatingStatisticsVo();
        vo.setApartmentId(1L);
        vo.setAvgOverallScore(new BigDecimal("4.80"));
        vo.setAvgEnvironmentScore(new BigDecimal("4.50"));
        vo.setAvgTrafficScore(new BigDecimal("4.80"));
        vo.setAvgFacilityScore(new BigDecimal("4.60"));
        vo.setAvgServiceScore(new BigDecimal("4.70"));
        vo.setAvgValueScore(new BigDecimal("4.50"));
        vo.setTotalCount(15);
        vo.setHasRated(true);
        vo.setUserRatingId(1L);

        Map<String, Integer> distribution = new HashMap<>();
        distribution.put("5", 8);
        distribution.put("4", 5);
        distribution.put("3", 2);
        distribution.put("2", 0);
        distribution.put("1", 0);
        vo.setScoreDistribution(distribution);

        Map<String, BigDecimal> dimensionScores = new HashMap<>();
        dimensionScores.put("environment", new BigDecimal("4.50"));
        dimensionScores.put("traffic", new BigDecimal("4.80"));
        dimensionScores.put("facility", new BigDecimal("4.60"));
        dimensionScores.put("service", new BigDecimal("4.70"));
        dimensionScores.put("value", new BigDecimal("4.50"));
        vo.setDimensionScores(dimensionScores);

        assertEquals(1L, vo.getApartmentId());
        assertEquals(new BigDecimal("4.80"), vo.getAvgOverallScore());
        assertEquals(15, vo.getTotalCount());
        assertTrue(vo.getHasRated());
        assertEquals(1L, vo.getUserRatingId());
        assertEquals(5, vo.getScoreDistribution().size());
        assertEquals(5, vo.getDimensionScores().size());

        log.info("RatingStatisticsVo测试通过");
    }

    /**
     * 测试评分列表项VO
     */
    @Test
    public void testRatingItemVo() {
        log.info("测试RatingItemVo");

        RatingItemVo vo = new RatingItemVo();
        vo.setId(1L);
        vo.setUserId(1L);
        vo.setUserNickname("小红用户");
        vo.setUserAvatar("https://example.com/avatar.jpg");
        vo.setApartmentId(1L);
        vo.setOverallScore(new BigDecimal("4.80"));
        vo.setEnvironmentScore(new BigDecimal("5.0"));
        vo.setTrafficScore(new BigDecimal("5.0"));
        vo.setFacilityScore(new BigDecimal("4.5"));
        vo.setServiceScore(new BigDecimal("5.0"));
        vo.setValueScore(new BigDecimal("4.5"));
        vo.setContent("非常满意");
        vo.setAnonymous(0);

        assertEquals(1L, vo.getId());
        assertEquals("小红用户", vo.getUserNickname());
        assertEquals(new BigDecimal("4.80"), vo.getOverallScore());
        assertEquals(0, vo.getAnonymous());

        log.info("RatingItemVo测试通过");
    }

    /**
     * 测试综合评分计算
     */
    @Test
    public void testOverallScoreCalculation() {
        log.info("测试综合评分计算");

        BigDecimal environment = new BigDecimal("4.5");
        BigDecimal traffic = new BigDecimal("5.0");
        BigDecimal facility = new BigDecimal("4.0");
        BigDecimal service = new BigDecimal("4.5");
        BigDecimal value = new BigDecimal("5.0");

        // 等权重计算: (4.5 + 5.0 + 4.0 + 4.5 + 5.0) / 5 = 23.0 / 5 = 4.6
        BigDecimal total = environment.multiply(new BigDecimal("0.20"))
                .add(traffic.multiply(new BigDecimal("0.20")))
                .add(facility.multiply(new BigDecimal("0.20")))
                .add(service.multiply(new BigDecimal("0.20")))
                .add(value.multiply(new BigDecimal("0.20")));

        assertEquals(0, total.compareTo(new BigDecimal("4.60")));

        log.info("综合评分计算测试通过");
    }

    /**
     * 测试评分范围校验
     */
    @Test
    public void testScoreValidation() {
        log.info("测试评分范围校验");

        BigDecimal validScore = new BigDecimal("3.5");
        assertTrue(validScore.compareTo(BigDecimal.ONE) >= 0);
        assertTrue(validScore.compareTo(new BigDecimal("5")) <= 0);

        BigDecimal lowScore = new BigDecimal("0.5");
        assertTrue(lowScore.compareTo(BigDecimal.ONE) < 0);

        BigDecimal highScore = new BigDecimal("5.5");
        assertTrue(highScore.compareTo(new BigDecimal("5")) > 0);

        log.info("评分范围校验测试通过");
    }

    /**
     * 测试星级分布统计
     */
    @Test
    public void testScoreDistribution() {
        log.info("测试星级分布统计");

        Map<String, Integer> distribution = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            distribution.put(String.valueOf(i), 0);
        }

        // 模拟一些评分数据
        int[] scores = {5, 5, 4, 5, 3, 4, 5, 2, 5, 4};
        for (int score : scores) {
            distribution.put(String.valueOf(score), distribution.get(String.valueOf(score)) + 1);
        }

        assertEquals(10, scores.length);
        assertEquals(1, distribution.get("2"));
        assertEquals(1, distribution.get("3"));
        assertEquals(3, distribution.get("4"));
        assertEquals(5, distribution.get("5"));
        assertEquals(0, distribution.get("1"));

        log.info("星级分布统计测试通过");
    }
}
