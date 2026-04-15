package cn.muziseo.common.cache.config;

import cn.muziseo.common.core.constant.ConfigConstants;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Function;

/**
 * 系统参数工具类
 *
 * <p>
 * 提供类型安全的系统参数取值方法，所有方法底层基于
 * {@link ConfigCacheManager} 二级缓存，可放心在高频场景使用。
 * </p>
 *
 * <h3>使用示例：</h3>
 * <pre>{@code
 * // 1. 获取字符串参数
 * String pwd = ConfigUtils.getString(ConfigConstants.USER_DEFAULT_PASSWORD, "123456");
 *
 * // 2. 获取布尔开关
 * boolean captcha = ConfigUtils.getBoolean(ConfigConstants.CAPTCHA_ENABLED, true);
 *
 * // 3. 获取整数参数
 * int maxSize = ConfigUtils.getInt(ConfigConstants.UPLOAD_MAX_SIZE_MB, 10);
 * }</pre>
 *
 * @author 木子软件
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConfigUtils {

    // ================== 兜底回调（由外部设置） ================== //

    /**
     * 全局 RPC 兜底回调
     * <p>
     * 由各微服务在启动时注入（通过 {@link #setRpcFallback}），
     * 默认返回 null（纯缓存模式）。
     * </p>
     */
    private static volatile Function<String, String> globalRpcFallback = k -> null;

    /**
     * 设置全局 RPC 兜底回调
     * <p>
     * 通常在 Spring Boot 启动类或 Configuration 中调用一次：
     * <pre>{@code
     * @PostConstruct
     * public void init() {
     *     ConfigUtils.setRpcFallback(configApi::getValueByKey);
     * }
     * }</pre>
     * </p>
     *
     * @param fallback RPC 回调函数
     */
    public static void setRpcFallback(Function<String, String> fallback) {
        globalRpcFallback = fallback;
    }

    // ================== 字符串取值 ================== //

    /**
     * 获取参数值（字符串）
     *
     * @param configKey 参数键名
     * @return 参数值，参数不存在时返回 null
     */
    public static String getString(String configKey) {
        return ConfigCacheManager.getConfigValue(configKey, globalRpcFallback);
    }

    /**
     * 获取参数值（字符串），带默认值
     *
     * @param configKey    参数键名
     * @param defaultValue 默认值（参数不存在时返回）
     * @return 参数值
     */
    public static String getString(String configKey, String defaultValue) {
        String val = getString(configKey);
        return val != null ? val : defaultValue;
    }

    // ================== 整数取值 ================== //

    /**
     * 获取参数值（Integer），带默认值
     *
     * @param configKey    参数键名
     * @param defaultValue 默认值
     * @return 参数值
     */
    public static Integer getInt(String configKey, Integer defaultValue) {
        String val = getString(configKey);
        if (val == null || val.isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(val.trim());
        } catch (NumberFormatException e) {
            log.warn("[ConfigUtils] 参数值无法转为 Integer: configKey={}, value={}", configKey, val);
            return defaultValue;
        }
    }

    // ================== 长整数取值 ================== //

    /**
     * 获取参数值（Long），带默认值
     *
     * @param configKey    参数键名
     * @param defaultValue 默认值
     * @return 参数值
     */
    public static Long getLong(String configKey, Long defaultValue) {
        String val = getString(configKey);
        if (val == null || val.isEmpty()) {
            return defaultValue;
        }
        try {
            return Long.parseLong(val.trim());
        } catch (NumberFormatException e) {
            log.warn("[ConfigUtils] 参数值无法转为 Long: configKey={}, value={}", configKey, val);
            return defaultValue;
        }
    }

    // ================== 布尔取值 ================== //

    /**
     * 获取参数值（Boolean），带默认值
     * <p>
     * 支持 "true"/"false"、"1"/"0"、"yes"/"no"、"on"/"off"
     * </p>
     *
     * @param configKey    参数键名
     * @param defaultValue 默认值
     * @return 参数值
     */
    public static Boolean getBoolean(String configKey, Boolean defaultValue) {
        String val = getString(configKey);
        if (val == null || val.isEmpty()) {
            return defaultValue;
        }
        val = val.trim().toLowerCase();
        return switch (val) {
            case "true", "1", "yes", "on" -> true;
            case "false", "0", "no", "off" -> false;
            default -> {
                log.warn("[ConfigUtils] 参数值无法转为 Boolean: configKey={}, value={}", configKey, val);
                yield defaultValue;
            }
        };
    }
}
