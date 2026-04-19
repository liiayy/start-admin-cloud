package cn.muziseo.common.core.constant;

/**
 * 权限安全相关常量
 * 
 * @author 木子软件
 */
public final class SecurityConstants {

    private SecurityConstants() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 超级管理员用户 ID
     */
    public static final Long SUPER_ADMIN_ID = 1L;

    /**
     * 超级管理员角色编码
     */
    public static final String SUPER_ADMIN_ROLE_CODE = "admin";

    /**
     * 所有权限标识
     */
    public static final String ALL_PERMISSION = "*:*:*";
}
