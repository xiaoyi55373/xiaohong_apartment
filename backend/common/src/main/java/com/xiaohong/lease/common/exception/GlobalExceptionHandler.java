package com.xiaohong.lease.common.exception;

import com.xiaohong.lease.common.result.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(LeaseException.class)
    public Result handleException(LeaseException e) {
        e.printStackTrace();
        return Result.fail(e.getCode(),e.getMessage());
    }


    @ResponseBody
    @ExceptionHandler(RuntimeException.class)
    public Result handleException(RuntimeException e) {
        e.printStackTrace();
        return Result.fail();
    }
}
