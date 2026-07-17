package com.sunlee.sys.common;

/**
 * 权限不足异常
 * @Author: sunlee
 * @Date: 2026/06/11
 */
public class PermissionDeniedException extends RuntimeException {

    public PermissionDeniedException(String message) {
        super(message);
    }

    public PermissionDeniedException(String message, Throwable cause) {
        super(message, cause);
    }
}
