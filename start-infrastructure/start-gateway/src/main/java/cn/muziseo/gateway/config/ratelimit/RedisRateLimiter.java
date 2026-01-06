package cn.muziseo.gateway.config.ratelimit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Collections;

/**
 * 基于Redis的令牌桶限流实现
 *
 * @Author Administrator
 * @Date 2026-01-06
 * @Copyright <a href="https://github.com/muziseo">木子软件</a>
 */
@Slf4j
public class RedisRateLimiter {

    private final ReactiveRedisTemplate<String, String> redisTemplate;
    private final RedisScript<Long> rateLimitScript;

    public RedisRateLimiter(ReactiveRedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.rateLimitScript = loadRateLimitScript();
    }

    /**
     * 加载限流Lua脚本
     *
     * @return RedisScript
     */
    private RedisScript<Long> loadRateLimitScript() {
        // Lua脚本实现令牌桶算法
        String script = """
                local key = KEYS[1]
                local limit = tonumber(ARGV[1])
                local window = tonumber(ARGV[2])
                local now = tonumber(ARGV[3])
                
                -- 计算令牌桶的容量和填充速率
                local capacity = limit
                local rate = limit / window
                
                -- 计算当前时间
                local currentTime = now
                
                -- 获取当前桶的状态
                local current = redis.call('get', key)
                local tokens = capacity
                local lastRefillTime = currentTime
                
                if current then
                    local parts = {}
                    for part in string.gmatch(current, "[^,]+") do
                        table.insert(parts, part)
                    end
                    if #parts == 2 then
                        tokens = tonumber(parts[1])
                        lastRefillTime = tonumber(parts[2])
                    end
                end
                
                -- 计算从上次填充到现在的时间差
                local timeElapsed = currentTime - lastRefillTime
                
                -- 计算需要填充的令牌数
                local tokensToAdd = math.floor(timeElapsed * rate)
                
                -- 填充令牌，但不超过容量
                tokens = math.min(tokens + tokensToAdd, capacity)
                lastRefillTime = currentTime
                
                -- 尝试消耗一个令牌
                local allowed = 0
                if tokens > 0 then
                    tokens = tokens - 1
                    allowed = 1
                end
                
                -- 更新桶的状态
                redis.call('set', key, tokens .. ',' .. lastRefillTime)
                redis.call('expire', key, window)
                
                return allowed
                """;
        return new DefaultRedisScript<>(script, Long.class);
    }

    /**
     * 检查是否允许请求通过
     *
     * @param key    限流键
     * @param limit  时间窗口内允许的最大请求数
     * @param window 时间窗口（秒）
     * @return 是否允许通过
     */
    public Mono<Boolean> isAllowed(String key, int limit, int window) {
        long now = Instant.now().getEpochSecond();
        return redisTemplate.execute(
                rateLimitScript,
                Collections.singletonList(key),
                String.valueOf(limit),
                String.valueOf(window),
                String.valueOf(now)
        ).next() // 将Flux转换为Mono，只取第一个结果
                .map(result -> result != null && result == 1)
                .doOnError(error -> log.error("限流检查失败: {}", error.getMessage(), error))
                .onErrorResume(error -> Mono.just(false));
    }
}