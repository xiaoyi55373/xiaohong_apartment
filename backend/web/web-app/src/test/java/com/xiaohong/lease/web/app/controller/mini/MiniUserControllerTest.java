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
 * 小程序端用户信息控制器测试
 */
@SpringBootTest
@AutoConfigureMockMvc
public class MiniUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String getToken() {
        return JWTUtil.createToken(1L, "13800138001");
    }

    /**
     * 测试获取用户信息
     */
    @Test
    public void testGetUserInfo() throws Exception {
        mockMvc.perform(get("/mini/user/info")
                        .header("access_token", getToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.phone").value("13800138001"))
                .andExpect(jsonPath("$.data.nickname").value("张三"));
    }

    /**
     * 测试未登录访问被拦截
     */
    @Test
    public void testGetUserInfoWithoutToken() throws Exception {
        mockMvc.perform(get("/mini/user/info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(501));
    }
}
