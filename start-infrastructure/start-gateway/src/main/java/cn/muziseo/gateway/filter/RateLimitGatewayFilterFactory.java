package cn.muziseo.gateway.filter;

import cn.muziseo.gateway.config.ratelimit.RateLimitProperties;
import cn.muziseo.gateway.config.ratelimit.RedisRateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 限流过滤器
 *
 * @Author Administrator
 * @Date 2026-01-06
 * @Copyright <a href="https://github.com/muziseo">木子软件</a>
 */
@Slf4j
@Component
public class RateLimitGatewayFilterFactory extends AbstractGatewayFilterFactory<RateLimitGatewayFilterFactory.Config> {

    private final RedisRateLimiter redisRateLimiter;
    private final RateLimitProperties rateLimitProperties;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public RateLimitGatewayFilterFactory(@Qualifier("customRedisRateLimiter") RedisRateLimiter redisRateLimiter, RateLimitProperties rateLimitProperties) {
        super(Config.class);
        this.redisRateLimiter = redisRateLimiter;
        this.rateLimitProperties = rateLimitProperties;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            // 检查限流是否启用
            if (!rateLimitProperties.isEnabled()) {
                return chain.filter(exchange);
            }

            // 获取请求路径
            String path = exchange.getRequest().getPath().value();
            exchange.getRequest().getMethod();
            String method = exchange.getRequest().getMethod().name();

            // 查找匹配的限流规则
            Optional<RateLimitProperties.RateLimitRule> matchedRule = findMatchedRule(path);

            if (matchedRule.isPresent()) {
                RateLimitProperties.RateLimitRule rule = matchedRule.get();

                // 检查规则是否启用
                if (!rule.isEnabled()) {
                    return chain.filter(exchange);
                }

                // 生成限流键（可以根据需要添加更多维度，如IP、用户等）
                String rateLimitKey = generateRateLimitKey(path, method);

                // 检查是否允许请求通过
                return redisRateLimiter.isAllowed(rateLimitKey, rule.getLimit(), rule.getWindow())
                        .flatMap(allowed -> {
                            if (allowed) {
                                // 允许通过，继续处理请求
                                return chain.filter(exchange);
                            } else {
                                // 限流，返回429状态码
                                exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                                return exchange.getResponse().setComplete();
                            }
                        });
            }

            // 没有匹配的规则，继续处理请求
            return chain.filter(exchange);
        };
    }

    /**
     * 查找匹配的限流规则
     *
     * @param path 请求路径
     * @return 匹配的规则
     */
    private Optional<RateLimitProperties.RateLimitRule> findMatchedRule(String path) {
        Map<String, RateLimitProperties.RateLimitRule> rules = rateLimitProperties.getRules();
        if (rules == null || rules.isEmpty()) {
            return Optional.empty();
        }

        return rules.values().stream()
                .filter(rule -> pathMatcher.match(rule.getPattern(), path))
                .findFirst();
    }

    /**
     * 生成限流键
     *
     * @param path   请求路径
     * @param method 请求方法
     * @return 限流键
     */
    private String generateRateLimitKey(String path, String method) {
        // 可以根据需要添加更多维度，如IP、用户等
        return "rate_limit:" + method + ":" + path;
    }

    /**
     * 配置类
     */
    public static class Config {
        // 可以添加过滤器特定的配置
    }
}