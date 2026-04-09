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
 * 小程序端租约协议控制器测试
 */
@SpringBootTest
@AutoConfigureMockMvc
public class MiniAgreementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String getToken() {
        return JWTUtil.createToken(1L, "13800138001");
    }

    /**
     * 测试查询租约协议列表
     */
    @Test
    public void testListItem() throws Exception {
        mockMvc.perform(get("/mini/agreement/listItem")
                        .header("access_token", getToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].apartmentName").exists());
    }

    /**
     * 测试获取租约协议详情
     */
    @Test
    public void testGetDetailById() throws Exception {
        mockMvc.perform(get("/mini/agreement/getDetailById")
                        .param("id", "1")
                        .header("access_token", getToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.apartmentName").exists())
                .andExpect(jsonPath("$.data.roomNumber").exists());
    }

    /**
     * 测试未登录访问被拦截
     */
    @Test
    public void testListItemWithoutToken() throws Exception {
        mockMvc.perform(get("/mini/agreement/listItem"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(501));
    }
}
