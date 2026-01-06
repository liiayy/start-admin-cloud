package cn.muziseo.gateway.config.ratelimit;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis配置
 *
 * @Author Administrator
 * @Date 2026-01-06
 * @Copyright <a href="https://github.com/muziseo">木子软件</a>
 */
@Configuration
public class RedisConfig {

    private final RateLimitProperties rateLimitProperties;

    public RedisConfig(RateLimitProperties rateLimitProperties) {
        this.rateLimitProperties = rateLimitProperties;
    }

    /**
     * 创建ReactiveRedisConnectionFactory
     *
     * @return ReactiveRedisConnectionFactory
     */
    @Bean
    @Primary
    public ReactiveRedisConnectionFactory reactiveRedisConnectionFactory() {
        RateLimitProperties.RedisConfig redisConfig = rateLimitProperties.getRedis();
        LettuceConnectionFactory factory = new LettuceConnectionFactory(
                redisConfig.getHost(),
                redisConfig.getPort()
        );
        factory.setPassword(redisConfig.getPassword());
        factory.setDatabase(redisConfig.getDatabase());
        factory.setTimeout(redisConfig.getTimeout());
        return factory;
    }

    /**
     * 创建ReactiveRedisTemplate
     *
     * @param factory ReactiveRedisConnectionFactory
     * @return ReactiveRedisTemplate
     */
    @Bean
    public ReactiveRedisTemplate<String, String> reactiveRedisTemplate(@Qualifier("reactiveRedisConnectionFactory") ReactiveRedisConnectionFactory factory) {
        RedisSerializer<String> keySerializer = new StringRedisSerializer();
        RedisSerializer<String> valueSerializer = new StringRedisSerializer();

        RedisSerializationContext<String, String> context = RedisSerializationContext.<String, String>newSerializationContext()
                .key(keySerializer)
                .value(valueSerializer)
                .hashKey(keySerializer)
                .hashValue(valueSerializer)
                .build();

        return new ReactiveRedisTemplate<>(factory, context);
    }
}