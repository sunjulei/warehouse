package com.sunlee.sys.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * SA-Token 配置 — 注册拦截器
 * @Author: sunlee
 * @Date: 2026/01/15 21:01
 */
@Configuration
public class SaTokenConfigure implements WebMvcConfigurer {

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
                    .check(r -> StpUtil.checkLogin());
        })).addPathPatterns("/**");
    }
}
