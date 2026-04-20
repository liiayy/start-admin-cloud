package cn.muziseo.common.cache.dict;

import cn.muziseo.common.cache.utils.RedisUtils;
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
 * 字典二级缓存管理器
 *
 * <p>
 * <b>设计思路：</b>
 * <ul>
 *   <li><b>一级缓存（Caffeine）</b>：JVM 本地内存，10 秒极短 TTL，
 *       核心目的是在短时间高频请求场景下（如 Excel 导出循环翻译 10000 行）
 *       避免大量重复网络 IO 打穿 Redis。</li>
 *   <li><b>二级缓存（Redis）</b>：集中共享缓存，无过期时间，
 *       仅在后台管理端执行字典增删改时由 system 服务主动清除。</li>
 *   <li><b>兜底回调（RPC / DB）</b>：当两级缓存均未命中时，通过调用方传入的
 *       {@code Function} 回调（可以是 Feign 调用、也可以是直接查询 DB）获取最新数据，
 *       并回写入 Redis + Caffeine。</li>
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
            String redisKey = DictConstants.DICT_CACHE_KEY_PREFIX + dictType;
            List<DictDataSimpleDTO> redisData = RedisUtils.getCacheObjectSafe(redisKey);

            if (redisData != null && !redisData.isEmpty()) {
                log.debug("[DictCache] L2 命中: dictType={}", dictType);
                return redisData;
            }

            // 2. L2 也未命中，走兜底回调
            log.info("[DictCache] L1 & L2 均未命中，执行兜底回调: dictType={}", dictType);
            List<DictDataSimpleDTO> fallbackData = rpcFallback.apply(dictType);
            if (fallbackData != null && !fallbackData.isEmpty()) {
                // 回写到 Redis（不设过期时间，靠管理后台主动清除）
                RedisUtils.setCacheObject(redisKey, fallbackData);
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
        String redisKey = DictConstants.DICT_CACHE_KEY_PREFIX + dictType;
        RedisUtils.deleteObject(redisKey);
        // 顺手把本机的 L1 也清掉，让本机立即生效
        CAFFEINE.invalidate(dictType);
        log.info("[DictCache] 缓存已清除: dictType={}", dictType);
    }

    /**
     * 清除所有字典缓存（用于全量刷新场景）
     */
    public static void evictAllCache() {
        RedisUtils.deleteKeys(DictConstants.DICT_CACHE_KEY_PREFIX + "*");
        CAFFEINE.invalidateAll();
        log.info("[DictCache] 所有字典缓存已清除");
    }
}
