package cn.muziseo.service.system.module.permission.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.muziseo.common.core.exception.BusinessException;
import cn.muziseo.service.system.enums.MenuErrorCode;
import cn.muziseo.service.system.module.auth.manager.UserRoleManager;
import cn.muziseo.service.system.module.auth.service.SaSessionRefreshService;
import cn.muziseo.service.system.module.permission.controller.request.MenuAddRequest;
import cn.muziseo.service.system.module.permission.controller.request.MenuUpdateRequest;
import cn.muziseo.service.system.module.permission.controller.vo.MenuTreeVO;
import cn.muziseo.service.system.module.permission.controller.vo.MenuVO;
import cn.muziseo.service.system.module.permission.manager.MenuManager;
import cn.muziseo.service.system.module.permission.manager.RoleMenuManager;
import cn.muziseo.service.system.module.permission.repository.entity.MenuEntity;
import cn.muziseo.service.system.module.permission.service.MenuService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 菜单业务实现
 *
 * @author 木子软件
 */
@Service
@Slf4j
public class MenuServiceImpl implements MenuService {

    @Resource
    private MenuManager menuManager;

    @Resource
    private RoleMenuManager roleMenuManager;

    @Resource
    private UserRoleManager userRoleManager;

    @Resource
    private SaSessionRefreshService saSessionRefreshService;

    @Override
    public List<MenuVO> getMenusByRoleIds(List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return List.of();
        }
        List<Long> menuIds = roleMenuManager.getMenuIdsByRoleIds(roleIds);
        if (menuIds.isEmpty()) {
            return List.of();
        }
        return menuManager.listByIds(menuIds).stream()
                .map(this::toMenuVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MenuTreeVO> getMenuTree() {
        List<MenuEntity> allMenus = menuManager.list(
                com.mybatisflex.core.query.QueryWrapper.create()
                        .orderBy(MenuEntity::getSort, true)
                        .orderBy(MenuEntity::getId, true));
        return buildTree(allMenus, 0L);
    }

    @Override
    public MenuVO getMenu(Long id) {
        MenuEntity entity = menuManager.getById(id);
        if (entity == null) {
            throw new BusinessException(MenuErrorCode.MENU_NOT_EXISTS);
        }
        return toMenuVO(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addMenu(MenuAddRequest request) {
        if (request.getPermission() != null && !request.getPermission().isEmpty()) {
            if (menuManager.existsByPermission(request.getPermission(), null)) {
                throw new BusinessException(MenuErrorCode.MENU_PERMISSION_EXISTS);
            }
        }
        MenuEntity entity = BeanUtil.copyProperties(request, MenuEntity.class);
        if (entity.getStatus() == null) {
            entity.setStatus(0);
        }
        if (entity.getParentId() == null) {
            entity.setParentId(0L);
        }
        menuManager.save(entity);
        log.info("新增菜单成功: id={}, name={}", entity.getId(), entity.getName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMenu(MenuUpdateRequest request) {
        MenuEntity existing = menuManager.getById(request.getId());
        if (existing == null) {
            throw new BusinessException(MenuErrorCode.MENU_NOT_EXISTS);
        }
        if (request.getPermission() != null && !request.getPermission().isEmpty()) {
            if (menuManager.existsByPermission(request.getPermission(), request.getId())) {
                throw new BusinessException(MenuErrorCode.MENU_PERMISSION_EXISTS);
            }
        }
        MenuEntity entity = BeanUtil.copyProperties(request, MenuEntity.class);
        menuManager.updateById(entity);

        // 刷新拥有该菜单关联角色的用户 Session
        refreshByMenuId(request.getId());
        log.info("修改菜单成功: id={}", request.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMenu(Long id) {
        MenuEntity existing = menuManager.getById(id);
        if (existing == null) {
            throw new BusinessException(MenuErrorCode.MENU_NOT_EXISTS);
        }
        // 检查是否有子菜单
        List<MenuEntity> children = menuManager.listByParentId(id);
        if (!children.isEmpty()) {
            throw new BusinessException(MenuErrorCode.MENU_HAS_CHILDREN);
        }

        // 先获取受影响的用户，再删除关联
        List<Long> affectedUserIds = getAffectedUserIdsByMenuId(id);
        roleMenuManager.deleteByMenuId(id);
        menuManager.removeById(id);

        saSessionRefreshService.refreshUserSessions(affectedUserIds);
        log.info("删除菜单成功: id={}, 影响用户数={}", id, affectedUserIds.size());
    }

    /**
     * 获取拥有某菜单关联角色的用户ID列表
     */
    private List<Long> getAffectedUserIdsByMenuId(Long menuId) {
        List<Long> roleIds = roleMenuManager.getRoleIdsByMenuId(menuId);
        if (roleIds.isEmpty()) {
            return List.of();
        }
        return userRoleManager.getUserIdsByRoleIds(roleIds);
    }

    /**
     * 刷新拥有某菜单关联角色的用户 Session
     */
    private void refreshByMenuId(Long menuId) {
        List<Long> userIds = getAffectedUserIdsByMenuId(menuId);
        saSessionRefreshService.refreshUserSessions(userIds);
    }

    /**
     * 构建菜单树 (O(N) 优化版，使用 Map 避免递归遍历所有元素)
     */
    private List<MenuTreeVO> buildTree(List<MenuEntity> menus, Long rootId) {
        if (menus == null || menus.isEmpty()) {
            return Collections.emptyList();
        }

        // 1. 将所有元素转换为 VO 并按父 ID 分组
        Map<Long, List<MenuTreeVO>> parentMap = menus.stream()
                .collect(Collectors.groupingBy(
                        MenuEntity::getParentId,
                        Collectors.mapping(this::toMenuTreeVO, Collectors.toList())
                ));

        // 2. 将列表转换为以 ID 为 Key 的 Map，方便查找
        List<MenuTreeVO> allVos = parentMap.values().stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());

        Map<Long, MenuTreeVO> voMap = allVos.stream()
                .collect(Collectors.toMap(MenuTreeVO::getId, vo -> vo));

        // 3. 构建嵌套结构
        allVos.forEach(vo -> {
            List<MenuTreeVO> children = parentMap.get(vo.getId());
            if (children != null) {
                vo.setChildren(children);
            }
        });

        // 4. 返回根节点下的数据
        return parentMap.getOrDefault(rootId, Collections.emptyList());
    }

    private MenuVO toMenuVO(MenuEntity entity) {
        return MenuVO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .permission(entity.getPermission())
                .type(entity.getType())
                .parentId(entity.getParentId())
                .sort(entity.getSort())
                .path(entity.getPath())
                .component(entity.getComponent())
                .componentName(entity.getComponentName())
                .icon(entity.getIcon())
                .status(entity.getStatus())
                .visible(entity.getVisible())
                .keepAlive(entity.getKeepAlive())
                .alwaysShow(entity.getAlwaysShow())
                .createTime(entity.getCreateTime())
                .build();
    }

    private MenuTreeVO toMenuTreeVO(MenuEntity entity) {
        MenuTreeVO vo = new MenuTreeVO();
        vo.setId(entity.getId());
        vo.setName(entity.getName());
        vo.setPermission(entity.getPermission());
        vo.setType(entity.getType());
        vo.setParentId(entity.getParentId());
        vo.setSort(entity.getSort());
        vo.setPath(entity.getPath());
        vo.setComponent(entity.getComponent());
        vo.setComponentName(entity.getComponentName());
        vo.setIcon(entity.getIcon());
        vo.setStatus(entity.getStatus());
        vo.setVisible(entity.getVisible());
        vo.setKeepAlive(entity.getKeepAlive());
        vo.setAlwaysShow(entity.getAlwaysShow());
        vo.setCreateTime(entity.getCreateTime());
        return vo;
    }
}
