package cn.muziseo.service.system.module.config;

import cn.muziseo.common.cache.config.ConfigUtils;
import cn.muziseo.common.cache.dict.DictUtils;
import cn.muziseo.common.core.domain.dto.DictDataSimpleDTO;
import cn.muziseo.service.system.module.system.manager.DictManager;
import cn.muziseo.service.system.module.system.service.SystemConfigService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 系统参数与字典工具类初始化器
 * <p>
 * system 服务自身作为数据的源头，直接将 Service/Manager 层方法注入为全局兜底回调。
 * 其他微服务通过 Feign 调用对应 Api。
 * </p>
 *
 * @author 木子软件
 */
@Slf4j
@Configuration
public class ConfigUtilsInitializer {

    @Resource
    private SystemConfigService systemConfigService;

    @Resource
    private DictManager dictManager;

    @PostConstruct
    public void init() {
        // 1. 初始化系统参数工具
        ConfigUtils.setRpcFallback(systemConfigService::getConfigValue);

        // 2. 初始化字典工具
        DictUtils.setRpcFallback(dictType -> {
            var list = dictManager.listByDictType(dictType);
            return list.stream()
                    .map(e -> DictDataSimpleDTO.builder()
                            .label(e.getLabel())
                            .value(e.getValue())
                            .dictType(e.getDictType())
                            .colorType(e.getColorType())
                            .cssClass(e.getCssClass())
                            .build())
                    .toList();
        });

        log.info("[ConfigUtils/DictUtils] 兜底回调已注入（system 服务直连 DB）");
    }
}
