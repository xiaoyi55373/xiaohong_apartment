package com.xiaohong.lease.web.app.controller.login;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiaohong.lease.common.utils.JWTUtil;
import com.xiaohong.lease.common.wx.WxMiniProgramClient;
import com.xiaohong.lease.web.app.vo.user.MiniProgramLoginVo;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 小程序登录接口测试
 */
@SpringBootTest
@AutoConfigureMockMvc
public class MiniProgramLoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private WxMiniProgramClient wxMiniProgramClient;

    /**
     * 测试新用户小程序登录：自动注册并返回 token
     */
    @Test
    public void testNewUserLogin() throws Exception {
        String code = "test_new_user_code";
        String openid = "openid_new_user_001";
        String sessionKey = "session_key_001";
        String unionid = "unionid_001";

        WxMiniProgramClient.Code2SessionResponse response = new WxMiniProgramClient.Code2SessionResponse();
        response.setOpenid(openid);
        response.setSessionKey(sessionKey);
        response.setUnionid(unionid);
        when(wxMiniProgramClient.code2Session(code)).thenReturn(response);

        MiniProgramLoginVo loginVo = new MiniProgramLoginVo();
        loginVo.setCode(code);

        MvcResult result = mockMvc.perform(post("/mini/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginVo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isString())
                .andReturn();

        String token = objectMapper.readTree(result.getResponse().getContentAsString()).get("data").asText();
        assertNotNull(token);

        Claims claims = JWTUtil.parseToken(token);
        assertNotNull(claims.get("userId", Long.class));
        assertTrue(claims.get("username", String.class).contains("微信用户"));
    }

    /**
     * 测试已有用户小程序登录：返回有效 token
     */
    @Test
    public void testExistingUserLogin() throws Exception {
        String code = "test_existing_user_code";
        // 使用 data-h2.sql 中预置的用户 openid（需与 schema 一致，但现有数据没有 openid，
        // 因此这里我们依赖 testNewUserLogin 创建的用户，或者使用一个独立的 openid）
        String openid = "openid_existing_user_002";
        String sessionKey = "session_key_002";

        WxMiniProgramClient.Code2SessionResponse response = new WxMiniProgramClient.Code2SessionResponse();
        response.setOpenid(openid);
        response.setSessionKey(sessionKey);
        when(wxMiniProgramClient.code2Session(code)).thenReturn(response);

        // 先创建用户
        MiniProgramLoginVo firstLogin = new MiniProgramLoginVo();
        firstLogin.setCode(code);
        mockMvc.perform(post("/mini/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(firstLogin)))
                .andExpect(status().isOk());

        // 再次登录（模拟换 session_key）
        String newCode = "test_existing_user_code_2";
        String newSessionKey = "session_key_002_new";
        WxMiniProgramClient.Code2SessionResponse newResponse = new WxMiniProgramClient.Code2SessionResponse();
        newResponse.setOpenid(openid);
        newResponse.setSessionKey(newSessionKey);
        when(wxMiniProgramClient.code2Session(newCode)).thenReturn(newResponse);

        MiniProgramLoginVo secondLogin = new MiniProgramLoginVo();
        secondLogin.setCode(newCode);

        MvcResult result = mockMvc.perform(post("/mini/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(secondLogin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn();

        String token = objectMapper.readTree(result.getResponse().getContentAsString()).get("data").asText();
        assertNotNull(token);
    }

    /**
     * 测试参数为空时登录失败
     */
    @Test
    public void testEmptyCodeLogin() throws Exception {
        MiniProgramLoginVo loginVo = new MiniProgramLoginVo();
        loginVo.setCode("");

        mockMvc.perform(post("/mini/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginVo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(202));
    }

    /**
     * 测试微信接口调用失败时登录失败
     */
    @Test
    public void testWxApiErrorLogin() throws Exception {
        String code = "test_wx_error_code";

        WxMiniProgramClient.Code2SessionResponse response = new WxMiniProgramClient.Code2SessionResponse();
        response.setErrcode(40029);
        response.setErrmsg("invalid code");
        when(wxMiniProgramClient.code2Session(anyString())).thenReturn(response);

        MiniProgramLoginVo loginVo = new MiniProgramLoginVo();
        loginVo.setCode(code);

        mockMvc.perform(post("/mini/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginVo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(203));
    }
}
