package cn.muziseo.common.satoken.integration.service;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

/**
 * 权限加载服务实现（Session 模式）
 * <p>
 * 从 Sa-Token Session 中读取用户的角色和权限。
 * 所有微服务共享同一个 Redis，登录时由 system-service 写入 Session，
 * 其他服务直接从 Session 读取，无需访问数据库或 Feign 调用。
 *
 * @author 木子软件
 */
@Slf4j
public class SaPermissionServiceImpl implements SaPermissionService {

    private static final String SESSION_ROLES_KEY = "roles";
    private static final String SESSION_PERMISSIONS_KEY = "permissions";

    @Override
    public Set<String> getRoleCodes(Long userId) {
        SaSession session = StpUtil.getSessionByLoginId(userId, false);
        if (session == null) {
            return Set.of();
        }
        Set<String> roles = session.getModel(SESSION_ROLES_KEY, Set.class);
        return roles != null ? roles : Set.of();
    }

    @Override
    public Set<String> getPermissionList(Long userId) {
        SaSession session = StpUtil.getSessionByLoginId(userId, false);
        if (session == null) {
            return Set.of();
        }
        Set<String> permissions = session.getModel(SESSION_PERMISSIONS_KEY, Set.class);
        return permissions != null ? permissions : Set.of();
    }
}
