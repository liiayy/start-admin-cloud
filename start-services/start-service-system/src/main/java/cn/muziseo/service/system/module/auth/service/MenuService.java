package cn.muziseo.service.system.module.auth.service;

import cn.muziseo.service.system.module.auth.controller.request.MenuAddRequest;
import cn.muziseo.service.system.module.auth.repository.entity.MenuEntity;

import java.util.List;

/**
 * 菜单业务接口
 */
public interface MenuService {
    /**
     * 获取角色菜单列表
     *
     * @param roleIds 角色ID列表
     * @return 菜单列表
     */
    List<MenuEntity> getMenusByRoleIds(List<Long> roleIds);

    /**
     * 添加菜单
     *
     * @return 菜单
     */
    List<MenuEntity> addMenu(MenuAddRequest request);
}
