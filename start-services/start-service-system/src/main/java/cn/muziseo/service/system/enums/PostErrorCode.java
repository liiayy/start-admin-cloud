package cn.muziseo.service.system.enums;

import cn.muziseo.common.core.exception.errorCode.IErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 岗位模块错误码（7xxxx）
 *
 * @author 木子软件
 */
@Getter
@AllArgsConstructor
public enum PostErrorCode implements IErrorCode {

    POST_NOT_EXISTS(10105001, "岗位不存在"),
    POST_CODE_EXISTS(10105002, "岗位编码已存在"),
    POST_HAS_USERS(10105003, "岗位下存在用户，无法删除"),
    ;

    private final int code;
    private final String message;
}
