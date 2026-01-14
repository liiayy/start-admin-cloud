package cn.muziseo.gateway.config.ratelimit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.ReactiveRedisTemplate;

/**
 * 限流配置
 *
 * @Author Administrator
 * @Date 2026-01-06
 * @Copyright <a href="https://github.com/muziseo">木子软件</a>
 */
@Configuration
public class RateLimitConfig {

    @Bean
    public RedisRateLimiter customRedisRateLimiter(ReactiveRedisTemplate<String, String> reactiveRedisTemplate) {
        return new RedisRateLimiter(reactiveRedisTemplate);
    }
}