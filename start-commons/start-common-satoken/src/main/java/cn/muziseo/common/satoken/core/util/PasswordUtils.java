package cn.muziseo.common.satoken.core.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码工具类
 * <p>
 * 基于 BCrypt 算法实现密码加密和验证
 *
 * @author 木子软件
 */
public final class PasswordUtils {

    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();

    private PasswordUtils() {
    }

    /**
     * 加密原始密码
     *
     * @param rawPassword 原始密码
     * @return BCrypt 哈希值
     */
    public static String encode(String rawPassword) {
        return ENCODER.encode(rawPassword);
    }

    /**
     * 验证密码是否匹配
     *
     * @param rawPassword     原始密码
     * @param encodedPassword 已加密的密码
     * @return 是否匹配
     */
    public static boolean matches(String rawPassword, String encodedPassword) {
        return ENCODER.matches(rawPassword, encodedPassword);
    }
}
