package cn.muziseo.common.web.core.config;
 
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
 
/**
 * Swagger/OpenAPI 统一配置
 *
 * @author 木子软件
 */
@Configuration
public class SwaggerConfig {
 
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("StartAdmin 微服务接口文档")
                        .version("1.0.0")
                        .description("基于 Spring Boot 3 & SpringDoc 的现代化接口定义"))
                // 1. 统一添加 Authorization 鉴权头
                .addSecurityItem(new SecurityRequirement().addList("Authorization"))
                .components(new Components()
                        .addSecuritySchemes("Authorization", new SecurityScheme()
                                .name("Authorization")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
