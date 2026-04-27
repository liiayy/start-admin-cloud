package cn.muziseo.common.core.exception;

import cn.muziseo.common.core.exception.errorCode.IErrorCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;

/**
 * 系统异常类
 * <p>
 * 用于业务处理过程中抛出的未预期异常，需要打印堆栈信息进行排查
 *
 * @Author 木子软件
 * @Date 2025/11/6周四 18:16:34
 * @Wechat liiayy
 * @Email 773582348@qq.com
 * @Copyright <a href="https://code.muziseo.cn">木子软件</a>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ServiceException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 7849419426878353094L;

    /**
     * 错误码
     */
    private Integer code = 500;

    /**
     * 错误提示
     */
    private String message = "系统异常,请联系管理员";

    /**
     * 原始错误码枚举
     */
    private IErrorCode errorCode;

    /**
     * 通过错误码枚举构造
     *
     * @param errorCode 错误码枚举
     */
    public ServiceException(IErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
        this.errorCode = errorCode;
    }

    /**
     * 通过错误码枚举 + 自定义错误消息构造
     *
     * @param errorCode 错误码枚举
     * @param message   自定义错误消息
     */
    public ServiceException(IErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
        this.message = message;
        this.errorCode = errorCode;
    }
 
    /**
     * 通过错误码枚举 + 原始异常构造
     *
     * @param errorCode 错误码枚举
     * @param cause     原始异常
     */
    public ServiceException(IErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
        this.errorCode = errorCode;
    }

    /**
     * 构造方法 - 仅包含错误信息
     *
     * @param message 错误提示
     */
    public ServiceException(String message) {
        super(message);
        this.message = message;
    }

    /**
     * 构造方法 - 包含错误信息和错误码
     *
     * @param message 错误提示
     * @param code    错误码
     */
    public ServiceException(String message, Integer code) {
        super(message);
        this.message = message;
        this.code = code;
    }

    /**
     * 构造方法 - 包含错误信息和原始异常
     *
     * @param message 错误提示
     * @param cause   原始异常
     */
    public ServiceException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }

    /**
     * 构造方法 - 包含错误信息、错误码和原始异常
     *
     * @param message 错误提示
     * @param code    错误码
     * @param cause   原始异常
     */
    public ServiceException(String message, Integer code, Throwable cause) {
        super(message, cause);
        this.message = message;
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    /**
     * 设置错误信息
     *
     * @param message 错误提示
     * @return 当前异常实例
     */
    public ServiceException setMessage(String message) {
        this.message = message;
        return this;
    }
}