package com.xiaohong.lease.web.app.controller.mini;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiaohong.lease.common.utils.JWTUtil;
import com.xiaohong.lease.web.app.vo.rating.RatingSubmitVo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 小程序端评分控制器测试
 */
@SpringBootTest
@AutoConfigureMockMvc
public class MiniRatingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String getToken() {
        return JWTUtil.createToken(1L, "13800138001");
    }

    private String getTokenUser2() {
        return JWTUtil.createToken(2L, "13800138002");
    }

    /**
     * 测试获取评分统计
     */
    @Test
    public void testGetRatingStatistics() throws Exception {
        mockMvc.perform(get("/mini/rating/statistics/1")
                        .header("access_token", getToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.apartmentId").value(1))
                .andExpect(jsonPath("$.data.avgOverallScore").exists())
                .andExpect(jsonPath("$.data.totalCount").exists())
                .andExpect(jsonPath("$.data.scoreDistribution").exists())
                .andExpect(jsonPath("$.data.dimensionScores").exists())
                .andExpect(jsonPath("$.data.hasRated").exists());
    }

    /**
     * 测试未登录获取评分统计
     */
    @Test
    public void testGetRatingStatisticsWithoutToken() throws Exception {
        mockMvc.perform(get("/mini/rating/statistics/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(501));
    }

    /**
     * 测试分页查询评分列表
     */
    @Test
    public void testPageRatingItem() throws Exception {
        mockMvc.perform(get("/mini/rating/list/1")
                        .param("current", "1")
                        .param("size", "10")
                        .header("access_token", getToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.records").isArray())
                .andExpect(jsonPath("$.data.records[0].id").exists())
                .andExpect(jsonPath("$.data.records[0].userNickname").exists())
                .andExpect(jsonPath("$.data.records[0].overallScore").exists());
    }

    /**
     * 测试提交评分（用户2对公寓3评分，避免重复评分冲突）
     */
    @Test
    public void testSubmitRating() throws Exception {
        RatingSubmitVo submitVo = new RatingSubmitVo();
        submitVo.setApartmentId(3L);
        submitVo.setEnvironmentScore(new BigDecimal("4.5"));
        submitVo.setTrafficScore(new BigDecimal("4.0"));
        submitVo.setFacilityScore(new BigDecimal("4.5"));
        submitVo.setServiceScore(new BigDecimal("4.0"));
        submitVo.setValueScore(new BigDecimal("4.5"));
        submitVo.setContent("小程序端测试评分");
        submitVo.setIsAnonymous(0);

        mockMvc.perform(post("/mini/rating/submit")
                        .header("access_token", getTokenUser2())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(submitVo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    /**
     * 测试未登录提交评分被拦截
     */
    @Test
    public void testSubmitRatingWithoutToken() throws Exception {
        RatingSubmitVo submitVo = new RatingSubmitVo();
        submitVo.setApartmentId(1L);
        submitVo.setEnvironmentScore(new BigDecimal("5"));
        submitVo.setTrafficScore(new BigDecimal("5"));
        submitVo.setFacilityScore(new BigDecimal("5"));
        submitVo.setServiceScore(new BigDecimal("5"));
        submitVo.setValueScore(new BigDecimal("5"));

        mockMvc.perform(post("/mini/rating/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(submitVo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(501));
    }
}
