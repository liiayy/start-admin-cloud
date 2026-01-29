package cn.muziseo.service.system.module.auth.service.impl;

import cn.muziseo.service.system.module.auth.controller.request.MenuAddRequest;
import cn.muziseo.service.system.module.auth.manager.MenuManager;
import cn.muziseo.service.system.module.auth.manager.RoleMenuManager;
import cn.muziseo.service.system.module.auth.repository.entity.MenuEntity;
import cn.muziseo.service.system.module.auth.repository.entity.RoleMenuEntity;
import cn.muziseo.service.system.module.auth.service.MenuService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜单业务实现
 */
@Service
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

        List<Long> menuIds = roleMenuManager.list(new LambdaQueryWrapper<RoleMenuEntity>()
                        .in(RoleMenuEntity::getRoleId, roleIds))
                .stream().map(RoleMenuEntity::getMenuId).distinct().collect(Collectors.toList());

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
