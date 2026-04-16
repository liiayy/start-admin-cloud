package cn.muziseo.service.system.module.permission.service.impl;

import cn.hutool.core.bean.BeanUtil;
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

    @Override
    public List<RoleEntity> getRolesByUserId(Long userId) {
        List<Long> roleIds = userRoleManager.getRoleIdsByUserId(userId);
        if (roleIds.isEmpty()) {
            return List.of();
        }
        return roleManager.listByIds(roleIds);
    }

    @Override
    public PageResponse<RoleVO> pageRole(RolePageRequest request) {
        var page = roleManager.pageRole(request);
        List<RoleVO> voList = page.getRecords().stream()
                .map(this::toRoleVO)
                .collect(Collectors.toList());

        PageResponse<RoleVO> response = new PageResponse<>();
        response.setList(voList);
        response.setTotal(page.getTotalRow());
        return response;
    }

    @Override
    public RoleVO getRole(Long id) {
        RoleEntity entity = roleManager.getById(id);
        if (entity == null) {
            throw new BusinessException(RoleErrorCode.ROLE_NOT_EXISTS);
        }
        return toRoleVO(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addRole(RoleAddRequest request) {
        if (roleManager.existsByCode(request.getCode(), null)) {
            throw new BusinessException(RoleErrorCode.ROLE_CODE_EXISTS);
        }
        RoleEntity entity = BeanUtil.copyProperties(request, RoleEntity.class);
        if (entity.getStatus() == null) {
            entity.setStatus(0);
        }
        if (entity.getType() == null) {
            entity.setType(2);
        }
        roleManager.save(entity);
        log.info("新增角色成功: id={}, code={}", entity.getId(), entity.getCode());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRole(RoleUpdateRequest request) {
        RoleEntity existing = roleManager.getById(request.getId());
        if (existing == null) {
            throw new BusinessException(RoleErrorCode.ROLE_NOT_EXISTS);
        }
        if (roleManager.existsByCode(request.getCode(), request.getId())) {
            throw new BusinessException(RoleErrorCode.ROLE_CODE_EXISTS);
        }
        RoleEntity entity = BeanUtil.copyProperties(request, RoleEntity.class);
        roleManager.updateById(entity);

        // 刷新拥有该角色的用户 Session
        List<Long> userIds = userRoleManager.getUserIdsByRoleId(request.getId());
        saSessionRefreshService.refreshUserSessions(userIds);
        log.info("修改角色成功: id={}, 影响用户数={}", request.getId(), userIds.size());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRole(Long id) {
        RoleEntity existing = roleManager.getById(id);
        if (existing == null) {
            throw new BusinessException(RoleErrorCode.ROLE_NOT_EXISTS);
        }
        // 检查是否有用户关联
        List<Long> userIds = userRoleManager.getUserIdsByRoleId(id);
        if (!userIds.isEmpty()) {
            throw new BusinessException(RoleErrorCode.ROLE_HAS_USERS);
        }
        roleMenuManager.deleteByRoleId(id);
        userRoleManager.deleteByRoleId(id);
        roleManager.removeById(id);

        saSessionRefreshService.refreshUserSessions(userIds);
        log.info("删除角色成功: id={}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Integer status) {
        RoleEntity existing = roleManager.getById(id);
        if (existing == null) {
            throw new BusinessException(RoleErrorCode.ROLE_NOT_EXISTS);
        }
        RoleEntity entity = new RoleEntity();
        entity.setId(id);
        entity.setStatus(status);
        roleManager.updateById(entity);

        // 刷新拥有该角色的用户 Session
        List<Long> userIds = userRoleManager.getUserIdsByRoleId(id);
        saSessionRefreshService.refreshUserSessions(userIds);
        log.info("更新角色状态: id={}, status={}, 影响用户数={}", id, status, userIds.size());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignMenus(Long roleId, List<Long> menuIds) {
        roleMenuManager.deleteByRoleId(roleId);
        if (menuIds != null && !menuIds.isEmpty()) {
            List<RoleMenuEntity> list = menuIds.stream().map(menuId -> RoleMenuEntity.builder()
                    .roleId(roleId)
                    .menuId(menuId)
                    .build()).collect(Collectors.toList());
            roleMenuManager.saveBatch(list);
        }

        // 刷新拥有该角色的所有用户的 Session
        List<Long> userIds = userRoleManager.getUserIdsByRoleId(roleId);
        saSessionRefreshService.refreshUserSessions(userIds);
        log.info("分配角色菜单: roleId={}, 影响用户数={}", roleId, userIds.size());
    }

    private RoleVO toRoleVO(RoleEntity entity) {
        return RoleVO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .code(entity.getCode())
                .sort(entity.getSort())
                .status(entity.getStatus())
                .dataScope(entity.getDataScope())
                .dataScopeDeptIds(entity.getDataScopeDeptIds())
                .type(entity.getType())
                .remark(entity.getRemark())
                .createTime(entity.getCreateTime())
                .build();
    }
}
