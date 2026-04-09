package com.xiaohong.lease.web.app.controller.mini;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiaohong.lease.common.utils.JWTUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 小程序端预约看房控制器测试
 */
@SpringBootTest
@AutoConfigureMockMvc
public class MiniAppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String getToken() {
        return JWTUtil.createToken(1L, "13800138001");
    }

    /**
     * 测试查询预约列表
     */
    @Test
    public void testListItem() throws Exception {
        mockMvc.perform(get("/mini/appointment/listItem")
                        .header("access_token", getToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }

    /**
     * 测试获取预约详情
     */
    @Test
    public void testGetDetailById() throws Exception {
        mockMvc.perform(get("/mini/appointment/getDetailById")
                        .param("id", "1")
                        .header("access_token", getToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.apartmentItemVo.id").value(1));
    }

    /**
     * 测试保存或更新预约
     */
    @Test
    public void testSaveOrUpdate() throws Exception {
        String jsonBody = "{\"apartmentId\":1,\"appointmentTime\":\"2026-04-10 10:00:00\",\"name\":\"测试\",\"phone\":\"13800138001\"}";
        mockMvc.perform(post("/mini/appointment/saveOrUpdate")
                        .header("access_token", getToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    /**
     * 测试未登录访问被拦截
     */
    @Test
    public void testListItemWithoutToken() throws Exception {
        mockMvc.perform(get("/mini/appointment/listItem"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(501));
    }
}
