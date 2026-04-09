package com.xiaohong.lease.web.app.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "小程序登录信息")
public class MiniProgramLoginVo {

    @Schema(description = "微信登录临时凭证code")
    private String code;
}
