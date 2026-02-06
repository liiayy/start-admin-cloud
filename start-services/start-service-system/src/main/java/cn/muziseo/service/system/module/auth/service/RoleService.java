package cn.muziseo.service.system.module.auth.service;

import cn.muziseo.service.system.module.auth.repository.entity.RoleEntity;

import java.util.List;

/**
 * 角色业务接口
 * <p>
 * 提供角色的增删改查、用户角色查询、分配菜单等功能
 *
 * @author 木子软件
 * @Date 2026-01-29
 * @Wechat liiayy
 * @Email 773582348@qq.com
 * @Copyright <a href="https://code.muziseo.cn">木子软件</a>
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
