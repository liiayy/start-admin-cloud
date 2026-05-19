package cn.muziseo.service.system.module.permission.service.impl;

import cn.muziseo.common.core.datatracer.DataTracerTypeEnum;
import cn.muziseo.common.log.utils.DataTracerUtils;

import cn.hutool.core.bean.BeanUtil;
import cn.muziseo.common.core.exception.BusinessException;
import cn.muziseo.common.core.util.UserSecurityUtils;
import cn.muziseo.service.system.enums.MenuErrorCode;
import cn.muziseo.service.system.module.auth.manager.UserRoleManager;
import cn.muziseo.service.system.module.auth.service.SaSessionRefreshService;
import cn.muziseo.service.system.module.permission.controller.request.MenuCreateRequest;
import cn.muziseo.service.system.module.permission.controller.request.MenuUpdateRequest;
import cn.muziseo.service.system.module.permission.controller.vo.MenuTreeVO;
import cn.muziseo.service.system.module.permission.controller.vo.MenuVO;
import cn.muziseo.service.system.module.permission.manager.MenuManager;
import cn.muziseo.service.system.module.permission.manager.RoleMenuManager;
import cn.muziseo.service.system.module.permission.repository.entity.MenuEntity;
import cn.muziseo.service.system.module.permission.service.MenuService;
import cn.muziseo.service.system.module.permission.convert.MenuConverter;
import com.mybatisflex.core.query.QueryWrapper;
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

    @Resource
    private MenuConverter menuConverter;

    /**
     * 根据角色 ID 列表获取菜单列表
     *
     * @param roleIds 角色 ID 列表
     * @return 菜单视图对象列表
     */
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
                .map(menuConverter::toVO)
                .collect(Collectors.toList());
    }

    /**
     * 获取用户的菜单树
     * <p>
     * 1. 超级管理员获取所有菜单
     * 2. 普通用户根据角色获取关联菜单
     *
     * @param userId 用户 ID
     * @return 菜单树视图对象列表
     */
    @Override
    public List<MenuTreeVO> getUserMenuTree(Long userId) {
        List<MenuEntity> menus;
        // 1. 超级管理员获取所有菜单
        if (UserSecurityUtils.isSuperAdmin(userId)) {
            menus = menuManager.list(
                    QueryWrapper.create()
                            .orderBy(MenuEntity::getSort, true)
                            .orderBy(MenuEntity::getId, true));
        } else {
            // 2. 普通用户根据角色获取菜单
            List<Long> roleIds = userRoleManager.getRoleIdsByUserId(userId);
            if (roleIds.isEmpty()) {
                return Collections.emptyList();
            }
            List<Long> menuIds = roleMenuManager.getMenuIdsByRoleIds(roleIds);
            if (menuIds.isEmpty()) {
                return Collections.emptyList();
            }
            menus = menuManager.listByIds(menuIds).stream()
                    .sorted(java.util.Comparator.comparing(MenuEntity::getSort).thenComparing(MenuEntity::getId))
                    .collect(Collectors.toList());
        }
        return buildTree(menus, 0L);
    }

    /**
     * 获取全量菜单树
     *
     * @return 菜单树视图对象列表
     */
    @Override
    public List<MenuTreeVO> getMenuTree() {
        List<MenuEntity> allMenus = menuManager.list(
                QueryWrapper.create()
                        .orderBy(MenuEntity::getSort, true)
                        .orderBy(MenuEntity::getId, true));
        return buildTree(allMenus, 0L);
    }

    /**
     * 获取菜单详情
     *
     * @param id 菜单 ID
     * @return 菜单视图对象
     */
    @Override
    public MenuVO getMenu(Long id) {
        MenuEntity entity = menuManager.getById(id);
        if (entity == null) {
            throw new BusinessException(MenuErrorCode.MENU_NOT_EXISTS);
        }
        return menuConverter.toVO(entity);
    }

    /**
     * 新增菜单
     *
     * @param request 新增菜单请求参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createMenu(MenuCreateRequest request) {
        if (request.getPermission() != null && !request.getPermission().isEmpty()) {
            if (menuManager.existsByPermission(request.getPermission(), null)) {
                throw new BusinessException(MenuErrorCode.MENU_PERMISSION_EXISTS);
            }
        }
        MenuEntity entity = menuConverter.toEntity(request);
        if (entity.getStatus() == null) {
            entity.setStatus(0);
        }
        if (entity.getParentId() == null) {
            entity.setParentId(0L);
        }
        menuManager.save(entity);
        DataTracerUtils.insert(entity.getId(), DataTracerTypeEnum.MENU);
        log.info("新增菜单成功: id={}, name={}", entity.getId(), entity.getName());
    }

    /**
     * 修改菜单
     *
     * @param request 修改菜单请求参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMenu(MenuUpdateRequest request) {
        MenuEntity existing = menuManager.getById(request.getId());
        if (existing == null) {
            throw new BusinessException(MenuErrorCode.MENU_NOT_EXISTS);
        }

        MenuEntity entity = menuConverter.toEntity(request);
        menuManager.updateById(entity);

        // 时光机记录
        MenuEntity updated = menuManager.getById(request.getId());
        DataTracerUtils.update(request.getId(), DataTracerTypeEnum.MENU, existing, updated);

        // 刷新拥有该菜单关联角色的用户 Session
        refreshByMenuId(request.getId());
        log.info("修改菜单成功: id={}", request.getId());
    }

    /**
     * 删除菜单
     * <p>
     * 1. 检查菜单是否存在
     * 2. 检查是否有子菜单
     * 3. 删除关联的角色菜单关系
     * 4. 删除菜单并刷新受影响用户的权限缓存
     *
     * @param id 菜单 ID
     */
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
        DataTracerUtils.delete(id, DataTracerTypeEnum.MENU);

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
                        Collectors.mapping(menuConverter::toTreeVO, Collectors.toList())
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
}
