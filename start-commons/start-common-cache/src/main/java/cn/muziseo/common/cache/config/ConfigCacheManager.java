package cn.muziseo.common.cache.config;

import cn.muziseo.common.cache.utils.RedisUtils;
import cn.muziseo.common.core.constant.ConfigConstants;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * 系统参数二级缓存管理器
 *
 * <p>
 * <b>设计思路（与 DictCacheManager 完全一致）：</b>
 * <ul>
 *   <li><b>一级缓存（Caffeine）</b>：JVM 本地内存，10 秒极短 TTL，
 *       防止短时间高频请求打穿 Redis。</li>
 *   <li><b>二级缓存（Redis）</b>：集中共享缓存，无过期时间，
 *       仅在后台管理端执行参数修改/删除时由 system 服务主动清除。</li>
 *   <li><b>兜底回调（RPC / DB）</b>：当两级缓存均未命中时，通过调用方传入的
 *       {@code Function} 回调获取最新数据，并回写入 Redis + Caffeine。</li>
 * </ul>
 * </p>
 *
 * <p>
 * <b>与 DictCacheManager 的区别：</b>缓存粒度为单条 configKey → String value，
 * 而非字典的 dictType → List&lt;DTO&gt;。
 * </p>
 *
 * @author 木子软件
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConfigCacheManager {

    /**
     * 一级缓存（防抖垫片）
     * <p>10 秒后自动过期，纯粹用来吸纳短时间内的并发海啸。</p>
     * <p>使用 Optional 包装来区分"未缓存"和"值为 null"的情况。</p>
     */
    private static final Cache<String, String> CAFFEINE = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.SECONDS)
            .initialCapacity(100)
            .maximumSize(500)
            .build();

    /**
     * 空值标记：用于区分"缓存了但值为 null"和"没有缓存"
     */
    private static final String NULL_PLACEHOLDER = "__CONFIG_NULL__";

    /**
     * 获取系统参数值（核心入口）
     *
     * @param configKey   参数键名，如 {@link ConfigConstants#USER_DEFAULT_PASSWORD}
     * @param rpcFallback 当两级缓存均未命中时的兜底回调（Feign / DB 查询）
     * @return 参数值，可能为 null（表示确实不存在该参数）
     */
    public static String getConfigValue(String configKey, Function<String, String> rpcFallback) {
        String result = CAFFEINE.get(configKey, k -> {
            // 1. L1 未命中，查 L2（Redis）
            String redisKey = ConfigConstants.CONFIG_CACHE_KEY_PREFIX + configKey;
            String redisData = RedisUtils.getCacheObjectSafe(redisKey);

            if (redisData != null) {
                log.debug("[ConfigCache] L2 命中: configKey={}", configKey);
                return redisData;
            }

            // 2. L2 也未命中，走兜底回调
            log.info("[ConfigCache] L1 & L2 均未命中，执行兜底回调: configKey={}", configKey);
            String fallbackData = rpcFallback.apply(configKey);
            // 回写到 Redis（不设过期时间，靠管理后台主动清除）
            String valueToCache = fallbackData != null ? fallbackData : NULL_PLACEHOLDER;
            RedisUtils.setCacheObject(redisKey, valueToCache);
            return valueToCache;
        });

        // 还原空值标记
        return NULL_PLACEHOLDER.equals(result) ? null : result;
    }

    /**
     * 清除指定参数的 Redis 二级缓存
     * <p>
     * 在系统服务的参数修改/删除操作后调用。
     * 不需要手动清除 Caffeine，因为 10 秒后各节点自然过期。
     * </p>
     *
     * @param configKey 参数键名
     */
    public static void evictCache(String configKey) {
        String redisKey = ConfigConstants.CONFIG_CACHE_KEY_PREFIX + configKey;
        RedisUtils.deleteObject(redisKey);
        // 顺手把本机的 L1 也清掉，让本机立即生效
        CAFFEINE.invalidate(configKey);
        log.info("[ConfigCache] 缓存已清除: configKey={}", configKey);
    }

    /**
     * 清除所有系统参数缓存（用于全量刷新场景）
     */
    public static void evictAllCache() {
        RedisUtils.deleteKeys(ConfigConstants.CONFIG_CACHE_KEY_PREFIX + "*");
        CAFFEINE.invalidateAll();
        log.info("[ConfigCache] 所有系统参数缓存已清除");
    }
}
