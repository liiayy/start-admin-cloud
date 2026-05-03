package cn.muziseo.common.social.utils;

import cn.muziseo.common.cache.utils.RedisUtils;
import cn.muziseo.common.core.constant.CacheConstants;
import me.zhyd.oauth.cache.AuthStateCache;

import java.time.Duration;

/**
 * 基于 Redis 实现的 OAuth 状态缓存
 *
 * @author 木子软件
 */
public class AuthRedisStateCache implements AuthStateCache {

    @Override
    public void cache(String key, String value) {
        RedisUtils.setCacheObject(
            CacheConstants.SOCIAL_AUTH_CODE_PREFIX + key,
            value,
            Duration.ofMinutes(3)
        );
    }

    @Override
    public void cache(String key, String value, long timeout) {
        RedisUtils.setCacheObject(
            CacheConstants.SOCIAL_AUTH_CODE_PREFIX + key,
            value,
            Duration.ofMillis(timeout)
        );
    }

    @Override
    public String get(String key) {
        return RedisUtils.getCacheObject(CacheConstants.SOCIAL_AUTH_CODE_PREFIX + key);
    }

    @Override
    public boolean containsKey(String key) {
        return RedisUtils.hasKey(CacheConstants.SOCIAL_AUTH_CODE_PREFIX + key);
    }
}
