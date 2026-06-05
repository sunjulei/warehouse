package com.sunlee.sys.config;

import com.sunlee.sys.common.ResultObj;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResultObj handleRuntimeException(RuntimeException e) {
        log.error("运行时异常: {}", e.getMessage(), e);
        return new ResultObj(-1, "操作失败: " + e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResultObj handleException(Exception e) {
        log.error("系统异常: {}", e.getMessage(), e);
        return new ResultObj(-1, "系统错误，请联系管理员");
    }
}
