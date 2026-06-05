package com.sunlee.sys.annotation;

import java.lang.annotation.*;

/**
 * 操作日志注解 — 标记在 Controller 方法上，自动记录操作日志
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationLog {

    /** 操作类型：添加、修改、删除、上架、下架 等 */
    String type();

    /** 模块名称：商品管理、客户管理 等 */
    String module();

    /** 操作描述，支持 SpEL 表达式（以 # 开头引用方法参数） */
    String description();
}
