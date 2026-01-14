package cn.muziseo.common.core.domain.dto;

import cn.muziseo.common.core.constant.HttpStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 统一API响应格式
 *
 * <p>标准响应结构：</p>
 * <pre>
 * {
 *   "code": 200,
 *   "msg": "操作成功",
 *   "data": {...}
 * }
 * </pre>
 *
 * @param <T> 数据负载类型
 * @Author 李彦军
 * @Date 2025/11/6周四 18:20:15
 * @Wechat liiayy
 * @Email 773582348@qq.com
 */
@Data
@NoArgsConstructor
@Schema(description = "统一API响应格式")
public class ResponseDTO<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "响应状态码", example = "200")
    private Integer code;

    @Schema(description = "响应消息", example = "操作成功")
    private String msg;

    @Schema(description = "响应数据负载")
    private T data;

    /* ------------------------- 成功响应构建方法 ------------------------- */

    /**
     * 构建成功响应（默认消息）
     */
    public static <T> ResponseDTO<T> success() {
        return of(HttpStatus.SUCCESS, "操作成功", null);
    }

    /**
     * 构建带数据的成功响应
     */
    public static <T> ResponseDTO<T> success(T data) {
        return of(HttpStatus.SUCCESS, "操作成功", data);
    }

    /**
     * 构建自定义消息的成功响应
     */
    public static <T> ResponseDTO<T> success(String message) {
        return of(HttpStatus.SUCCESS, message, null);
    }

    /**
     * 构建完整参数的响应
     */
    public static <T> ResponseDTO<T> success(String message, T data) {
        return of(HttpStatus.SUCCESS, message, data);
    }

    /* ------------------------- 失败响应构建方法 ------------------------- */

    /**
     * 构建失败响应（默认消息）
     */
    public static <T> ResponseDTO<T> fail() {
        return of(HttpStatus.INTERNAL_SERVER_ERROR, "操作失败", null);
    }

    /**
     * 构建带错误消息的失败响应
     */
    public static <T> ResponseDTO<T> fail(String message) {
        return of(HttpStatus.INTERNAL_SERVER_ERROR, message, null);
    }

    /**
     * 构建带状态码的失败响应
     */
    public static <T> ResponseDTO<T> fail(Integer code, String message) {
        return of(code, message, null);
    }

    /**
     * 构建带数据的失败响应
     */
    public static <T> ResponseDTO<T> fail(String message, T data) {
        return of(HttpStatus.INTERNAL_SERVER_ERROR, message, data);
    }

    /* ------------------------- 核心构建方法 ------------------------- */

    /**
     * 通用响应构建方法
     *
     * @param code    状态码
     * @param message 响应消息
     * @param data    数据负载
     * @param <T>     数据类型
     * @return 构建的响应对象
     */
    public static <T> ResponseDTO<T> of(Integer code, String message, T data) {
        ResponseDTO<T> response = new ResponseDTO<>();
        response.setCode(code);
        response.setMsg(message);
        response.setData(data);
        return response;
    }

    /* ------------------------- 业务快捷方法 ------------------------- */


    /**
     * 转换为其他类型的响应（用于数据转换场景）
     */
    public <R> ResponseDTO<R> convert(R newData) {
        return of(this.code, this.msg, newData);
    }


    // 链式调用方法
    public ResponseDTO<T> setCode(Integer code) {
        this.code = code;
        return this;
    }

    public ResponseDTO<T> setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public ResponseDTO<T> setData(T data) {
        this.data = data;
        return this;
    }
}
