package com.xiaohong.lease.common.exception;


import com.xiaohong.lease.common.result.ResultCodeEnum;
import lombok.Data;

@Data
public class LeaseException extends RuntimeException{

    private Integer code;

    public LeaseException(ResultCodeEnum codeEnum) {
        super(codeEnum.getMessage());
        code=codeEnum.getCode();
    }

    public LeaseException(String message, Integer code) {
        super(message);
        this.code = code;
    }

}
