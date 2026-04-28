package cn.muziseo.service.system.module.auth.api;

import cn.hutool.core.bean.BeanUtil;
import cn.muziseo.common.core.domain.dto.DataScopeInfo;
import cn.muziseo.service.system.datascope.DataScopeService;
import cn.muziseo.service.system.module.auth.api.dto.DataScopeRemoteDTO;
import cn.muziseo.service.system.module.auth.convert.UserConverter;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RestController;

/**
 * 权限/安全 RPC 接口实现
 *
 * @author 木子软件
 */
@RestController
public class PermissionApiImpl implements PermissionApi {

    @Resource
    private DataScopeService dataScopeService;

    @Resource
    private UserConverter userConverter;

    @Override
    public DataScopeRemoteDTO getDataScope(Long userId) {
        DataScopeInfo info = dataScopeService.getDataScopeInfo(userId);
        return userConverter.toRemoteDTO(info);
    }
}
