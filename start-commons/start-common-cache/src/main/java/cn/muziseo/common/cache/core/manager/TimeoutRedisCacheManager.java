package cn.muziseo.common.cache.core.manager;

import cn.hutool.core.util.StrUtil;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 超时 Redis 缓存管理器
 * <p>
 * 支持多级过期时间的 Redis 缓存管理器
 *
 * @author 木子软件
 * @Date 2026-01-26
 * @Copyright <a href="https://code.muziseo.cn">木子软件</a>
 */
public class TimeoutRedisCacheManager extends RedisCacheManager {

    private static final String SPLIT = "#";

    /**
     * 缓存解析结果，避免重复字符串操作
     */
    private final Map<String, Duration> cacheDurationMap = new ConcurrentHashMap<>();

    /**
     * 构造方法
     *
     * @param cacheWriter               缓存写入器
     * @param defaultCacheConfiguration 默认缓存配置
     */
    public TimeoutRedisCacheManager(RedisCacheWriter cacheWriter, RedisCacheConfiguration defaultCacheConfiguration) {
        super(cacheWriter, defaultCacheConfiguration);
    }

    /**
     * 创建 Redis 缓存
     *
     * @param name        缓存名称
     * @param cacheConfig 缓存配置
     * @return RedisCache
     */
    @Override
    protected RedisCache createRedisCache(String name, RedisCacheConfiguration cacheConfig) {
        if (StrUtil.isEmpty(name) || !name.contains(SPLIT)) {
            return super.createRedisCache(name, cacheConfig);
        }

        // 1. 拆分名称与 TTL 指令
        String[] names = StrUtil.splitToArray(name, SPLIT);
        if (names.length != 2) {
            return super.createRedisCache(name, cacheConfig);
        }

        String cacheName = names[0];
        String ttlWithSuffix = names[1];

        // 2. 解析真正的 TTL 字符串（处理可能存在的冒号后缀）
        // 例如 "1h:custom" -> ttlPart="1h", suffix=":custom"
        String ttlPart = StrUtil.subBefore(ttlWithSuffix, StrUtil.COLON, false);
        String suffix = StrUtil.subAfter(ttlWithSuffix, ttlPart, false);

        // 3. 获取或计算 Duration
        Duration duration = cacheDurationMap.computeIfAbsent(ttlPart, this::parseDuration);

        // 4. 动态修改配置
        if (cacheConfig != null && !duration.isNegative()) {
            cacheConfig = cacheConfig.entryTtl(duration);
        }

        // 5. 还原干净的缓存名称（保留后缀以支持特定的前缀策略）
        return super.createRedisCache(cacheName + suffix, cacheConfig);
    }

    /**
     * 解析持续时间
     *
     * @param ttlStr TTL 字符串
     * @return Duration
     */
    private Duration parseDuration(String ttlStr) {
        try {
            String timeUnit = StrUtil.subSuf(ttlStr, -1).toLowerCase();
            String numStr = StrUtil.sub(ttlStr, 0, -1);

            return switch (timeUnit) {
                case "d" -> Duration.ofDays(Long.parseLong(numStr));
                case "h" -> Duration.ofHours(Long.parseLong(numStr));
                case "m" -> Duration.ofMinutes(Long.parseLong(numStr));
                case "s" -> Duration.ofSeconds(Long.parseLong(numStr));
                // 如果末尾是数字而非字母，尝试整体解析为秒
                default -> Duration.ofSeconds(Long.parseLong(ttlStr));
            };
        } catch (Exception e) {
            // 解析失败返回 -1，由调用处判断并回退到默认配置
            return Duration.ofSeconds(-1);
        }
    }
}
