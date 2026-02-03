package cn.muziseo.common.core.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;

/**
 * 服务异常类
 * <p>
 * 用于业务处理过程中抛出的预期异常
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
public class BusinessException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 7849419426878353093L;

    /**
     * 错误码
     */
    private Integer code = 600;

    /**
     * 错误提示
     */
    private String message = "服务异常,请稍后再试";

    /**
     * 构造方法 - 仅包含错误信息
     *
     * @param message 错误提示
     */
    public BusinessException(String message) {
        super(message);
        this.message = message;
    }

    /**
     * 构造方法 - 包含错误信息和错误码
     *
     * @param message 错误提示
     * @param code    错误码
     */
    public BusinessException(String message, Integer code) {
        super(message);
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
    public BusinessException setMessage(String message) {
        this.message = message;
        return this;
    }
}