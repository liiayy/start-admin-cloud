package cn.muziseo.common.core.event;

import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 错误日志事件
 */
@Getter
@Setter
public class ErrorLogEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 异常类名 */
    private String errorType;
    /** 错误消息 */
    private String errorMessage;
    /** 异常堆栈 */
    private String errorStack;
    /** 请求URI */
    private String requestUri;
    /** 请求方法 */
    private String requestMethod;
    /** 请求参数 */
    private String requestParams;
    /** 请求IP */
    private String requestIp;
    /** User-Agent */
    private String userAgent;
    /** 用户ID */
    private Long userId;
    /** 用户名 */
    private String userName;
    /** 模块名称 */
    private String moduleName;
    /** 追踪ID */
    private String traceId;
    /** 服务器名称 */
    private String serverName;
    /** 服务器IP */
    private String serverIp;
    /** 发生时间 */
    private LocalDateTime createTime;
}
