package com.xiaohong.lease.web.app.controller.mini;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiaohong.lease.common.utils.JWTUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 小程序端推荐控制器测试
 */
@SpringBootTest
@AutoConfigureMockMvc
public class MiniRecommendControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String getToken() {
        return JWTUtil.createToken(1L, "13800138001");
    }

    /**
     * 测试获取个性化推荐房源
     */
    @Test
    public void testGetRecommendRooms() throws Exception {
        mockMvc.perform(get("/mini/recommend/rooms")
                        .param("limit", "5")
                        .param("type", "HOT")
                        .header("access_token", getToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").exists())
                .andExpect(jsonPath("$.data[0].roomNumber").exists())
                .andExpect(jsonPath("$.data[0].rent").exists())
                .andExpect(jsonPath("$.data[0].apartmentName").exists())
                .andExpect(jsonPath("$.data[0].districtName").exists());
    }

    /**
     * 测试获取热门推荐
     */
    @Test
    public void testGetHotRecommendations() throws Exception {
        mockMvc.perform(get("/mini/recommend/hot")
                        .param("limit", "5")
                        .header("access_token", getToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").exists())
                .andExpect(jsonPath("$.data[0].apartmentName").exists());
    }

    /**
     * 测试获取相似房源
     */
    @Test
    public void testGetSimilarRooms() throws Exception {
        mockMvc.perform(get("/mini/recommend/similar/1")
                        .param("limit", "3")
                        .header("access_token", getToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }

    /**
     * 测试未登录访问被拦截
     */
    @Test
    public void testGetHotRecommendationsWithoutToken() throws Exception {
        mockMvc.perform(get("/mini/recommend/hot")
                        .param("limit", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(501));
    }
}
