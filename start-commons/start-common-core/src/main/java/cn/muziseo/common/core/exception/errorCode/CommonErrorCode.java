package cn.muziseo.common.core.exception.errorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 通用业务错误码（2xxxx）
 * <p>
 * 用于参数校验、数据操作等跨模块的通用业务错误
 *
 * @author 木子软件
 */
@Getter
@AllArgsConstructor
public enum CommonErrorCode implements IErrorCode {

    PARAM_VALIDATION_FAILED(20001, "参数校验失败"),
    DATA_NOT_EXISTS(20002, "数据不存在"),
    DATA_ALREADY_EXISTS(20003, "数据已存在"),
    OPERATION_NOT_ALLOWED(20004, "操作不允许"),
    PERMISSION_DENIED(20005, "权限不足"),
    OPERATION_TOO_FREQUENT(20006, "操作过于频繁，请稍后再试"),
    ;

    private final int code;
    private final String message;
}
