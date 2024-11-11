package com.yixi.yipan.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;
import io.swagger.v3.oas.models.tags.Tag;


@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title("忆盘网盘系统 API")
                        .version("v1.0.0")
                        .description("忆盘网盘系统后台接口文档")
                        .contact(new Contact()
                                .name("yixi")
                                .email("partialy@163.com")))
                .tags(List.of(
                new Tag().name("用户管理").description("用户的增删改查接口"),
                new Tag().name("文件管理").description("文件相关接口"),
                new Tag().name("文件分享").description("文件分享相关接口"),
                new Tag().name("系统管理").description("系统配置相关接口")
                )
        );
    }
}
