package cn.muziseo.service.system.module.config;

import cn.muziseo.common.cache.config.ConfigUtils;
import cn.muziseo.service.system.module.system.service.SystemConfigService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

/**
 * 系统参数工具类初始化器
 * <p>
 * system 服务自身作为参数的源头，直接将 Service 层方法注入为 RPC 兜底回调。
 * 其他微服务通过 Feign 调用 ConfigApi。
 * </p>
 *
 * @author 木子软件
 */
@Slf4j
@Configuration
public class ConfigUtilsInitializer {

    @Resource
    private SystemConfigService systemConfigService;

    @PostConstruct
    public void init() {
        ConfigUtils.setRpcFallback(systemConfigService::getConfigValue);
        log.info("[ConfigUtils] 兜底回调已注入（system 服务直连 DB）");
    }
}
