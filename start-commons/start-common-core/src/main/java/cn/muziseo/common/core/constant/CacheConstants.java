package cn.muziseo.common.core.constant;

/**
 * 缓存常量定义
 * <p>
 * 统一管理 Redis 缓存 Key 前缀和模式，规范命名空间。
 * </p>
 *
 * @author 木子软件
 */
public final class CacheConstants {

    private CacheConstants() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 系统参数缓存前缀
     */
    public static final String CONFIG_PREFIX = "sys:config:";

    /**
     * 字典数据缓存前缀
     */
    public static final String DICT_PREFIX = "sys:dict:";

    /**
     * 数据权限缓存前缀
     */
    public static final String DATA_SCOPE_PREFIX = "sys:data_scope:";

    /**
     * 验证码缓存前缀
     */
    public static final String CAPTCHA_CODE_PREFIX = "sys:auth:captcha:";

    /**
     * 登录失败锁定缓存前缀
     */
    public static final String LOGIN_FAIL_PREFIX = "sys:auth:login_fail:";

    /**
     * WebSocket 在线用户缓存前缀
     */
    public static final String WS_ONLINE_PREFIX = "sys:ws:cluster:";

}
