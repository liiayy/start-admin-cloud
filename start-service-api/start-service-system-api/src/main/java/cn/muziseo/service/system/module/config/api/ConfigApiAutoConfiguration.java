package cn.muziseo.service.system.module.config.api;

import cn.muziseo.common.cache.config.ConfigUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;

/**
 * ConfigUtils RPC 回调自动装配
 * <p>
 * 条件生效规则：
 * <ol>
 *   <li>classpath 上存在 {@link ConfigUtils}（即引入了 start-common-cache）</li>
 *   <li>Spring 容器中存在 {@link ConfigApi} Bean（即启用了 Feign 并扫描到了该接口）</li>
 * </ol>
 * 满足以上两个条件时，自动将 {@code ConfigApi::getValueByKey} 注入为
 * {@link ConfigUtils} 的全局兜底回调，实现跨服务参数获取。
 * </p>
 * <p>
 * 注意：system 服务自身不走 Feign，而是通过 {@code ConfigUtilsInitializer}
 * 直连 DB，因此该配置不会与其冲突（system 服务的 Initializer 会覆盖此回调）。
 * </p>
 *
 * @author 木子软件
 */
@Slf4j
@AutoConfiguration
@ConditionalOnClass(ConfigUtils.class)
@ConditionalOnBean(ConfigApi.class)
public class ConfigApiAutoConfiguration {

    @Resource
    private ConfigApi configApi;

    @PostConstruct
    public void init() {
        ConfigUtils.setRpcFallback(configApi::getValueByKey);
        log.info("[ConfigUtils] 兜底回调已注入（Feign RPC → system 服务）");
    }
}
