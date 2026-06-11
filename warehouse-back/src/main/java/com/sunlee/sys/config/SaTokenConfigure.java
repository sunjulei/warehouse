package com.sunlee.sys.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import com.sunlee.sys.common.Constast;
import com.sunlee.sys.common.PermissionDeniedException;
import com.sunlee.sys.entity.User;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SA-Token 配置 — 注册拦截器（含登录校验 + 接口级权限校验）
 * @Author: sunlee
 * @Date: 2026/01/15 21:01
 */
@Configuration
public class SaTokenConfigure implements WebMvcConfigurer {

    /**
     * URL 前缀 → 所需权限码 映射表
     */
    private static final Map<String, String> URL_PERM_MAP = new HashMap<>();

    static {
        // sys 模块
        URL_PERM_MAP.put("/user/", "user:*");
        URL_PERM_MAP.put("/role/", "role:*");
        URL_PERM_MAP.put("/menu/", "menu:*");
        URL_PERM_MAP.put("/dept/", "dept:*");
        URL_PERM_MAP.put("/permission/", "permission:*");
        URL_PERM_MAP.put("/notice/", "notice:*");
        URL_PERM_MAP.put("/loginfo/", "loginfo:*");
        // bus 模块
        URL_PERM_MAP.put("/goods/", "goods:*");
        URL_PERM_MAP.put("/customer/", "customer:*");
        URL_PERM_MAP.put("/provider/", "provider:*");
        URL_PERM_MAP.put("/inport/", "inport:*");
        URL_PERM_MAP.put("/outport/", "outport:*");
        URL_PERM_MAP.put("/sales/", "sales:*");
        URL_PERM_MAP.put("/retail/", "retail:*");
        URL_PERM_MAP.put("/salesback/", "salesback:*");
        URL_PERM_MAP.put("/retailback/", "retailback:*");
        URL_PERM_MAP.put("/report/", "report:*");
        URL_PERM_MAP.put("/stocktake/", "stocktake:*");
        URL_PERM_MAP.put("/category/", "category:*");
        URL_PERM_MAP.put("/commission/", "commission:*");
        URL_PERM_MAP.put("/member/", "member:*");
        URL_PERM_MAP.put("/performance/", "performance:*");
        // 文件上传单独控制
        URL_PERM_MAP.put("/file/uploadFile", "file:*");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(handle -> {
            SaRouter.match("/**")
                    .notMatch(
                            "/login/login",
                            "/login/getCode",
                            "/login/getCaptchaBase64",
                            "/login/logout",
                            "/index.html*",
                            "/sys/toLogin*",
                            "/resources/**",
                            "/file/showImageByPath*",
                            "/druid/**",
                            "/swagger-ui/**",
                            "/v3/api-docs/**"
                    )
                    .check(r -> {
                        // 1. 登录校验
                        StpUtil.checkLogin();
                        // 2. 接口级权限校验
                        checkPermission();
                    });
        })).addPathPatterns("/**");
    }

    /**
     * 接口级权限校验
     */
    private void checkPermission() {
        // 获取请求路径（不含 context-path）
        String path = cn.dev33.satoken.context.SaHolder.getRequest().getRequestPath();
        if (path == null || path.isEmpty()) {
            return;
        }

        // 获取当前用户
        User user = (User) StpUtil.getSession().get("user");
        if (user == null) {
            throw new PermissionDeniedException("未获取到用户信息");
        }

        // 超级管理员直接放行
        if (user.getType().equals(Constast.USER_TYPE_SUPER)) {
            return;
        }

        // 匹配 URL 前缀获取所需权限码
        String requiredPerm = matchRequiredPermission(path);
        if (requiredPerm == null) {
            // 该 URL 不在权限控制范围内，放行
            return;
        }

        // 获取用户权限列表
        List<String> permissions = (List<String>) StpUtil.getSession().get("permissions");
        if (permissions == null || permissions.isEmpty()) {
            throw new PermissionDeniedException("用户无任何权限，访问路径: " + path);
        }

        // 校验权限：拥有 *:* 或对应模块权限即可
        if (!hasPermission(permissions, requiredPerm)) {
            throw new PermissionDeniedException(
                    "缺少权限 [" + requiredPerm + "]，访问路径: " + path
            );
        }
    }

    /**
     * 根据请求路径匹配所需权限码
     * @param path 请求路径
     * @return 所需权限码，null 表示该路径不需要权限控制
     */
    private String matchRequiredPermission(String path) {
        // 精确匹配（如 /file/uploadFile）
        if (URL_PERM_MAP.containsKey(path)) {
            return URL_PERM_MAP.get(path);
        }

        // 前缀匹配（如 /user/loadAllUser → user:*）
        for (Map.Entry<String, String> entry : URL_PERM_MAP.entrySet()) {
            String urlPrefix = entry.getKey();
            if (path.startsWith(urlPrefix)) {
                return entry.getValue();
            }
        }

        return null;
    }

    /**
     * 判断用户权限列表是否包含所需权限
     * @param permissions 用户权限列表
     * @param required 所需权限码
     * @return true 有权限
     */
    private boolean hasPermission(List<String> permissions, String required) {
        // 拥有通配权限
        if (permissions.contains("*:*")) {
            return true;
        }
        // 精确匹配
        if (permissions.contains(required)) {
            return true;
        }
        // 模块级通配匹配（如 user:* 匹配 user:add、user:view 等）
        if (required.endsWith(":*")) {
            String module = required.substring(0, required.length() - 2);
            for (String perm : permissions) {
                if (perm.equals(module + ":*") || perm.startsWith(module + ":")) {
                    return true;
                }
            }
        }
        return false;
    }
}
