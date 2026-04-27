package cn.muziseo.gateway.config;
 
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.properties.AbstractSwaggerUiConfigProperties;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.context.annotation.Configuration;
 
import java.util.HashSet;
import java.util.List;
import java.util.Set;
 
/**
 * 网关动态聚合 Swagger 文档配置
 * 自动扫描网关路由，发现微服务的 OpenAPI 定义
 *
 * @author 木子软件
 */
@Slf4j
@Configuration
public class DynamicSwaggerConfig implements ApplicationListener<RefreshRoutesEvent> {
 
    private final RouteDefinitionLocator routeDefinitionLocator;
    private final SwaggerUiConfigProperties swaggerUiConfigProperties;
 
    public DynamicSwaggerConfig(RouteDefinitionLocator routeDefinitionLocator, SwaggerUiConfigProperties swaggerUiConfigProperties) {
        this.routeDefinitionLocator = routeDefinitionLocator;
        this.swaggerUiConfigProperties = swaggerUiConfigProperties;
    }
 
    @Override
    public void onApplicationEvent(RefreshRoutesEvent event) {
        refresh();
    }
 
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        refresh();
    }
 
    /**
     * 刷新 Swagger 资源列表
     */
    public void refresh() {
        routeDefinitionLocator.getRouteDefinitions().collectList().subscribe(routes -> {
            if (routes == null || routes.isEmpty()) {
                return;
            }
     
            Set<AbstractSwaggerUiConfigProperties.SwaggerUrl> urls = new HashSet<>();
            urls.add(new AbstractSwaggerUiConfigProperties.SwaggerUrl("gateway", "/v3/api-docs", "gateway"));
            
            routes.forEach(route -> {
                // 约定：id 以 -api 结尾的路由通常是我们想要聚合文档的接口路由
                String routeId = route.getId();
                if (routeId != null && routeId.endsWith("-api")) {
                    route.getPredicates().stream()
                            .filter(predicate -> "Path".equalsIgnoreCase(predicate.getName()))
                            .forEach(predicate -> {
                                // Gateway yaml 简写配置中，参数名可能为 pattern 也可能为 _genkey_0
                                String pattern = predicate.getArgs().get("pattern");
                                if (pattern == null) {
                                    pattern = predicate.getArgs().get("_genkey_0");
                                }
                                if (pattern != null) {
                                    // 将 /** 替换为 /v3/api-docs，形成文档访问路径
                                    String docPath = pattern.replace("/**", "/v3/api-docs");
                                    // 生成友好的服务名称 (如 system-admin-api -> system-admin)
                                    String name = routeId.replace("-api", "");
                                    urls.add(new AbstractSwaggerUiConfigProperties.SwaggerUrl(name, docPath, name));
                                    log.info("[Swagger聚合] 发现服务文档: {} -> {}", name, docPath);
                                }
                            });
                }
            });
            if (!urls.isEmpty()) {
                swaggerUiConfigProperties.setUrls(urls);
                log.info("[Swagger聚合] 已成功更新文档列表: {} 个服务", urls.size());
            }
        }, error -> log.error("[Swagger聚合] 获取路由列表失败", error));
    }
}
