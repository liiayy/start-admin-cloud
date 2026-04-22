package cn.muziseo.common.core.constant;

/**
 * 字典常量定义
 * <p>
 * 将高频使用的字典类型编码集中管理，杜绝魔法字符串。
 * 其他微服务通过引入 start-common-core 即可使用。
 * </p>
 *
 * @author 木子软件
 */
public final class DictConstants {

    private DictConstants() {
        throw new IllegalStateException("Utility class");
    }

    // ================== Redis 缓存 Key 前缀 ================== //

    /**
     * 字典数据 Redis 缓存 Key 前缀
     * <p>完整 Key 格式：sys:dict:type:{dictType}</p>
     */
    public static final String DICT_CACHE_KEY_PREFIX = "sys:dict:type:";

    // ================== 通用字典类型编码 ================== //

    /** 用户性别 */
    public static final String SYS_USER_SEX = "sys_user_sex";

    /** 系统状态（0正常 1停用） */
    public static final String SYS_STATUS = "sys_status";

    /** 启用状态（0正常 1停用） */
    public static final String SYS_ENABLE_STATUS = "sys_enable_status";

    /** 成功/失败状态（0成功 1失败） */
    public static final String SYS_COMMON_STATUS = "sys_common_status";

    /** 菜单显示状态（显示/隐藏） */
    public static final String SYS_SHOW_HIDE = "sys_show_hide";

    /** 系统开关（是/否） */
    public static final String SYS_YES_NO = "sys_yes_no";

    /** 菜单类型（目录/菜单/按钮） */
    public static final String SYS_MENU_TYPE = "sys_menu_type";

    /** 操作类型 */
    public static final String SYS_OPER_TYPE = "sys_oper_type";

    /** OSS存储平台 */
    public static final String SYS_OSS_SERVICE = "sys_oss_service";

    /** OSS桶权限类型 */
    public static final String SYS_OSS_ACCESS_POLICY = "sys_oss_access_policy";
}
