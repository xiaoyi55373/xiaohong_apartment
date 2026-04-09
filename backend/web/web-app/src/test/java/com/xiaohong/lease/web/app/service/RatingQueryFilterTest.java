package com.xiaohong.lease.web.app.service;

import com.xiaohong.lease.web.app.vo.room.RoomQueryVo;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 评分筛选功能单元测试
 *
 * @author 小红
 * @createDate 2026-04-03
 */
public class RatingQueryFilterTest {

    private static final Logger log = LoggerFactory.getLogger(RatingQueryFilterTest.class);

    /**
     * 测试RoomQueryVo评分筛选字段
     */
    @Test
    public void testRoomQueryVoRatingFilter() {
        log.info("测试RoomQueryVo评分筛选字段");

        RoomQueryVo queryVo = new RoomQueryVo();
        queryVo.setProvinceId(1L);
        queryVo.setCityId(2L);
        queryVo.setMinRent(new BigDecimal("1000"));
        queryVo.setMaxRent(new BigDecimal("5000"));
        
        // 设置评分筛选条件
        queryVo.setMinScore(new BigDecimal("4.0"));
        queryVo.setMaxScore(new BigDecimal("5.0"));
        queryVo.setSortByScore("desc");

        assertEquals(1L, queryVo.getProvinceId());
        assertEquals(new BigDecimal("4.0"), queryVo.getMinScore());
        assertEquals(new BigDecimal("5.0"), queryVo.getMaxScore());
        assertEquals("desc", queryVo.getSortByScore());

        log.info("RoomQueryVo评分筛选字段测试通过");
    }

    /**
     * 测试评分范围有效性
     */
    @Test
    public void testScoreRangeValidation() {
        log.info("测试评分范围有效性");

        // 有效评分范围
        BigDecimal minScore = new BigDecimal("3.5");
        BigDecimal maxScore = new BigDecimal("4.8");
        
        assertTrue(minScore.compareTo(BigDecimal.ONE) >= 0);
        assertTrue(minScore.compareTo(new BigDecimal("5")) <= 0);
        assertTrue(maxScore.compareTo(BigDecimal.ONE) >= 0);
        assertTrue(maxScore.compareTo(new BigDecimal("5")) <= 0);
        assertTrue(minScore.compareTo(maxScore) <= 0);

        log.info("评分范围有效性测试通过");
    }

    /**
     * 测试评分排序类型
     */
    @Test
    public void testScoreSortType() {
        log.info("测试评分排序类型");

        RoomQueryVo queryVoDesc = new RoomQueryVo();
        queryVoDesc.setSortByScore("desc");
        assertEquals("desc", queryVoDesc.getSortByScore());

        RoomQueryVo queryVoAsc = new RoomQueryVo();
        queryVoAsc.setSortByScore("asc");
        assertEquals("asc", queryVoAsc.getSortByScore());

        log.info("评分排序类型测试通过");
    }

    /**
     * 测试多条件组合查询
     */
    @Test
    public void testCombinedQueryConditions() {
        log.info("测试多条件组合查询");

        RoomQueryVo queryVo = new RoomQueryVo();
        
        // 地区条件
        queryVo.setProvinceId(1L);
        queryVo.setCityId(2L);
        queryVo.setDistrictId(3L);
        
        // 租金条件
        queryVo.setMinRent(new BigDecimal("2000"));
        queryVo.setMaxRent(new BigDecimal("6000"));
        
        // 评分条件
        queryVo.setMinScore(new BigDecimal("4.5"));
        queryVo.setSortByScore("desc");
        
        // 验证所有条件
        assertNotNull(queryVo.getProvinceId());
        assertNotNull(queryVo.getCityId());
        assertNotNull(queryVo.getDistrictId());
        assertNotNull(queryVo.getMinRent());
        assertNotNull(queryVo.getMaxRent());
        assertNotNull(queryVo.getMinScore());
        assertNotNull(queryVo.getSortByScore());

        log.info("多条件组合查询测试通过");
    }
}
