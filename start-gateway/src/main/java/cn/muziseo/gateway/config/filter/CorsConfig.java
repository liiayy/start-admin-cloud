package cn.muziseo.gateway.config.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.util.pattern.PathPatternParser;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * CORS 跨域配置
 * 使用 Spring 内置的 CorsWebFilter
 *
 * @Author Administrator
 * @Date 2026-01-06
 * @Copyright <a href="https://github.com/muziseo">木子软件</a>
 */
@Configuration
@Slf4j
public class CorsConfig {

    // region 常量定义

    /**
     * 通配符
     */
    private static final String WILDCARD = "*";

    /**
     * 默认允许的 HTTP 方法
     */
    private static final List<String> DEFAULT_ALLOWED_METHODS = Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD"
    );

    /**
     * 默认允许的请求头
     */
    private static final List<String> DEFAULT_ALLOWED_HEADERS = Arrays.asList(
            "Origin", "Content-Type", "Accept", "Authorization", "X-Requested-With"
    );

    /**
     * 默认暴露的响应头
     */
    private static final List<String> DEFAULT_EXPOSED_HEADERS = Arrays.asList(
            "Authorization", "Content-Disposition", "X-Request-ID", "X-Total-Count"
    );

    /**
     * 默认预检请求缓存时间（秒）
     */
    private static final Long DEFAULT_MAX_AGE = 3600L;

    // endregion

    // region 配置属性

    /**
     * 允许的源，支持通配符模式
     * 多个用逗号分隔
     */
    @Value("${cors.allowed-origins:*}")
    private String allowedOriginsConfig;

    /**
     * 允许的 HTTP 方法
     * 多个用逗号分隔
     */
    @Value("${cors.allowed-methods:}")
    private String allowedMethodsConfig;

    /**
     * 允许的请求头
     * 多个用逗号分隔
     */
    @Value("${cors.allowed-headers:}")
    private String allowedHeadersConfig;

    /**
     * 暴露给前端的响应头
     * 多个用逗号分隔
     */
    @Value("${cors.exposed-headers:}")
    private String exposedHeadersConfig;

    /**
     * 是否允许携带凭证
     */
    @Value("${cors.allow-credentials:true}")
    private boolean allowCredentials;

    /**
     * 预检请求缓存时间（秒）
     */
    @Value("${cors.max-age:3600}")
    private Long maxAge;

    // endregion

    /**
     * 创建 CORS 过滤器
     *
     * @return CorsWebFilter
     */
    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 1. 配置允许的源
        configureAllowedOrigins(configuration);

        // 2. 配置允许的方法
        configureAllowedMethods(configuration);

        // 3. 配置允许的请求头
        configureAllowedHeaders(configuration);

        // 4. 配置暴露的响应头
        configureExposedHeaders(configuration);

        // 5. 配置是否允许凭证
        configuration.setAllowCredentials(allowCredentials);

        // 6. 配置预检请求缓存时间
        configuration.setMaxAge(maxAge != null ? maxAge : DEFAULT_MAX_AGE);

        // 7. 创建配置源
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(new PathPatternParser());
        source.registerCorsConfiguration("/**", configuration);


        return new CorsWebFilter(source);
    }

    /**
     * 配置允许的源
     *
     * @param configuration CORS 配置
     */
    private void configureAllowedOrigins(CorsConfiguration configuration) {
        if (!StringUtils.hasText(allowedOriginsConfig) || WILDCARD.equals(allowedOriginsConfig.trim())) {
            configuration.addAllowedOriginPattern(WILDCARD);
            log.info("CORS 允许所有源");
        } else {
            List<String> origins = parseCommaSeparatedConfig(allowedOriginsConfig);
            origins.forEach(origin -> {
                String trimmedOrigin = origin.trim();
                if (trimmedOrigin.contains(WILDCARD)) {
                    configuration.addAllowedOriginPattern(trimmedOrigin);
                } else {
                    configuration.addAllowedOrigin(trimmedOrigin);
                }
            });
            log.info("CORS 允许的源: {}", origins);
        }
    }

    /**
     * 配置允许的方法
     *
     * @param configuration CORS 配置
     */
    private void configureAllowedMethods(CorsConfiguration configuration) {
        if (!StringUtils.hasText(allowedMethodsConfig) || WILDCARD.equals(allowedMethodsConfig.trim())) {
            DEFAULT_ALLOWED_METHODS.forEach(configuration::addAllowedMethod);
            log.info("CORS 允许所有 HTTP 方法");
        } else {
            List<String> methods = parseCommaSeparatedConfig(allowedMethodsConfig);
            methods.forEach(method -> configuration.addAllowedMethod(method.trim().toUpperCase()));
            log.info("CORS 允许的 HTTP 方法: {}", methods);
        }
    }

    /**
     * 配置允许的请求头
     *
     * @param configuration CORS 配置
     */
    private void configureAllowedHeaders(CorsConfiguration configuration) {
        if (!StringUtils.hasText(allowedHeadersConfig) || WILDCARD.equals(allowedHeadersConfig.trim())) {
            configuration.addAllowedHeader(WILDCARD);
            log.info("CORS 允许所有请求头");
        } else {
            List<String> headers = parseCommaSeparatedConfig(allowedHeadersConfig);
            headers.forEach(header -> configuration.addAllowedHeader(header.trim()));
            log.info("CORS 允许的请求头: {}", headers);
        }
    }

    /**
     * 配置暴露的响应头
     *
     * @param configuration CORS 配置
     */
    private void configureExposedHeaders(CorsConfiguration configuration) {
        if (!StringUtils.hasText(exposedHeadersConfig)) {
            DEFAULT_EXPOSED_HEADERS.forEach(configuration::addExposedHeader);
            log.info("CORS 使用默认暴露的响应头: {}", DEFAULT_EXPOSED_HEADERS);
        } else {
            List<String> headers = parseCommaSeparatedConfig(exposedHeadersConfig);
            headers.forEach(header -> configuration.addExposedHeader(header.trim()));
            log.info("CORS 暴露的响应头: {}", headers);
        }
    }

    /**
     * 解析逗号分隔的配置
     *
     * @param config 配置字符串
     * @return 配置列表
     */
    private List<String> parseCommaSeparatedConfig(String config) {
        if (!StringUtils.hasText(config)) {
            return List.of();
        }

        return Arrays.stream(config.split(","))
                .filter(StringUtils::hasText)
                .map(String::trim)
                .collect(Collectors.toList());
    }

}