package cn.muziseo.common.core.exception.errorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 系统级错误码（1xxxx）
 * <p>
 * 用于基础设施、数据库、第三方服务等非业务层面的错误
 *
 * @author 木子软件
 */
@Getter
@AllArgsConstructor
public enum SystemErrorCode implements IErrorCode {

    INTERNAL_ERROR(10001, "系统内部错误"),
    DB_OPERATION_FAILED(10002, "数据库操作失败"),
    THIRD_SERVICE_ERROR(10003, "第三方服务调用失败"),
    REDIS_OPERATION_FAILED(10004, "缓存操作失败"),
    FILE_OPERATION_FAILED(10005, "文件操作失败"),
    NETWORK_ERROR(10006, "网络连接异常"),
    ;

    private final int code;
    private final String message;
}
