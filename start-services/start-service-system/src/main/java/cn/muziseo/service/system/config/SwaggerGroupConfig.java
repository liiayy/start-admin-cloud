package cn.muziseo.service.system.config;

import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerGroupConfig {
    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
                .group("权限管理")
                .pathsToMatch("/system/auth/**")
                .addOpenApiCustomizer(openApi -> {
                    openApi.info(new Info()
                            .title("订单管理API")
                            .version("1.0")
                            .description("订单相关接口"));
                })
                .build();
    }
}
