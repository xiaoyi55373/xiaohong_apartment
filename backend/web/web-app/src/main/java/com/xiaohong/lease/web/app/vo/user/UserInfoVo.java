package com.xiaohong.lease.web.app.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "APP端用户信息")
public class UserInfoVo {

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "头像")
    private String avatarUrl;

    public UserInfoVo() {}

    public UserInfoVo(String nickname, String avatarUrl) {
        this.nickname = nickname;
        this.avatarUrl = avatarUrl;
    }
}
