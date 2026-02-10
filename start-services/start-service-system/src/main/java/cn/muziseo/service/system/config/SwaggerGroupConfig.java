package cn.muziseo.service.system.config;

import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerGroupConfig {
    @Bean
    public GroupedOpenApi authApi() {
        return GroupedOpenApi.builder()
                .group("权限管理")
                .pathsToMatch("/auth/**")
                .addOpenApiCustomizer(openApi -> {
                    openApi.info(new Info()
                            .title("订单管理API")
                            .version("1.0")
                            .description("订单相关接口"));
                })
                .build();
    }

    @Bean
    public GroupedOpenApi demoApi() {
        return GroupedOpenApi.builder()
                .group("演示分组")
                .pathsToMatch("/demo/**")
                .addOpenApiCustomizer(openApi -> {
                    openApi.info(new Info()
                            .title("演示分组API")
                            .version("1.0")
                            .description("演示分组相关接口"));
                })
                .build();
    }
}
