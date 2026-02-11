package cn.muziseo.service.system.module.auth.service.impl;

import cn.muziseo.service.system.module.auth.controller.request.MenuAddRequest;
import cn.muziseo.service.system.module.auth.manager.MenuManager;
import cn.muziseo.service.system.module.auth.manager.RoleMenuManager;
import cn.muziseo.service.system.module.auth.repository.entity.MenuEntity;
import cn.muziseo.service.system.module.auth.service.MenuService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 菜单业务实现
 * <p>
 * 实现菜单的增删改查、角色菜单查询等功能
 *
 * @author 木子软件
 * @Date 2026-01-29
 * @Wechat liiayy
 * @Email 773582348@qq.com
 * @Copyright <a href="https://code.muziseo.cn">木子软件</a>
 */
@Service
@Slf4j
public class MenuServiceImpl implements MenuService {

    @Resource
    private MenuManager menuManager;

    @Resource
    private RoleMenuManager roleMenuManager;

    @Override
    public List<MenuEntity> getMenusByRoleIds(List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return List.of();
        }

        // 调用Manager层获取菜单ID
        List<Long> menuIds = roleMenuManager.getMenuIdsByRoleIds(roleIds);

        if (menuIds.isEmpty()) {
            return List.of();
        }

        return menuManager.listByIds(menuIds);
    }

    @Override
    public List<MenuEntity> addMenu(MenuAddRequest request) {
        MenuEntity menuEntity = cn.hutool.core.bean.BeanUtil.copyProperties(request, MenuEntity.class);
        menuManager.save(menuEntity);
        return menuManager.list();
    }
}
