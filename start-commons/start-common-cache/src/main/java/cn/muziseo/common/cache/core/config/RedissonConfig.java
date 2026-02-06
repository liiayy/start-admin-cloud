package cn.muziseo.common.cache.core.config;

import org.redisson.codec.JsonJacksonCodec;
import org.redisson.spring.starter.RedissonAutoConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static cn.muziseo.common.cache.core.config.StarRedisAutoConfiguration.createRedisObjectMapper;

/**
 * Redisson 配置类
 *
 * @author 木子软件
 * @Date 2026-02-06
 * @Copyright <a href="https://code.muziseo.cn">木子软件</a>
 */
@Configuration
public class RedissonConfig {

    /**
     * 自定义 Redisson 配置
     *
     * @return RedissonAutoConfigurationCustomizer
     */
    @Bean
    public RedissonAutoConfigurationCustomizer redissonCustomizer() {
        return config -> {
            config.setCodec(new JsonJacksonCodec(createRedisObjectMapper()));
        };
    }
}
