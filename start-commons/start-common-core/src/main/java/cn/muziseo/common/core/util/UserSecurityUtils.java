package cn.muziseo.common.core.util;

import cn.muziseo.common.core.constant.SecurityConstants;

/**
 * 用户安全校验工具类
 * 
 * @author 木子软件
 */
public class UserSecurityUtils {

    private UserSecurityUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 是否为超级管理员
     * 
     * @param userId 用户ID
     * @return boolean
     */
    public static boolean isSuperAdmin(Long userId) {
        return userId != null && SecurityConstants.SUPER_ADMIN_ID.equals(userId);
    }
}
