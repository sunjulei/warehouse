package com.sunlee.sys.config;

import com.sunlee.sys.common.PermissionDeniedException;
import com.sunlee.sys.common.ResultObj;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * @Author: sunlee
 * @Date: 2026/06/11
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理权限不足异常
     */
    @ExceptionHandler(PermissionDeniedException.class)
    public ResultObj handlePermissionDenied(PermissionDeniedException e) {
        log.warn("权限校验失败: {}", e.getMessage());
        return ResultObj.error("无权限访问此接口");
    }
}
