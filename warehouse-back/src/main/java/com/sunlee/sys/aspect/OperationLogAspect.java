package com.sunlee.sys.aspect;

import com.sunlee.bus.service.IOperationLogService;
import com.sunlee.sys.annotation.OperationLog;
import com.sunlee.sys.common.WebUtils;
import com.sunlee.sys.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.StandardReflectionParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Date;

/**
 * 操作日志 AOP 切面
 */
@Slf4j
@Aspect
@Component
public class OperationLogAspect {

    @Autowired
    private IOperationLogService operationLogService;

    private final ExpressionParser parser = new SpelExpressionParser();
    private final StandardReflectionParameterNameDiscoverer discoverer = new StandardReflectionParameterNameDiscoverer();

    @AfterReturning(pointcut = "@annotation(com.sunlee.sys.annotation.OperationLog)", returning = "result")
    public void doAfterReturning(JoinPoint joinPoint, Object result) {
        try {
            handleLog(joinPoint);
        } catch (Exception e) {
            log.error("记录操作日志失败: {}", e.getMessage(), e);
        }
    }

    private void handleLog(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        OperationLog annotation = method.getAnnotation(OperationLog.class);
        if (annotation == null) return;

        String description = annotation.description();
        // 如果描述包含 #，尝试解析 SpEL 表达式
        if (description.contains("#")) {
            description = parseSpel(description, joinPoint, method);
        }

        com.sunlee.bus.entity.OperationLog entity = new com.sunlee.bus.entity.OperationLog();
        entity.setType(annotation.type());
        entity.setModule(annotation.module());
        entity.setDescription(description);

        try {
            User user = (User) WebUtils.getSession().getAttribute("user");
            entity.setOperateperson(user != null ? user.getName() : "未知用户");
        } catch (Exception e) {
            entity.setOperateperson("未知用户");
        }

        entity.setOperatetime(new Date());
        operationLogService.save(entity);
    }

    private String parseSpel(String spel, JoinPoint joinPoint, Method method) {
        try {
            Object[] args = joinPoint.getArgs();
            String[] paramNames = discoverer.getParameterNames(method);
            if (paramNames == null) return spel;

            EvaluationContext context = new StandardEvaluationContext();
            // 设置方法参数
            for (int i = 0; i < paramNames.length; i++) {
                ((StandardEvaluationContext) context).setVariable(paramNames[i], args[i]);
            }
            // 设置 args 数组
            ((StandardEvaluationContext) context).setVariable("args", args);

            // 解析整个表达式（包含字符串拼接）
            Object value = parser.parseExpression(spel).getValue(context);
            return value != null ? value.toString() : spel;
        } catch (Exception e) {
            log.warn("SpEL 解析失败: {}", spel, e);
            return spel;
        }
    }
}
