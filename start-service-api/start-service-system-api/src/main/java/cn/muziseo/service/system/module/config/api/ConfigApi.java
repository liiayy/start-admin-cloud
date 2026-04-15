package cn.muziseo.service.system.module.config.api;

import cn.muziseo.service.system.constants.ApiConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 系统参数 RPC 接口（供其他微服务远程调用）
 *
 * <p>
 * 该接口由 system 服务实现，其他业务微服务通过 OpenFeign 调用，
 * 配合 {@link cn.muziseo.common.cache.config.ConfigCacheManager} 二级缓存使用。
 * </p>
 *
 * @author 木子软件
 */
@FeignClient(name = ApiConstants.NAME, contextId = "configApi")
public interface ConfigApi {

    String PREFIX = ApiConstants.PREFIX + "/config";

    /**
     * 根据参数键名获取参数值
     *
     * @param configKey 参数键名
     * @return 参数值，不存在则返回 null
     */
    @GetMapping(PREFIX + "/value-by-key")
    String getValueByKey(@RequestParam("configKey") String configKey);
}
