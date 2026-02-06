package cn.muziseo.common.cache.core.config;

import cn.hutool.core.util.StrUtil;
import cn.muziseo.common.cache.core.config.properties.StartCacheProperties;
import cn.muziseo.common.cache.core.manager.TimeoutRedisCacheManager;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.BatchStrategies;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.util.StringUtils;

import static cn.muziseo.common.cache.core.config.StarRedisAutoConfiguration.buildRedisSerializer;

/**
 * 缓存自动配置类
 *
 * @author 木子软件
 * @Date 2026-01-26
 * @Copyright <a href="https://code.muziseo.cn">木子软件</a>
 */
@AutoConfiguration
@EnableCaching
@EnableConfigurationProperties({CacheProperties.class, StartCacheProperties.class})
public class StartCacheAutoConfiguration {

    /**
     * 创建 RedisCacheConfiguration Bean
     *
     * @param cacheProperties 缓存属性配置
     * @return RedisCacheConfiguration
     */
    @Bean
    @Primary
    public RedisCacheConfiguration redisCacheConfiguration(CacheProperties cacheProperties) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();

        config = config.computePrefixWith(cacheName -> {
            String keyPrefix = cacheProperties.getRedis().getKeyPrefix();
            String prefix = StringUtils.hasText(keyPrefix) ? keyPrefix : "";
            if (StringUtils.hasText(prefix) && !prefix.endsWith(StrUtil.COLON)) {
                prefix += StrUtil.COLON;
            }
            return prefix + cacheName + StrUtil.COLON;
        });

        config = config.serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(buildRedisSerializer()));

        CacheProperties.Redis redisProperties = cacheProperties.getRedis();
        if (redisProperties.getTimeToLive() != null) {
            config = config.entryTtl(redisProperties.getTimeToLive());
        }
        if (!redisProperties.isCacheNullValues()) {
            config = config.disableCachingNullValues();
        }
        if (!redisProperties.isUseKeyPrefix()) {
            config = config.disableKeyPrefix();
        }

        return config;
    }

    /**
     * 创建 RedisCacheManager Bean
     *
     * @param connectionFactory       Redis 连接工厂
     * @param redisCacheConfiguration Redis 缓存配置
     * @param startCacheProperties    缓存属性配置
     * @return RedisCacheManager
     */
    @Bean
    public RedisCacheManager redisCacheManager(RedisConnectionFactory connectionFactory,
                                               RedisCacheConfiguration redisCacheConfiguration,
                                               StartCacheProperties startCacheProperties) {
        RedisCacheWriter cacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(
                connectionFactory,
                BatchStrategies.scan(startCacheProperties.getRedisScanBatchSize())
        );

        return new TimeoutRedisCacheManager(cacheWriter, redisCacheConfiguration);
    }
}
