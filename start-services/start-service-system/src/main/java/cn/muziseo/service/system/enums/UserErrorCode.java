package cn.muziseo.service.system.enums;

import cn.muziseo.common.core.exception.errorCode.IErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户模块错误码（3xxxx）
 *
 * @author 木子软件
 */
@Getter
@AllArgsConstructor
public enum UserErrorCode implements IErrorCode {

    USERNAME_EXISTS(30001, "用户名已存在"),
    LOGIN_FAILED(30002, "账号或密码错误"),
    USER_DISABLED(30003, "账号已被停用"),
    USER_NOT_EXISTS(30004, "用户不存在"),
    PHONE_EXISTS(30005, "手机号已存在"),
    EMAIL_EXISTS(30006, "邮箱已存在"),
    OLD_PASSWORD_ERROR(30007, "原密码错误"),
    ;

    private final int code;
    private final String message;
}
