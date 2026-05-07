package cn.muziseo.service.system.module.auth.service;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import cn.muziseo.common.core.constant.SaSessionConstants;
import cn.muziseo.service.system.module.auth.repository.entity.UserEntity;
import cn.muziseo.service.system.module.permission.controller.vo.MenuVO;
import cn.muziseo.service.system.module.permission.repository.entity.RoleEntity;
import cn.muziseo.service.system.module.permission.service.MenuService;
import cn.muziseo.service.system.module.permission.service.RoleService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Sa-Token Session 权限刷新服务
 * <p>
 * 登录时写入、权限变更时刷新用户 Session 中的角色和权限数据。
 * 所有微服务共享 Redis，读取端通过 start-common-satoken-integration 模块获取。
 *
 * @author 木子软件
 */
@Service
@Slf4j
public class SaSessionRefreshService {

    @Resource
    private RoleService roleService;

    @Resource
    private MenuService menuService;

    @Resource
    @Lazy
    private UserService userService;

    /**
     * 刷新指定用户的 Session 权限数据
     * <p>
     * 重新查询角色和权限，写入 SaSession。
     * 如果用户未登录（无 Session），则跳过。
     *
     * @param userId 用户ID
     */
    public void refreshUserSession(Long userId) {
        SaSession session = StpUtil.getSessionByLoginId(userId, false);
        if (session == null) {
            log.debug("用户未登录，跳过 Session 刷新: userId={}", userId);
            return;
        }
        writeSession(userId, session);
        log.info("刷新用户 Session 权限: userId={}", userId);
    }

    /**
     * 批量刷新多个用户的 Session 权限数据
     *
     * @param userIds 用户ID列表
     */
    public void refreshUserSessions(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return;
        }
        for (Long userId : userIds) {
            refreshUserSession(userId);
        }
    }

    /**
     * 刷新拥有指定角色的所有用户的 Session
     *
     * @param roleId 角色ID
     */
    public void refreshByRoleId(Long roleId) {
        // 通过 UserRoleManager 查询，这里需要间接获取
        // 由调用方传入 userIds 更合适，避免此处依赖 Manager
    }

    /**
     * 写入角色和权限到 Session
     */
    private void writeSession(Long userId, SaSession session) {
        List<RoleEntity> roles = roleService.getRolesByUserId(userId);
        Set<String> roleCodes = roles.stream()
                .map(RoleEntity::getCode)
                .collect(Collectors.toSet());

        List<Long> roleIds = roles.stream()
                .map(RoleEntity::getId)
                .collect(Collectors.toList());

        Set<String> permissions = Set.of();
        if (!roleIds.isEmpty()) {
            List<MenuVO> menus = menuService.getMenusByRoleIds(roleIds);
            permissions = menus.stream()
                    .map(MenuVO::getPermission)
                    .filter(p -> p != null && !p.isEmpty())
                    .collect(Collectors.toSet());
        }

        session.set(SaSessionConstants.ROLES, roleCodes);
        session.set(SaSessionConstants.PERMISSIONS, permissions);
        
        // 补存用户信息
        UserEntity user = userService.getUserById(userId);
        if (user != null) {
            // 存入整个对象（供 system 服务内部使用）
            session.set(SaSessionConstants.USER, user);
            // 存入纯文本用户名（供 common 模块跨服务使用）
            session.set(SaSessionConstants.USERNAME, user.getUsername());
        }
    }
}
