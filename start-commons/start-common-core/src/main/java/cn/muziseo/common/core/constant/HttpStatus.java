package cn.muziseo.common.core.constant;

/**
 * HTTP 状态码常量定义
 *
 * <p>包含所有标准 HTTP 状态码和常用业务状态码范围:</p>
 * <ul>
 *   <li>1xx - 临时信息</li>
 *   <li>2xx - 成功 (200-299)</li>
 *   <li>3xx - 重定向 (300-399)</li>
 *   <li>4xx - 客户端错误 (400-499)</li>
 *   <li>5xx - 服务器错误 (500-599)</li>
 * </ul>
 *
 * @Author 木子软件
 * @Date 2025/11/7周五 09:15:30
 * @Wechat liiayy
 * @Email 773582348@qq.com
 * @Copyright <a href="https://code.muziseo.cn">木子软件</a>
 */
public final class HttpStatus {

    private HttpStatus() {
        throw new IllegalStateException("Utility class");
    }

    // ================== 1xx Informational ================== //
    public static final int CONTINUE = 100;
    public static final int SWITCHING_PROTOCOLS = 101;

    // ================== 2xx Successful ================== //
    public static final int SUCCESS = 200;
    public static final int CREATED = 201;
    public static final int ACCEPTED = 202;
    public static final int NO_CONTENT = 204;
    public static final int RESET_CONTENT = 205;

    // ================== 3xx Redirection ================== //
    public static final int MULTIPLE_CHOICES = 300;
    public static final int MOVED_PERMANENTLY = 301;
    public static final int FOUND = 302;
    public static final int SEE_OTHER = 303;
    public static final int TEMPORARY_REDIRECT = 307;
    public static final int PERMANENT_REDIRECT = 308;

    // ================== 4xx Client Errors ================== //
    public static final int BAD_REQUEST = 400;
    public static final int UNAUTHORIZED = 401;
    public static final int FORBIDDEN = 403;
    public static final int NOT_FOUND = 404;
    public static final int METHOD_NOT_ALLOWED = 405;
    public static final int NOT_ACCEPTABLE = 406;
    public static final int REQUEST_TIMEOUT = 408;
    public static final int CONFLICT = 409;
    public static final int GONE = 410;
    public static final int PAYLOAD_TOO_LARGE = 413;
    public static final int UNSUPPORTED_MEDIA_TYPE = 415;
    public static final int TOO_MANY_REQUESTS = 429;

    // ================== 5xx Server Errors ================== //
    public static final int INTERNAL_SERVER_ERROR = 500;
    public static final int NOT_IMPLEMENTED = 501;
    public static final int BAD_GATEWAY = 502;
    public static final int SERVICE_UNAVAILABLE = 503;
    public static final int GATEWAY_TIMEOUT = 504;
    public static final int HTTP_VERSION_NOT_SUPPORTED = 505;

    // ================== 实用方法 ================== //

    /**
     * 判断是否为成功状态码 (200-299)
     */
    public static boolean isSuccess(int status) {
        return status >= 200 && status < 300;
    }

    /**
     * 判断是否为服务器错误状态码 (500-599)
     */
    public static boolean isServerError(int status) {
        return status >= 500 && status < 600;
    }

    /**
     * 获取状态码描述信息
     */
    public static String getMessage(int status) {
        return switch (status) {
            case SUCCESS -> "请求成功";
            case CREATED -> "资源创建成功";
            case BAD_REQUEST -> "无效的请求";
            case UNAUTHORIZED -> "未经授权";
            case INTERNAL_SERVER_ERROR -> "服务器内部错误";
            // ...其他状态码的描述
            default -> "未知状态码";
        };
    }
}
