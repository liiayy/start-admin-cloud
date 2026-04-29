package cn.muziseo.gateway.config;

import cn.dev33.satoken.dao.auto.SaTokenDaoBySessionFollowObject;
import cn.dev33.satoken.util.SaFoxUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 二级缓存的 SaTokenDao 实现 (Caffeine 本地缓存 + Redis 远程缓存)
 * 专门为 Spring Cloud Gateway（WebFlux）定制，避免引入不兼容的 WebMvc 依赖。
 */
@Slf4j
@RequiredArgsConstructor
public class GatewaySaTokenDao implements SaTokenDaoBySessionFollowObject {

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    // Caffeine L1 缓存，5秒自动过期
    private final Cache<String, Object> caffeine = Caffeine.newBuilder()
            .expireAfterWrite(5, TimeUnit.SECONDS)
            .initialCapacity(100)
            .maximumSize(1000)
            .build();

    @Override
    public String get(String key) {
        Object cached = caffeine.get(key, k -> {
            String val = stringRedisTemplate.opsForValue().get(k);
            return val != null ? val : "";
        });
        return "".equals(cached) ? null : (String) cached;
    }

    @Override
    public void set(String key, String value, long timeout) {
        if (timeout == 0 || timeout <= NOT_VALUE_EXPIRE) {
            return;
        }
        if (timeout == NEVER_EXPIRE) {
            stringRedisTemplate.opsForValue().set(key, value);
        } else {
            stringRedisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
        }
        caffeine.invalidate(key);
    }

    @Override
    public void update(String key, String value) {
        Long expire = stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
        if (expire != null && expire > 0) {
            stringRedisTemplate.opsForValue().set(key, value, expire, TimeUnit.SECONDS);
            caffeine.invalidate(key);
        } else if (expire != null && expire == -1) {
            stringRedisTemplate.opsForValue().set(key, value);
            caffeine.invalidate(key);
        }
    }

    @Override
    public void delete(String key) {
        stringRedisTemplate.delete(key);
        caffeine.invalidate(key);
    }

    @Override
    public long getTimeout(String key) {
        Long timeout = stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
        return timeout == null ? NOT_VALUE_EXPIRE : timeout;
    }

    @Override
    public void updateTimeout(String key, long timeout) {
        stringRedisTemplate.expire(key, timeout, TimeUnit.SECONDS);
    }

    @Override
    public Object getObject(String key) {
        Object cached = caffeine.get(key, k -> {
            String json = stringRedisTemplate.opsForValue().get(k);
            if (json == null) return "";
            return json;
        });
        if ("".equals(cached)) return null;
        try {
            return objectMapper.readValue((String) cached, Object.class);
        } catch (JsonProcessingException e) {
            log.error("GatewaySaTokenDao getObject json deserialize error: key={}", key, e);
            return null;
        }
    }

    @SuppressWarnings("unchecked cast")
    @Override
    public <T> T getObject(String key, Class<T> classType) {
        Object cached = caffeine.get(key, k -> {
            String json = stringRedisTemplate.opsForValue().get(k);
            if (json == null) return "";
            return json;
        });
        if ("".equals(cached)) return null;
        try {
            return objectMapper.readValue((String) cached, classType);
        } catch (JsonProcessingException e) {
            log.error("GatewaySaTokenDao getObject json deserialize error: key={}", key, e);
            return null;
        }
    }

    @Override
    public void setObject(String key, Object object, long timeout) {
        if (timeout == 0 || timeout <= NOT_VALUE_EXPIRE) {
            return;
        }
        try {
            String json = objectMapper.writeValueAsString(object);
            if (timeout == NEVER_EXPIRE) {
                stringRedisTemplate.opsForValue().set(key, json);
            } else {
                stringRedisTemplate.opsForValue().set(key, json, timeout, TimeUnit.SECONDS);
            }
            caffeine.invalidate(key);
        } catch (JsonProcessingException e) {
            log.error("GatewaySaTokenDao setObject json serialize error: key={}", key, e);
        }
    }

    @Override
    public void updateObject(String key, Object object) {
        Long expire = stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
        if (expire != null) {
            try {
                String json = objectMapper.writeValueAsString(object);
                if (expire > 0) {
                    stringRedisTemplate.opsForValue().set(key, json, expire, TimeUnit.SECONDS);
                } else if (expire == -1) {
                    stringRedisTemplate.opsForValue().set(key, json);
                }
                caffeine.invalidate(key);
            } catch (JsonProcessingException e) {
                log.error("GatewaySaTokenDao updateObject json serialize error: key={}", key, e);
            }
        }
    }

    @Override
    public void deleteObject(String key) {
        delete(key);
    }

    @Override
    public long getObjectTimeout(String key) {
        return getTimeout(key);
    }

    @Override
    public void updateObjectTimeout(String key, long timeout) {
        updateTimeout(key, timeout);
    }

    @Override
    public List<String> searchData(String prefix, String keyword, int start, int size, boolean sortType) {
        String keyStr = prefix + "*" + keyword + "*";
        Collection<String> keys = stringRedisTemplate.keys(keyStr);
        List<String> list = new ArrayList<>(keys != null ? keys : new ArrayList<>());
        return SaFoxUtil.searchList(list, start, size, sortType);
    }
}
