package cn.muziseo.common.core.exception;

import cn.muziseo.common.core.exception.errorCode.IErrorCode;
import cn.muziseo.common.core.exception.errorCode.SystemErrorCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 业务异常类
 * <p>
 * 用于业务处理过程中抛出的预期异常，全局异常处理器会捕获并返回友好的错误信息。
 * 推荐使用 {@link IErrorCode} 枚举构造，确保错误码统一管理。
 *
 * @author 木子软件
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BusinessException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 7849419426878353093L;

    /**
     * 错误码
     */
    private Integer code;

    /**
     * 错误提示
     */
    private String message;

    /**
     * 原始错误码枚举
     */
    private IErrorCode errorCode;

    /**
     * 通过错误码枚举构造（推荐）
     *
     * @param errorCode 错误码枚举
     */
    public BusinessException(IErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
        this.errorCode = errorCode;
    }

    /**
     * 通过错误码枚举 + 自定义消息构造
     *
     * @param errorCode 错误码枚举
     * @param message   自定义错误提示
     */
    public BusinessException(IErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
        this.message = message;
        this.errorCode = errorCode;
    }

    /**
     * 通过错误码 + 消息构造（兼容旧代码，不推荐新代码使用）
     *
     * @param message 错误提示
     * @param code    错误码
     */
    public BusinessException(String message, Integer code) {
        super(message);
        this.code = code;
        this.message = message;
    }

    /**
     * 仅消息构造（兼容旧代码，不推荐新代码使用）
     *
     * @param message 错误提示
     */
    public BusinessException(String message) {
        super(message);
        this.code = SystemErrorCode.INTERNAL_ERROR.getCode();
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
