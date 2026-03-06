package cn.muziseo.service.system.module.permission.service;

import cn.muziseo.service.system.module.permission.controller.request.MenuAddRequest;
import cn.muziseo.service.system.module.permission.repository.entity.MenuEntity;

import java.util.List;

/**
 * 菜单业务接口
 * <p>
 * 提供菜单的增删改查、角色菜单查询等功能
 *
 * @author 木子软件
 * @Date 2026-01-29
 * @Wechat liiayy
 * @Email 773582348@qq.com
 * @Copyright <a href="https://code.muziseo.cn">木子软件</a>
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
