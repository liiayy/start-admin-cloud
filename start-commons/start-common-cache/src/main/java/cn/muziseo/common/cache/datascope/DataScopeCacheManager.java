package cn.muziseo.common.cache.datascope;

import cn.muziseo.common.cache.utils.RedisUtils;
import cn.muziseo.common.core.constant.CacheConstants;
import cn.muziseo.common.core.constant.DataScopeConstants;
import cn.muziseo.common.core.domain.dto.DataScopeInfo;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * 数据权限缓存管理层（二级缓存方案）
 * <p>
 * 缓存用户的数据权限范围（DataScopeInfo），用于在 AOP 拦截器中快速获取权限策略，避免频繁的数据库权限计算。
 *
 * @author 木子软件
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DataScopeCacheManager {

    /**
     * 一级缓存（JVM 内存）
     * 1 秒过期，防止频繁 RPC/DB 调用
     */
    private static final Cache<Long, DataScopeInfo> CAFFEINE = Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.SECONDS)
            .initialCapacity(100)
            .maximumSize(1000)
            .build();

    /**
     * 获取数据权限范围（核心入口）
     *
     * @param userId      用户ID
     * @param rpcFallback 兜底回调（RPC / DB）
     * @return 范围信息
     */
    public static DataScopeInfo getDataScope(Long userId, Function<Long, DataScopeInfo> rpcFallback) {
        return CAFFEINE.get(userId, id -> {
            // 1. L1 未命中，查 L2（Redis）
            String redisKey = CacheConstants.DATA_SCOPE_PREFIX + id;
            DataScopeInfo redisData = RedisUtils.getCacheObject(redisKey);

            if (redisData != null) {
                return redisData;
            }

            // 2. L2 未命中，执行回调
            DataScopeInfo fallbackData = rpcFallback.apply(id);
            if (fallbackData != null) {
                RedisUtils.setCacheObject(redisKey, fallbackData);
            }
            return fallbackData;
        });
    }

    /**
     * 清除指定用户的缓存
     */
    public static void evictCache(Long userId) {
        String redisKey = CacheConstants.DATA_SCOPE_PREFIX + userId;
        RedisUtils.deleteObject(redisKey);
        CAFFEINE.invalidate(userId);
    }
}
