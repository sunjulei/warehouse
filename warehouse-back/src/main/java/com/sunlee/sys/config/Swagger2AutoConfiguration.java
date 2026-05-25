package com.sunlee.sys.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: sunlee
 * @Date: 2026/05/01 17:30
 */
@Configuration
public class Swagger2AutoConfiguration {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("仓库后台管理系统接口文档")
                        .description("仓库后台管理系统SwaggerUI接口工具")
                        .version("1.0")
                        .contact(new Contact()
                                .name("sunlee")
                                .email("123@163.com")));
    }

}
