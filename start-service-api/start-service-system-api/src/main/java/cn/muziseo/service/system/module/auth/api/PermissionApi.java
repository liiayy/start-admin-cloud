package cn.muziseo.service.system.module.auth.api;

import cn.muziseo.service.system.constants.ApiConstants;
import cn.muziseo.service.system.module.auth.api.dto.DataScopeRemoteDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 权限/安全 RPC 接口
 *
 * @author 木子软件
 */
@FeignClient(name = ApiConstants.NAME, contextId = "permissionApi")
public interface PermissionApi {

    String PREFIX = ApiConstants.PREFIX + "/permission";

    /**
     * 获取指定用户的数据权限范围
     *
     * @param userId 用户ID
     * @return 数据权限信息
     */
    @GetMapping(PREFIX + "/get-data-scope")
    DataScopeRemoteDTO getDataScope(@RequestParam("userId") Long userId);
}
