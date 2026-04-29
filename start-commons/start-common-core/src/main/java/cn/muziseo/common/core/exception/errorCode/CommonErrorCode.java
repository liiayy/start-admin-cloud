package cn.muziseo.common.core.exception.errorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 通用业务错误码 (8位区间分配法)
 * <p>
 * 核心基础设施系统 (100) -> 通用业务模块 (01)
 */
@Getter
@AllArgsConstructor
public enum CommonErrorCode implements IErrorCode {

    PARAM_VALIDATION_FAILED(10001001, "参数校验失败"),
    DATA_NOT_EXISTS(10001002, "数据不存在"),
    DATA_ALREADY_EXISTS(10001003, "数据已存在"),
    OPERATION_NOT_ALLOWED(10001004, "操作不允许"),
    PERMISSION_DENIED(10001005, "权限不足"),
    OPERATION_TOO_FREQUENT(10001006, "操作过于频繁，请稍后再试"),
    UNAUTHORIZED(10001007, "未登录或登录已过期"),
    ;

    private final int code;
    private final String message;
}
