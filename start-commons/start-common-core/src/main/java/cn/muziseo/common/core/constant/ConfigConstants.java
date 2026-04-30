package cn.muziseo.common.core.constant;

/**
 * 系统参数常量定义
 * <p>
 * 将高频使用的配置键名集中管理，杜绝魔法字符串。
 * 其他微服务通过引入 start-common-core 即可使用。
 * </p>
 *
 * @author 木子软件
 */
public final class ConfigConstants {

    private ConfigConstants() {
        throw new IllegalStateException("Utility class");
    }

    // ================== 通用配置键名 ================== //

    /** 用户初始密码 */
    public static final String USER_DEFAULT_PASSWORD = "sys.user.defaultPassword";

    /** 验证码开关 */
    public static final String CAPTCHA_ENABLED = "sys.captcha.enabled";

    /** 用户注册开关 */
    public static final String REGISTER_ENABLED = "sys.register.enabled";

    /** 文件上传大小限制（MB） */
    public static final String UPLOAD_MAX_SIZE_MB = "sys.upload.maxSizeMB";

    /** 登录失败锁定次数 */
    public static final String LOGIN_MAX_RETRY_COUNT = "sys.login.maxRetryCount";
}
