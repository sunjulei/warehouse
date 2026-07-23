package com.sunlee.sys.config;

import cn.dev33.satoken.exception.NotLoginException;
import com.sunlee.sys.common.PermissionDeniedException;
import com.sunlee.sys.common.ResultObj;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    /**
     * 处理未登录/token 失效异常，返回 HTTP 401 + JSON（前端拦截器据此跳转登录页）
     */
    @ExceptionHandler(NotLoginException.class)
    public ResponseEntity<ResultObj> handleNotLogin(NotLoginException e) {
        log.warn("未登录访问: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ResultObj.error("登录已过期，请重新登录"));
    }
}
