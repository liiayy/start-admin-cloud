package cn.muziseo.service.system.module.config.api;

import cn.muziseo.service.system.module.system.service.SystemConfigService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统参数 RPC 接口实现
 * <p>
 * 直接实现 {@link ConfigApi} Feign 接口，编译期保证契约一致性。
 * 路由路径完全继承自接口定义，无需重复声明。
 * </p>
 *
 * @author 木子软件
 */
@RestController
public class ConfigApiImpl implements ConfigApi {

    @Resource
    private SystemConfigService systemConfigService;

    @Override
    public String getValueByKey(String configKey) {
        return systemConfigService.getConfigValue(configKey);
    }
}
