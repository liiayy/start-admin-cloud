package cn.muziseo.service.system.module.permission.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.muziseo.common.cache.datascope.DataScopeCacheManager;
import cn.muziseo.common.core.exception.BusinessException;
import cn.muziseo.common.db.page.PageResponse;
import cn.muziseo.service.system.enums.RoleErrorCode;
import cn.muziseo.service.system.module.auth.manager.UserRoleManager;
import cn.muziseo.service.system.module.auth.service.SaSessionRefreshService;
import cn.muziseo.service.system.module.permission.controller.request.RoleAddRequest;
import cn.muziseo.service.system.module.permission.controller.request.RolePageRequest;
import cn.muziseo.service.system.module.permission.controller.request.RoleUpdateRequest;
import cn.muziseo.service.system.module.permission.controller.vo.RoleVO;
import cn.muziseo.service.system.module.permission.manager.RoleManager;
import cn.muziseo.service.system.module.permission.manager.RoleMenuManager;
import cn.muziseo.service.system.module.permission.repository.entity.RoleEntity;
import cn.muziseo.service.system.module.permission.repository.entity.RoleMenuEntity;
import cn.muziseo.service.system.module.permission.service.RoleService;
import cn.muziseo.service.system.module.permission.convert.RoleConverter;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色业务实现
 *
 * @author 木子软件
 */
@Service
@Slf4j
public class RoleServiceImpl implements RoleService {

    @Resource
    private RoleManager roleManager;

    @Resource
    private UserRoleManager userRoleManager;

    @Resource
    private RoleMenuManager roleMenuManager;

    @Resource
    private SaSessionRefreshService saSessionRefreshService;

    @Resource
    private RoleConverter roleConverter;

    /**
     * 根据用户 ID 获取所属角色列表
     *
     * @param userId 用户 ID
     * @return 角色实体列表
     */
    @Override
    public List<RoleEntity> getRolesByUserId(Long userId) {
        List<Long> roleIds = userRoleManager.getRoleIdsByUserId(userId);
        if (roleIds.isEmpty()) {
            return List.of();
        }
        return roleManager.listByIds(roleIds);
    }

    /**
     * 分页查询角色列表
     *
     * @param request 角色分页查询请求
     * @return 角色分页结果
     */
    @Override
    public PageResponse<RoleVO> pageRole(RolePageRequest request) {
        var page = roleManager.pageRole(request);
        List<RoleVO> voList = page.getRecords().stream()
                .map(roleConverter::toVO)
                .collect(Collectors.toList());

        PageResponse<RoleVO> response = new PageResponse<>();
        response.setList(voList);
        response.setTotal(page.getTotalRow());
        return response;
    }

    /**
     * 获取角色详情
     *
     * @param id 角色 ID
     * @return 角色视图对象
     */
    @Override
    public RoleVO getRole(Long id) {
        RoleEntity entity = roleManager.getById(id);
        if (entity == null) {
            throw new BusinessException(RoleErrorCode.ROLE_NOT_EXISTS);
        }
        return roleConverter.toVO(entity);
    }

    /**
     * 新增角色
     *
     * @param request 新增角色请求参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addRole(RoleAddRequest request) {
        // 1. 校验代码唯一
        if (roleManager.existsByCode(request.getCode(), null)) {
            throw new BusinessException(RoleErrorCode.ROLE_CODE_EXISTS);
        }

        RoleEntity entity = roleConverter.toEntity(request);
        if (entity.getStatus() == null) {
            entity.setStatus(0);
        }
        roleManager.save(entity);
        log.info("新增角色成功: id={}, code={}", entity.getId(), entity.getCode());
    }

    /**
     * 修改角色
     *
     * @param request 修改角色请求参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRole(RoleUpdateRequest request) {
        RoleEntity role = roleManager.getById(request.getId());
        if (role == null) {
            throw new BusinessException(RoleErrorCode.ROLE_NOT_EXISTS);
        }

        // 1. 校验代码唯一
        if (roleManager.existsByCode(request.getCode(), request.getId())) {
            throw new BusinessException(RoleErrorCode.ROLE_CODE_EXISTS);
        }

        RoleEntity entity = roleConverter.toEntity(request);
        roleManager.updateById(entity);

        // 刷新拥有该角色的用户 Session 和 数据权限缓存
        refreshUserCacheByRoleId(request.getId());

        log.info("更新角色成功: id={}", request.getId());
    }

    /**
     * 删除角色
     * <p>
     * 1. 检查是否有用户关联
     * 2. 清理角色菜单关联关系
     * 3. 删除角色记录
     *
     * @param id 角色 ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRole(Long id) {
        RoleEntity role = roleManager.getById(id);
        if (role == null) {
            throw new BusinessException(RoleErrorCode.ROLE_NOT_EXISTS);
        }

        // 1. 检查是否有用户关联
        if (userRoleManager.countByRoleId(id) > 0) {
            throw new BusinessException(RoleErrorCode.ROLE_HAS_USERS);
        }

        // 2. 清理角色菜单关联
        roleMenuManager.deleteByRoleId(id);

        roleManager.removeById(id);
        log.info("删除角色成功: id={}", id);
    }

    /**
     * 更新角色状态
     *
     * @param id     角色 ID
     * @param status 状态值
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Integer status) {
        RoleEntity role = roleManager.getById(id);
        if (role == null) {
            throw new BusinessException(RoleErrorCode.ROLE_NOT_EXISTS);
        }
        RoleEntity entity = new RoleEntity();
        entity.setId(id);
        entity.setStatus(status);
        roleManager.updateById(entity);

        // 刷新拥有该角色的用户 Session 和 数据权限缓存
        refreshUserCacheByRoleId(id);

        log.info("更新角色状态: id={}, status={}", id, status);
    }

    /**
     * 分配角色菜单权限
     *
     * @param roleId  角色 ID
     * @param menuIds 菜单 ID 列表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignMenus(Long roleId, List<Long> menuIds) {
        RoleEntity role = roleManager.getById(roleId);
        if (role == null) {
            throw new BusinessException(RoleErrorCode.ROLE_NOT_EXISTS);
        }

        // 1. 清理旧关联
        roleMenuManager.deleteByRoleId(roleId);

        // 2. 批量插入新关联
        if (menuIds != null && !menuIds.isEmpty()) {
            List<RoleMenuEntity> entities = menuIds.stream()
                    .map(menuId -> {
                        RoleMenuEntity entity = new RoleMenuEntity();
                        entity.setRoleId(roleId);
                        entity.setMenuId(menuId);
                        return entity;
                    })
                    .collect(Collectors.toList());
            roleMenuManager.saveBatch(entities);
        }

        // 3. 刷新权限缓存
        List<Long> userIds = userRoleManager.getUserIdsByRoleId(roleId);
        if (userIds != null && !userIds.isEmpty()) {
            saSessionRefreshService.refreshUserSessions(userIds);
        }

        log.info("分配角色菜单成功: roleId={}, menuCount={}", roleId, menuIds != null ? menuIds.size() : 0);
    }

    /**
     * 刷新拥有指定角色的所有用户的 Session 和数据权限缓存
     */
    private void refreshUserCacheByRoleId(Long roleId) {
        List<Long> userIds = userRoleManager.getUserIdsByRoleId(roleId);
        if (userIds != null && !userIds.isEmpty()) {
            saSessionRefreshService.refreshUserSessions(userIds);
            userIds.forEach(DataScopeCacheManager::evictCache);
        }
    }
}
