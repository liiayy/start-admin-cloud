package cn.muziseo.common.cache.core.config;

import cn.muziseo.common.core.factory.YmlPropertySourceFactory;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.redisson.spring.starter.RedissonAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * Redis 自动配置类
 *
 * @author 木子软件
 * @Date 2026-01-26
 * @Copyright <a href="https://code.muziseo.cn">木子软件</a>
 */
@AutoConfiguration(before = RedissonAutoConfiguration.class)
@PropertySource(value = "classpath:common-redis.yml", factory = YmlPropertySourceFactory.class)
public class StarRedisAutoConfiguration {

    /**
     * 构建 Redis 序列化器
     *
     * @return RedisSerializer<Object>
     */
    public static RedisSerializer<Object> buildRedisSerializer() {
        return new GenericJackson2JsonRedisSerializer(createRedisObjectMapper());
    }

    /**
     * 创建 Redis ObjectMapper
     *
     * @return ObjectMapper
     */
    public static ObjectMapper createRedisObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.activateDefaultTyping(
                objectMapper.getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY);
        return objectMapper;
    }

    /**
     * 创建 RedisTemplate Bean，使用 JSON 序列化方式
     *
     * @param factory Redis 连接工厂
     * @return RedisTemplate<String, Object>
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(RedisSerializer.string());
        template.setHashKeySerializer(RedisSerializer.string());
        template.setValueSerializer(buildRedisSerializer());
        template.setHashValueSerializer(buildRedisSerializer());
        return template;
    }

    /**
     * 创建 HashOperations Bean
     *
     * @param redisTemplate RedisTemplate
     * @return HashOperations<String, String, Object>
     */
    @Bean
    public HashOperations<String, String, Object> hashOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForHash();
    }

    /**
     * 创建 ValueOperations Bean
     *
     * @param redisTemplate RedisTemplate
     * @return ValueOperations<String, String>
     */
    @Bean
    public ValueOperations<String, String> valueOperations(RedisTemplate<String, String> redisTemplate) {
        return redisTemplate.opsForValue();
    }

    /**
     * 创建 ListOperations Bean
     *
     * @param redisTemplate RedisTemplate
     * @return ListOperations<String, Object>
     */
    @Bean
    public ListOperations<String, Object> listOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForList();
    }

    /**
     * 创建 SetOperations Bean
     *
     * @param redisTemplate RedisTemplate
     * @return SetOperations<String, Object>
     */
    @Bean
    public SetOperations<String, Object> setOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForSet();
    }

    /**
     * 创建 ZSetOperations Bean
     *
     * @param redisTemplate RedisTemplate
     * @return ZSetOperations<String, Object>
     */
    @Bean
    public ZSetOperations<String, Object> zSetOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForZSet();
    }
}
