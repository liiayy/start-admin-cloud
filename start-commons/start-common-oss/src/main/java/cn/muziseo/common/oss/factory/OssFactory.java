package cn.muziseo.common.oss.factory;

import cn.muziseo.common.oss.core.LocalOssStrategy;
import cn.muziseo.common.oss.core.OssClient;
import cn.muziseo.common.oss.core.S3OssStrategy;
import cn.muziseo.common.oss.entity.OssProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * OSS 客户端工厂类
 *
 * @author 木子软件
 */
@Slf4j
public class OssFactory {

    /**
     * 客户端本地缓存
     */
    private static final Map<String, OssClient> CLIENT_CACHE = new ConcurrentHashMap<>();

    /**
     * 获取实例 (默认缓存)
     *
     * @param configKey 配置标识
     * @return OSS 客户端
     */
    public static OssClient instance(String configKey) {
        OssClient client = CLIENT_CACHE.get(configKey);
        if (client == null) {
            log.error("OSS 客户端 [{}] 未初始化", configKey);
            throw new RuntimeException("OSS 客户端未配置或启动失败");
        }
        return client;
    }

    public static OssClient init(OssProperties properties) {
        OssClient client = create(properties);
        CLIENT_CACHE.put(properties.getConfigKey(), client);
        log.info("初始化 OSS 客户端成功: key={}", properties.getConfigKey());
        return client;
    }

    /**
     * 创建一个新的客户端 (不加入缓存)
     *
     * @param properties 配置
     * @return OSS 客户端
     */
    public static OssClient create(OssProperties properties) {
        // 判断策略类型
        String service = properties.getService();
        if ("local".equalsIgnoreCase(service) || !StringUtils.hasText(properties.getEndpoint())) {
            return new LocalOssStrategy(properties);
        } else {
            return new S3OssStrategy(properties);
        }
    }

    /**
     * 移除客户端缓存
     *
     * @param configKey 配置标识
     */
    public static void remove(String configKey) {
        CLIENT_CACHE.remove(configKey);
        log.info("移除 OSS 客户端成功: key={}", configKey);
    }
    /**
     * 清空所有客户端缓存
     */
    public static void clear() {
        CLIENT_CACHE.clear();
        log.info("已清空所有 OSS 客户端缓存");
    }

}
