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

    USERNAME_EXISTS(10101001, "用户名已存在"),
    LOGIN_FAILED(10101002, "账号或密码错误"),
    USER_DISABLED(10101003, "账号已被停用"),
    USER_NOT_EXISTS(10101004, "用户不存在"),
    PHONE_EXISTS(10101005, "手机号已存在"),
    EMAIL_EXISTS(10101006, "邮箱已存在"),
    OLD_PASSWORD_ERROR(10101007, "原密码错误"),
    SUPER_ADMIN_PROTECTED(10101008, "超级管理员受到保护，禁止该操作"),
    CAPTCHA_EMPTY(10101009, "验证码不能为空"),
    CAPTCHA_EXPIRED(10101010, "验证码已过期"),
    CAPTCHA_ERROR(10101011, "验证码错误"),
    SOCIAL_USER_NOT_BOUND(10101012, "社交账号未绑定系统用户"),
    SOCIAL_USER_ALREADY_BOUND(10101013, "该社交账号已被其他用户绑定"),
    ;


    private final int code;
    private final String message;
}
