package cn.muziseo.common.cache.config;

import cn.muziseo.common.cache.utils.RedisUtils;
import cn.muziseo.common.core.constant.CacheConstants;
import cn.muziseo.common.core.constant.ConfigConstants;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * 系统参数缓存管理层（二级缓存方案）
 * <p>
 * 采用本地内存（Caffeine）与分布式缓存（Redis）结合的二级缓存架构，有效降低 Redis 网络 IO 并防止缓存击穿。
 *
 * <p>
 * <b>设计方案：</b>
 * <ul>
 *   <li><b>一级缓存（L1 - Caffeine）：</b> JVM 本地内存，设置 10 秒短生存期（TTL），用于吸纳瞬时高并发流量。</li>
 *   <li><b>二级缓存（L2 - Redis）：</b> 分布式共享缓存，无固定过期时间，数据一致性由后台更新时主动清除保证。</li>
 *   <li><b>数据回源：</b> 两级缓存均未命中时，通过 {@code rpcFallback} 回调函数从持久化层（DB 或 RPC）加载。</li>
 * </ul>
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
            String redisKey = CacheConstants.CONFIG_PREFIX + configKey;
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
        String redisKey = CacheConstants.CONFIG_PREFIX + configKey;
        RedisUtils.deleteObject(redisKey);
        // 顺手把本机的 L1 也清掉，让本机立即生效
        CAFFEINE.invalidate(configKey);
        log.info("[ConfigCache] 缓存已清除: configKey={}", configKey);
    }

    /**
     * 清除所有系统参数缓存（用于全量刷新场景）
     */
    public static void evictAllCache() {
        RedisUtils.deleteKeys(CacheConstants.CONFIG_PREFIX + "*");
        CAFFEINE.invalidateAll();
        log.info("[ConfigCache] 所有系统参数缓存已清除");
    }
}
