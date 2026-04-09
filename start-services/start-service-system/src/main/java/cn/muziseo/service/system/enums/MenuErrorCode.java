package cn.muziseo.service.system.enums;

import cn.muziseo.common.core.exception.errorCode.IErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 菜单模块错误码（10xxxx）
 *
 * @author 木子软件
 */
@Getter
@AllArgsConstructor
public enum MenuErrorCode implements IErrorCode {

    MENU_NOT_EXISTS(100001, "菜单不存在"),
    MENU_HAS_CHILDREN(100002, "存在子菜单，无法删除"),
    MENU_PERMISSION_EXISTS(100003, "权限标识已存在"),
    ;

    private final int code;
    private final String message;
}
