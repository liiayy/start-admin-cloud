package cn.muziseo.common.web.core.handler;

import cn.hutool.core.util.StrUtil;
import cn.muziseo.common.core.constant.HttpStatus;
import cn.muziseo.common.core.domain.dto.ResponseDTO;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Optional;

/**
 * 全局异常处理器
 *
 * @author 木子软件
 * @Date 2026-01-22
 * @Copyright <a href="https://code.muziseo.cn">木子软件</a>
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 处理 SpringMVC 请求参数缺失
     *
     * @param ex 异常对象
     * @return ResponseDTO
     */
    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    public ResponseDTO<?> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        log.warn("[请求参数缺失]", ex);
        return ResponseDTO.fail(HttpStatus.BAD_REQUEST, String.format("请求参数缺失:%s", ex.getParameterName()));
    }

    /**
     * 处理 SpringMVC 请求参数类型错误
     *
     * @param ex 异常对象
     * @return ResponseDTO
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseDTO<?> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        log.warn("[请求参数类型错误]", ex);
        return ResponseDTO.fail(HttpStatus.BAD_REQUEST, String.format("请求参数类型错误:%s", ex.getMessage()));
    }

    /**
     * 处理 SpringMVC 参数校验不正确
     *
     * @param ex 异常对象
     * @return ResponseDTO
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseDTO<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.warn("[参数校验不正确] 路径: {}", ex.getObjectName());
        // 使用 Optional 或直接从 BindingResult 获取第一个错误
        String errorMessage = Optional.ofNullable(ex.getBindingResult().getFieldError())
                .map(FieldError::getDefaultMessage)
                .orElseGet(() -> {
                    // 如果 FieldError 为空，尝试获取 ObjectError（针对类级别的复合校验）
                    return ex.getBindingResult().getAllErrors().stream()
                            .map(ObjectError::getDefaultMessage)
                            .filter(StrUtil::isNotEmpty)
                            .findFirst()
                            .orElse("参数格式错误"); // 最终兜底，防止 getDefaultMessage 返回 null
                });
        return ResponseDTO.fail(HttpStatus.BAD_REQUEST, String.format("参数校验失败: %s", errorMessage));
    }

    /**
     * 处理 SpringMVC 参数绑定异常
     *
     * @param ex 异常对象
     * @return ResponseDTO
     */
    @ExceptionHandler(BindException.class)
    public ResponseDTO<?> handleBindException(BindException ex) {
        log.warn("[参数绑定异常] 发生参数绑定异常", ex);

        // 1. 优先获取字段错误，如果没有则获取全局错误
        ObjectError error = ex.getBindingResult().getFieldError();
        if (error == null) {
            error = ex.getBindingResult().getGlobalError();
        }

        // 2. 提取错误信息，并提供一个最终兜底文案
        String errorMessage = "请求参数格式错误"; // 默认兜底
        if (error != null) {
            String defaultMessage = error.getDefaultMessage();
            if (StrUtil.isNotEmpty(defaultMessage)) {
                errorMessage = defaultMessage;
            }
        }

        return ResponseDTO.fail(HttpStatus.BAD_REQUEST, String.format("请求参数不正确: %s", errorMessage));
    }

    /**
     * 处理 HTTP 消息不可读异常
     *
     * @param ex 异常对象
     * @return ResponseDTO
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseDTO<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        log.warn("[参数解析失败] 详情: {}", ex.getMessage());

        Throwable cause = ex.getCause();

        // 1. 处理 JSON 格式转换错误 (类型不匹配)
        if (cause instanceof InvalidFormatException ife) {
            return ResponseDTO.fail(HttpStatus.BAD_REQUEST, String.format("参数类型错误: 期望类型与输入值 [%s] 不匹配", ife.getValue()));
        }

        // 2. 处理 Body 为空
        if (ex.getMessage() != null && ex.getMessage().contains("Required request body is missing")) {
            return ResponseDTO.fail(HttpStatus.BAD_REQUEST, "请求体 (Body) 不能为空");
        }

        // 3. 处理 JSON 语法错误 (比如少了引号)
        if (cause instanceof com.fasterxml.jackson.core.JsonParseException) {
            return ResponseDTO.fail(HttpStatus.BAD_REQUEST, "JSON 格式语法错误，请检查括号或逗号");
        }

        return ResponseDTO.fail(HttpStatus.BAD_REQUEST, "请求参数格式无法解析");
    }

    /**
     * 处理 Validator 校验不通过产生的异常
     *
     * @param ex 异常对象
     * @return ResponseDTO
     */
    @ExceptionHandler(value = ConstraintViolationException.class)
    public ResponseDTO<?> handleConstraintViolationException(ConstraintViolationException ex) {
        log.warn("[参数校验异常]", ex);
        ConstraintViolation<?> constraintViolation = ex.getConstraintViolations().iterator().next();
        return ResponseDTO.fail(HttpStatus.BAD_REQUEST, String.format("请求参数不正确:%s", constraintViolation.getMessage()));
    }

    /**
     * 处理上传文件过大异常
     *
     * @param ex 异常对象
     * @return ResponseDTO
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseDTO<?> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex) {
        return ResponseDTO.fail(HttpStatus.BAD_REQUEST, "上传文件过大，请调整后重试");
    }

    /**
     * 处理 SpringMVC 请求地址不存在
     *
     * @param req 请求对象
     * @param ex  异常对象
     * @return ResponseDTO
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseDTO<?> handleNoResourceFoundException(HttpServletRequest req, NoResourceFoundException ex) {
        log.warn("[请求地址不存在]", ex);
        return ResponseDTO.fail(HttpStatus.NOT_FOUND, String.format("请求地址不存在:%s", ex.getResourcePath()));
    }

    /**
     * 处理 SpringMVC 请求方法不正确
     *
     * @param ex 异常对象
     * @return ResponseDTO
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseDTO<?> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        log.warn("[请求方法不正确]", ex);
        return ResponseDTO.fail(HttpStatus.METHOD_NOT_ALLOWED, String.format("请求方法不正确:%s", ex.getMessage()));
    }

    /**
     * 处理 SpringMVC 请求的 Content-Type 不正确
     *
     * @param ex 异常对象
     * @return ResponseDTO
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseDTO<?> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException ex) {
        log.warn("[请求类型不正确]", ex);
        return ResponseDTO.fail(HttpStatus.BAD_REQUEST, String.format("请求类型不正确:%s", ex.getMessage()));
    }

    /**
     * 处理系统异常，兜底处理所有的一切
     * 使用 @ExceptionHandler(Throwable.class) 可以捕获包括 Exception 和 Error（如 OutOfMemoryError）在内的所有问题
     *
     * @param req 请求对象
     * @param ex  异常对象
     * @return ResponseDTO
     */
    @ExceptionHandler(value = Throwable.class)
    public ResponseDTO<?> handleDefaultException(HttpServletRequest req, Exception ex) {
        log.error("[系统异常] 请求地址: {}, 异常原因: ", req.getRequestURI(), ex);
        // 在生产环境下，不建议直接把 ex.getMessage() 返回给前端，防止泄露堆栈或数据库结构
        return ResponseDTO.fail(HttpStatus.INTERNAL_SERVER_ERROR, "服务器繁忙，请稍后再试");
    }


}
