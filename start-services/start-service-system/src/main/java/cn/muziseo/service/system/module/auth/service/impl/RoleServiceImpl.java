package cn.muziseo.service.system.module.auth.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.muziseo.service.system.module.auth.controller.request.RoleAddRequest;
import cn.muziseo.service.system.module.auth.manager.RoleManager;
import cn.muziseo.service.system.module.auth.manager.RoleMenuManager;
import cn.muziseo.service.system.module.auth.manager.UserRoleManager;
import cn.muziseo.service.system.module.auth.repository.entity.RoleEntity;
import cn.muziseo.service.system.module.auth.repository.entity.RoleMenuEntity;
import cn.muziseo.service.system.module.auth.repository.entity.UserRoleEntity;
import cn.muziseo.service.system.module.auth.service.RoleService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色业务实现
 */
@Service
public class RoleServiceImpl implements RoleService {

    @Resource
    private RoleManager roleManager;

    @Resource
    private UserRoleManager userRoleManager;

    @Resource
    private RoleMenuManager roleMenuManager;

    @Override
    public List<RoleEntity> getRolesByUserId(Long userId) {
        List<Long> roleIds = userRoleManager.list(new LambdaQueryWrapper<UserRoleEntity>()
                        .eq(UserRoleEntity::getUserId, userId))
                .stream().map(UserRoleEntity::getRoleId).collect(Collectors.toList());

        if (roleIds.isEmpty()) {
            return List.of();
        }

        return roleManager.listByIds(roleIds);
    }

    @Override
    public void addRole(RoleAddRequest request) {
        RoleEntity roleEntity = BeanUtil.copyProperties(request, RoleEntity.class);
        roleManager.save(roleEntity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignMenus(Long roleId, List<Long> menuIds) {
        // 1. 删除原有关联
        roleMenuManager.remove(new LambdaQueryWrapper<RoleMenuEntity>()
                .eq(RoleMenuEntity::getRoleId, roleId));

        // 2. 插入新关联
        if (menuIds != null && !menuIds.isEmpty()) {
            List<RoleMenuEntity> list = menuIds.stream().map(menuId -> RoleMenuEntity.builder()
                    .roleId(roleId)
                    .menuId(menuId)
                    .build()).collect(Collectors.toList());
            roleMenuManager.saveBatch(list);
        }
    }
}
