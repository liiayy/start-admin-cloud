package cn.muziseo.common.core.datatracer;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 数据变更记录业务类型枚举
 */
@Getter
@AllArgsConstructor
public enum DataTracerTypeEnum {

    SYSTEM_CONFIG(1, "系统参数"),
    ROLE(2, "角色管理"),
    USER(3, "用户管理"),
    MENU(4, "菜单管理"),
    DICT(5, "字典管理"),
    DEPT(6, "部门管理"),
    POST(7, "岗位管理"),
    NOTICE(8, "公告管理"),
    OSS_CONFIG(9, "存储配置"),
    DEMO(10, "开发演示");

    private final int value;
    private final String desc;
}
