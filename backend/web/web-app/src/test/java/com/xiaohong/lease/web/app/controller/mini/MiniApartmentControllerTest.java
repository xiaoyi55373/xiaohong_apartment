package com.xiaohong.lease.web.app.controller.mini;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiaohong.lease.common.utils.JWTUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 小程序端公寓信息控制器测试
 */
@SpringBootTest
@AutoConfigureMockMvc
public class MiniApartmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String getToken() {
        return JWTUtil.createToken(1L, "13800138001");
    }

    /**
     * 测试获取公寓详情
     */
    @Test
    public void testGetDetailById() throws Exception {
        mockMvc.perform(get("/mini/apartment/getDetailById")
                        .param("id", "1")
                        .header("access_token", getToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("小红公寓·阳光社区"))
                .andExpect(jsonPath("$.data.cityName").value("北京市"))
                .andExpect(jsonPath("$.data.districtName").value("朝阳区"))
                .andExpect(jsonPath("$.data.imageList").isArray())
                .andExpect(jsonPath("$.data.labelNames").isArray())
                .andExpect(jsonPath("$.data.facilityNames").isArray());
    }

    /**
     * 测试获取不存在的公寓详情
     */
    @Test
    public void testGetDetailByIdNotFound() throws Exception {
        mockMvc.perform(get("/mini/apartment/getDetailById")
                        .param("id", "9999")
                        .header("access_token", getToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    /**
     * 测试未登录访问被拦截
     */
    @Test
    public void testGetDetailWithoutToken() throws Exception {
        mockMvc.perform(get("/mini/apartment/getDetailById")
                        .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(501));
    }
}
