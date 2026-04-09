package cn.muziseo.common.satoken.integration.service;

import java.util.List;
import java.util.Set;

/**
 * 权限加载服务接口
 * <p>
 * 定义从数据源加载用户角色和权限的方法，由业务服务实现
 *
 * @author 木子软件
 */
public interface SaPermissionService {

    /**
     * 获取用户角色编码列表
     *
     * @param userId 用户ID
     * @return 角色编码集合
     */
    Set<String> getRoleCodes(Long userId);

    /**
     * 获取用户权限标识列表
     *
     * @param userId 用户ID
     * @return 权限标识集合
     */
    Set<String> getPermissionList(Long userId);
}
