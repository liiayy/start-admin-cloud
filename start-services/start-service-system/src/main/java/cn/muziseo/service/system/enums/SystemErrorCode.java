package cn.muziseo.service.system.enums;

import cn.muziseo.common.core.exception.errorCode.IErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 系统配置模块错误码（8xxxx）
 *
 * @author 木子软件
 */
@Getter
@AllArgsConstructor
public enum SystemErrorCode implements IErrorCode {

    CONFIG_NOT_EXISTS(10109001, "系统配置不存在"),
    CONFIG_KEY_EXISTS(10109002, "配置键已存在"),
    CONFIG_BUILTIN_CANNOT_MODIFY(10109003, "系统内置配置不允许修改配置键"),
    CONFIG_BUILTIN_CANNOT_DELETE(10109004, "系统内置配置不允许删除"),
    ;

    private final int code;
    private final String message;
}
