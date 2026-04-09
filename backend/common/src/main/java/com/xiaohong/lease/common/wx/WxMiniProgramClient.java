package com.xiaohong.lease.common.wx;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * 微信小程序服务端客户端
 */
@Slf4j
@Component
public class WxMiniProgramClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${wx.mini-program.appid:}")
    private String appid;

    @Value("${wx.mini-program.secret:}")
    private String secret;

    private static final String JSCODE2SESSION_URL =
            "https://api.weixin.qq.com/sns/jscode2session?appid={appid}&secret={secret}&js_code={jsCode}&grant_type=authorization_code";

    private static final String ACCESS_TOKEN_URL =
            "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid={appid}&secret={secret}";

    private static final String SUBSCRIBE_MESSAGE_SEND_URL =
            "https://api.weixin.qq.com/cgi-bin/message/subscribe/send?access_token={accessToken}";

    /**
     * 调用微信 auth.code2Session 接口
     *
     * @param jsCode 小程序登录时获取的 code
     * @return 微信响应结果
     */
    @SuppressWarnings("unchecked")
    public Code2SessionResponse code2Session(String jsCode) {
        Map<String, Object> result = restTemplate.getForObject(
                JSCODE2SESSION_URL,
                Map.class,
                appid, secret, jsCode
        );

        if (result == null) {
            throw new RuntimeException("微信接口调用失败，返回为空");
        }

        Code2SessionResponse response = new Code2SessionResponse();
        response.setOpenid((String) result.get("openid"));
        response.setSessionKey((String) result.get("session_key"));
        response.setUnionid((String) result.get("unionid"));
        response.setErrcode(result.get("errcode") != null ? ((Number) result.get("errcode")).intValue() : null);
        response.setErrmsg((String) result.get("errmsg"));
        return response;
    }

    /**
     * 获取微信小程序 access_token
     *
     * @return access_token，获取失败返回 null
     */
    @SuppressWarnings("unchecked")
    public String getAccessToken() {
        if (!hasConfig()) {
            log.warn("微信小程序 appid 或 secret 未配置，无法获取 access_token");
            return null;
        }
        try {
            Map<String, Object> result = restTemplate.getForObject(
                    ACCESS_TOKEN_URL,
                    Map.class,
                    appid, secret
            );
            if (result == null) {
                log.warn("微信获取 access_token 接口返回为空");
                return null;
            }
            Integer errcode = result.get("errcode") != null ? ((Number) result.get("errcode")).intValue() : null;
            if (errcode != null && errcode != 0) {
                log.warn("微信获取 access_token 失败，errcode: {}, errmsg: {}", errcode, result.get("errmsg"));
                return null;
            }
            return (String) result.get("access_token");
        } catch (Exception e) {
            log.error("获取微信小程序 access_token 异常", e);
            return null;
        }
    }

    /**
     * 发送订阅消息
     *
     * @param accessToken 接口调用凭证
     * @param openid      接收者 openid
     * @param templateId  订阅消息模板 ID
     * @param page        点击消息后跳转的页面路径（可选）
     * @param data        模板数据，key 为模板字段名，value 为 {value: "内容"}
     * @return 是否发送成功
     */
    @SuppressWarnings("unchecked")
    public boolean sendSubscribeMessage(String accessToken, String openid, String templateId,
                                         String page, Map<String, Map<String, String>> data) {
        Map<String, Object> body = new HashMap<>();
        body.put("touser", openid);
        body.put("template_id", templateId);
        if (page != null && !page.isEmpty()) {
            body.put("page", page);
        }
        body.put("data", data);
        // 跳转小程序类型：developer 为开发版；trial 为体验版；formal 为正式版
        body.put("miniprogram_state", "formal");
        // 进入客服会话
        body.put("lang", "zh_CN");

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    SUBSCRIBE_MESSAGE_SEND_URL,
                    body,
                    Map.class,
                    accessToken
            );
            Map<String, Object> result = response.getBody();
            if (result == null) {
                log.warn("发送订阅消息接口返回为空");
                return false;
            }
            Integer errcode = result.get("errcode") != null ? ((Number) result.get("errcode")).intValue() : null;
            if (errcode == null || errcode == 0) {
                log.info("发送订阅消息成功，openid: {}, templateId: {}", openid, templateId);
                return true;
            }
            log.warn("发送订阅消息失败，errcode: {}, errmsg: {}", errcode, result.get("errmsg"));
            return false;
        } catch (Exception e) {
            log.error("发送订阅消息异常，openid: {}, templateId: {}", openid, templateId, e);
            return false;
        }
    }

    /**
     * 检查是否配置了微信小程序参数
     */
    public boolean hasConfig() {
        return appid != null && !appid.isEmpty() && secret != null && !secret.isEmpty();
    }

    @Data
    public static class Code2SessionResponse {
        private String openid;
        private String sessionKey;
        private String unionid;
        private Integer errcode;
        private String errmsg;

        public boolean isSuccess() {
            return errcode == null || errcode == 0;
        }
    }
}
