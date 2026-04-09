package com.xiaohong.lease.web.app.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "APP端登录信息")
public class LoginVo {

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "验证码")
    private String code;

    // 显式添加 getter 方法
    public String getPhone() { return phone; }
    public String getCode() { return code; }
}
