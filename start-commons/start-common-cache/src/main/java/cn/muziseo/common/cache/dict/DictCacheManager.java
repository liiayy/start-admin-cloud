package cn.muziseo.common.cache.dict;

import cn.muziseo.common.cache.utils.RedisUtils;
import cn.muziseo.common.core.constant.CacheConstants;
import cn.muziseo.common.core.constant.DictConstants;
import cn.muziseo.common.core.domain.dto.DictDataSimpleDTO;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * 字典数据缓存管理层（二级缓存方案）
 * <p>
 * 采用本地内存（Caffeine）与分布式缓存（Redis）结合的二级缓存架构，专为高频字典翻译场景设计。
 *
 * <p>
 * <b>设计方案：</b>
 * <ul>
 *   <li><b>一级缓存（L1 - Caffeine）：</b> JVM 本地内存，设置 10 秒短生存期（TTL）。
 *       在高并发场景（如万级数据导出翻译）下，能极大减少对 Redis 的并发压力和网络消耗。</li>
 *   <li><b>二级缓存（L2 - Redis）：</b> 分布式共享缓存，无固定过期时间，数据一致性由业务更新时主动清除保证。</li>
 *   <li><b>数据回源：</b> 两级缓存均未命中时，通过 {@code rpcFallback} 回调函数获取最新数据，并同步更新双级缓存。</li>
 * </ul>
 * </p>
 *
 * @author 木子软件
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DictCacheManager {

    /**
     * 一级缓存（防抖垫片）
     * <p>10 秒后自动过期，纯粹用来吸纳短时间内的并发海啸。</p>
     */
    private static final Cache<String, List<DictDataSimpleDTO>> CAFFEINE = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.SECONDS)
            .initialCapacity(100)
            .maximumSize(500)
            .build();

    /**
     * 获取字典数据（核心入口）
     *
     * @param dictType    字典类型编码，如 {@link DictConstants#SYS_USER_SEX}
     * @param rpcFallback 当两级缓存均未命中时的兜底回调（Feign / DB 查询）
     * @return 字典数据列表，不会返回 null
     */
    public static List<DictDataSimpleDTO> getDict(String dictType, Function<String, List<DictDataSimpleDTO>> rpcFallback) {
        List<DictDataSimpleDTO> result = CAFFEINE.get(dictType, k -> {
            // 1. L1 未命中，查 L2（Redis）
            String redisKey = CacheConstants.DICT_PREFIX + dictType;
            List<DictDataSimpleDTO> redisData = null;
            try {
                redisData = RedisUtils.getCacheList(redisKey);
            } catch (Exception e) {
                log.warn("[DictCache] 缓存列表读取失败，清理脏数据: key={}, error={}", redisKey, e.getMessage());
                RedisUtils.deleteObject(redisKey);
            }

            if (redisData != null && !redisData.isEmpty()) {
                log.debug("[DictCache] L2 命中: dictType={}", dictType);
                return redisData;
            }

            // 2. L2 也未命中，走兜底回调
            log.info("[DictCache] L1 & L2 均未命中，执行兜底回调: dictType={}", dictType);
            List<DictDataSimpleDTO> fallbackData = rpcFallback.apply(dictType);
            if (fallbackData != null && !fallbackData.isEmpty()) {
                // 回写到 Redis（不设过期时间，靠管理后台主动清除）
                RedisUtils.setCacheList(redisKey, fallbackData);
            }
            return fallbackData;
        });
        return result != null ? result : Collections.emptyList();
    }

    /**
     * 清除指定字典类型的 Redis 二级缓存
     * <p>
     * 在系统服务的字典增删改操作后调用。
     * 不需要手动清除 Caffeine，因为 10 秒后各节点自然过期。
     * </p>
     *
     * @param dictType 字典类型编码
     */
    public static void evictCache(String dictType) {
        String redisKey = CacheConstants.DICT_PREFIX + dictType;
        RedisUtils.deleteObject(redisKey);
        // 顺手把本机的 L1 也清掉，让本机立即生效
        CAFFEINE.invalidate(dictType);
        log.info("[DictCache] 缓存已清除: dictType={}", dictType);
    }

    /**
     * 清除所有字典缓存（用于全量刷新场景）
     */
    public static void evictAllCache() {
        RedisUtils.deleteKeys(CacheConstants.DICT_PREFIX + "*");
        CAFFEINE.invalidateAll();
        log.info("[DictCache] 所有字典缓存已清除");
    }
}
