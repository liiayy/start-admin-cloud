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

    /** 系统通用状态（正常/停用） */
    public static final String SYS_STATUS = "sys_status";

    /** 菜单显示状态（显示/隐藏） */
    public static final String SYS_SHOW_HIDE = "sys_show_hide";

    /** 系统开关（是/否） */
    public static final String SYS_YES_NO = "sys_yes_no";

    /** 菜单类型（目录/菜单/按钮） */
    public static final String SYS_MENU_TYPE = "sys_menu_type";
}
