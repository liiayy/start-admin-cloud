package cn.muziseo.common.core.exception.errorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 系统级错误码（8位区间分配法）
 * <p>
 * 核心基础设施系统 (100) -> 基础设施通用模块 (00)
 */
@Getter
@AllArgsConstructor
public enum SystemErrorCode implements IErrorCode {

    INTERNAL_ERROR(10000001, "系统内部错误"),
    DB_OPERATION_FAILED(10000002, "数据库操作失败"),
    THIRD_SERVICE_ERROR(10000003, "第三方服务调用失败"),
    REDIS_OPERATION_FAILED(10000004, "缓存操作失败"),
    FILE_OPERATION_FAILED(10000005, "文件操作失败"),
    NETWORK_ERROR(10000006, "网络连接异常"),
    ;

    private final int code;
    private final String message;
}
