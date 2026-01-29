package cn.muziseo.service.system.module.auth.service;

import cn.muziseo.service.system.module.auth.repository.entity.RoleEntity;

import java.util.List;

/**
 * 角色业务接口
 */
public interface RoleService {
    /**
     * 获取用户角色列表
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    List<RoleEntity> getRolesByUserId(Long userId);

    /**
     * 添加角色
     *
     * @param request 添加请求
     */
    void addRole(cn.muziseo.service.system.module.auth.controller.request.RoleAddRequest request);

    /**
     * 给角色分配菜单
     *
     * @param roleId  角色ID
     * @param menuIds 菜单ID列表
     */
    void assignMenus(Long roleId, List<Long> menuIds);
}
