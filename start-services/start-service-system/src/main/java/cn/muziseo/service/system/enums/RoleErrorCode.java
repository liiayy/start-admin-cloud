package cn.muziseo.service.system.enums;

import cn.muziseo.common.core.exception.errorCode.IErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 角色模块错误码（9xxxx）
 *
 * @author 木子软件
 */
@Getter
@AllArgsConstructor
public enum RoleErrorCode implements IErrorCode {

    ROLE_NOT_EXISTS(90001, "角色不存在"),
    ROLE_CODE_EXISTS(90002, "角色编码已存在"),
    ROLE_HAS_USERS(90003, "角色下存在用户，无法删除"),
    ;

    private final int code;
    private final String message;
}
