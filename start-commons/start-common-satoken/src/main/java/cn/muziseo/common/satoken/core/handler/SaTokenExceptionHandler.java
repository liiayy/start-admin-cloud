package cn.muziseo.common.satoken.core.handler;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import cn.muziseo.common.core.domain.dto.ResponseDTO;
import cn.muziseo.common.core.exception.errorCode.CommonErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class SaTokenExceptionHandler {

    /**
     * 权限码异常
     */
    @ExceptionHandler(NotPermissionException.class)
    public ResponseDTO<Void> handleNotPermissionException(NotPermissionException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',权限码校验失败'{}'", requestURI, e.getMessage());
        return ResponseDTO.fail(CommonErrorCode.PERMISSION_DENIED);
    }

    /**
     * 角色权限异常
     */
    @ExceptionHandler(NotRoleException.class)
    public ResponseDTO<Void> handleNotRoleException(NotRoleException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',角色权限校验失败'{}'", requestURI, e.getMessage());
        return ResponseDTO.fail(CommonErrorCode.PERMISSION_DENIED);
    }

    /**
     * 认证失败
     */
    @ExceptionHandler(NotLoginException.class)
    public ResponseDTO<Void> handleNotLoginException(NotLoginException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',认证失败'{}',无法访问系统资源", requestURI, e.getMessage());
        return ResponseDTO.fail(CommonErrorCode.UNAUTHORIZED);
    }
}
