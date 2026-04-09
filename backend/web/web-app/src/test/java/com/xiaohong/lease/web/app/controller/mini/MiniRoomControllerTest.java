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
 * 小程序端房间信息控制器测试
 */
@SpringBootTest
@AutoConfigureMockMvc
public class MiniRoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String getToken() {
        return JWTUtil.createToken(1L, "13800138001");
    }

    /**
     * 测试分页查询房间列表
     */
    @Test
    public void testPageItem() throws Exception {
        mockMvc.perform(get("/mini/room/pageItem")
                        .param("current", "1")
                        .param("size", "10")
                        .header("access_token", getToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.records").isArray())
                .andExpect(jsonPath("$.data.records[0].id").exists())
                .andExpect(jsonPath("$.data.records[0].roomNumber").exists())
                .andExpect(jsonPath("$.data.records[0].rent").exists())
                .andExpect(jsonPath("$.data.records[0].apartmentName").exists())
                .andExpect(jsonPath("$.data.records[0].districtName").exists());
    }

    /**
     * 测试获取房间详情
     */
    @Test
    public void testGetDetailById() throws Exception {
        mockMvc.perform(get("/mini/room/getDetailById")
                        .param("id", "1")
                        .header("access_token", getToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.roomNumber").value("101"))
                .andExpect(jsonPath("$.data.rent").value(3500.00))
                .andExpect(jsonPath("$.data.apartmentName").exists())
                .andExpect(jsonPath("$.data.imageList").isArray())
                .andExpect(jsonPath("$.data.labelNames").isArray())
                .andExpect(jsonPath("$.data.facilityNames").isArray())
                .andExpect(jsonPath("$.data.isCheckIn").exists());
    }

    /**
     * 测试根据公寓ID查询房间列表
     */
    @Test
    public void testPageItemByApartmentId() throws Exception {
        mockMvc.perform(get("/mini/room/pageItemByApartmentId")
                        .param("current", "1")
                        .param("size", "10")
                        .param("id", "1")
                        .header("access_token", getToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.records").isArray())
                .andExpect(jsonPath("$.data.records[0].apartmentId").value(1));
    }

    /**
     * 测试未登录访问被拦截
     */
    @Test
    public void testPageItemWithoutToken() throws Exception {
        mockMvc.perform(get("/mini/room/pageItem")
                        .param("current", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(501));
    }
}
