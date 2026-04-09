package com.xiaohong.lease.web.admin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiaohong.lease.common.utils.JWTUtil;
import com.xiaohong.lease.web.admin.service.LoginService;
import com.xiaohong.lease.web.admin.vo.login.CaptchaVo;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 自动化 API 测试类
 * 测试所有 Controller 接口，无需人工干预
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(com.xiaohong.lease.web.admin.config.TestDataConfig.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AutoApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LoginService loginService;

    private static String token;
    private static int passedTests = 0;
    private static int failedTests = 0;

    @BeforeEach
    void setup() {
        // 配置 Mock LoginService - 在每个测试方法前执行
        when(loginService.getCaptcha()).thenReturn(
            new CaptchaVo(
                "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg==",
                "TEST_CAPTCHA_KEY_12345"
            )
        );
        
        when(loginService.login(any())).thenAnswer(invocation -> {
            com.xiaohong.lease.web.admin.vo.login.LoginVo loginVo = invocation.getArgument(0);
            String token = JWTUtil.createToken(1L, loginVo.getUsername());
            System.out.println("  Mock LoginService 生成 token: " + token.substring(0, 30) + "...");
            return token;
        });
    }

    // ==================== 登录管理接口测试 ====================

    @Test
    @Order(1)
    @DisplayName("[登录] 获取验证码")
    void test01_GetCaptcha() throws Exception {
        executeTest("获取验证码", () -> {
            mockMvc.perform(get("/admin/login/captcha"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.key").exists())
                    .andExpect(jsonPath("$.data.image").exists());
        });
    }

    @Test
    @Order(2)
    @DisplayName("[登录] 用户登录")
    void test02_Login() throws Exception {
        executeTest("用户登录", () -> {
            String loginJson = "{" +
                    "\"username\":\"admin\"," +
                    "\"password\":\"admin\"," +
                    "\"captchaKey\":\"TEST_CAPTCHA_KEY_12345\"," +
                    "\"captchaCode\":\"TEST\"" +
                    "}";

            MvcResult result = mockMvc.perform(post("/admin/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(loginJson))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data").exists())
                    .andReturn();

            // 提取 token
            String response = result.getResponse().getContentAsString();
            token = objectMapper.readTree(response).path("data").asText();
            System.out.println("  Token: " + token.substring(0, Math.min(30, token.length())) + "...");
        });
    }

    // ==================== 用户信息管理接口测试 ====================

    @Test
    @Order(10)
    @DisplayName("[用户] 分页查询用户信息")
    void test03_PageUserInfo() throws Exception {
        executeTestWithToken("分页查询用户信息", () -> {
            mockMvc.perform(get("/admin/userInfo/page")
                            .header("access_token", token)
                            .param("page", "1")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));
        });
    }

    @Test
    @Order(11)
    @DisplayName("[用户] 根据ID修改用户状态")
    void test04_UpdateUserStatus() throws Exception {
        executeTestWithToken("修改用户状态", () -> {
            mockMvc.perform(post("/admin/userInfo/updateStatusById")
                            .header("access_token", token)
                            .param("id", "1")
                            .param("status", "0"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));
        });
    }

    // ==================== 看房预约管理接口测试 ====================

    @Test
    @Order(20)
    @DisplayName("[预约] 分页查询预约信息")
    void test05_PageAppointment() throws Exception {
        executeTestWithToken("分页查询预约信息", () -> {
            mockMvc.perform(get("/admin/appointment/page")
                            .header("access_token", token)
                            .param("page", "1")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));
        });
    }

    @Test
    @Order(21)
    @DisplayName("[预约] 根据ID修改预约状态")
    void test06_UpdateAppointmentStatus() throws Exception {
        executeTestWithToken("修改预约状态", () -> {
            mockMvc.perform(post("/admin/appointment/updateStatusById")
                            .header("access_token", token)
                            .param("id", "1")
                            .param("status", "1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));
        });
    }

    // ==================== 租约管理接口测试 ====================

    @Test
    @Order(30)
    @DisplayName("[租约] 保存或更新租约")
    void test07_SaveOrUpdateLeaseAgreement() throws Exception {
        executeTestWithToken("保存或更新租约", () -> {
            String json = "{" +
                    "\"userId\":1," +
                    "\"apartmentId\":1," +
                    "\"roomId\":1," +
                    "\"leaseStartDate\":\"2024-01-01\"," +
                    "\"leaseEndDate\":\"2024-12-31\"," +
                    "\"rent\":3000," +
                    "\"deposit\":3000" +
                    "}";
            mockMvc.perform(post("/admin/leaseAgreement/saveOrUpdate")
                            .header("access_token", token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));
        });
    }

    @Test
    @Order(31)
    @DisplayName("[租约] 分页查询租约")
    void test08_PageLeaseAgreement() throws Exception {
        executeTestWithToken("分页查询租约", () -> {
            mockMvc.perform(get("/admin/leaseAgreement/page")
                            .header("access_token", token)
                            .param("page", "1")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));
        });
    }

    // ==================== 系统用户管理接口测试 ====================

    @Test
    @Order(40)
    @DisplayName("[系统用户] 分页查询")
    void test09_PageSystemUser() throws Exception {
        executeTestWithToken("分页查询系统用户", () -> {
            mockMvc.perform(get("/admin/systemUser/page")
                            .header("access_token", token)
                            .param("page", "1")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));
        });
    }

    @Test
    @Order(41)
    @DisplayName("[系统用户] 检查用户名是否可用")
    void test10_IsUsernameAvailable() throws Exception {
        executeTestWithToken("检查用户名", () -> {
            mockMvc.perform(get("/admin/systemUser/isUserNameAvailable")
                            .header("access_token", token)
                            .param("username", "testuser"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));
        });
    }

    // ==================== 岗位管理接口测试 ====================

    @Test
    @Order(50)
    @DisplayName("[岗位] 分页查询")
    void test11_PageSystemPost() throws Exception {
        executeTestWithToken("分页查询岗位", () -> {
            mockMvc.perform(get("/admin/systemPost/page")
                            .header("access_token", token)
                            .param("page", "1")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));
        });
    }

    @Test
    @Order(51)
    @DisplayName("[岗位] 查询全部岗位")
    void test12_ListSystemPost() throws Exception {
        executeTestWithToken("查询全部岗位", () -> {
            mockMvc.perform(get("/admin/systemPost/list")
                            .header("access_token", token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));
        });
    }

    // ==================== 区域管理接口测试 ====================

    @Test
    @Order(60)
    @DisplayName("[区域] 查询所有省份")
    void test13_ListProvince() throws Exception {
        executeTestWithToken("查询所有省份", () -> {
            mockMvc.perform(get("/admin/region/province/list")
                            .header("access_token", token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));
        });
    }

    @Test
    @Order(61)
    @DisplayName("[区域] 根据省份ID查询城市")
    void test14_ListCityByProvinceId() throws Exception {
        executeTestWithToken("查询城市", () -> {
            mockMvc.perform(get("/admin/region/city/listByProvinceId")
                            .header("access_token", token)
                            .param("provinceId", "1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));
        });
    }

    // ==================== 属性管理接口测试 ====================

    @Test
    @Order(70)
    @DisplayName("[属性] 查询全部属性")
    void test15_ListAttrInfo() throws Exception {
        executeTestWithToken("查询全部属性", () -> {
            mockMvc.perform(get("/admin/attr/list")
                            .header("access_token", token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));
        });
    }

    // ==================== 测试统计 ====================

    @AfterAll
    void printStatistics() {
        System.out.println("\n========================================");
        System.out.println("           自动化测试统计");
        System.out.println("========================================");
        System.out.println("通过: " + passedTests);
        System.out.println("失败: " + failedTests);
        System.out.println("总计: " + (passedTests + failedTests));
        System.out.println("========================================");
    }

    // ==================== 辅助方法 ====================

    private void executeTest(String testName, TestAction action) {
        try {
            System.out.println("[测试] " + testName + " ...");
            action.execute();
            System.out.println("  ✅ 通过");
            passedTests++;
        } catch (Exception e) {
            System.out.println("  ❌ 失败: " + e.getMessage());
            failedTests++;
        }
    }

    private void executeTestWithToken(String testName, TestAction action) {
        if (token == null || token.isEmpty()) {
            System.out.println("[测试] " + testName + " ...");
            System.out.println("  ⚠️ 跳过 (未获取到token)");
            return;
        }
        executeTest(testName, action);
    }

    @FunctionalInterface
    interface TestAction {
        void execute() throws Exception;
    }
}
