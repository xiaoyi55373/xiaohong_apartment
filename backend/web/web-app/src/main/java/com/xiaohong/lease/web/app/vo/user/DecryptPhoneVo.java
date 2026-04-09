package com.xiaohong.lease.web.app.vo.user;

import lombok.Data;

@Data
public class DecryptPhoneVo {
    private String encryptedData;
    private String iv;
}
