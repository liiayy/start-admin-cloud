package cn.muziseo.common.core.utils.servlet;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.JakartaServletUtil;
import cn.hutool.http.HttpStatus;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.Map;
import java.util.function.Function;

/**
 * 客户端工具类，提供获取请求参数、响应处理、头部信息等常用操作
 *
 * @author ruoyi
 * @author 李彦军
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ServletUtils extends JakartaServletUtil {

    /* ------------------------- 常量定义 ------------------------- */

    // 常用请求头
    private static final String HEADER_USER_AGENT = "User-Agent";
    private static final String HEADER_X_FORWARDED_FOR = "X-Forwarded-For";
    private static final String HEADER_X_REAL_IP = "X-Real-IP";
    private static final String HEADER_PROXY_CLIENT_IP = "Proxy-Client-IP";
    private static final String HEADER_WL_PROXY_CLIENT_IP = "WL-Proxy-Client-IP";
    private static final String HEADER_HTTP_CLIENT_IP = "HTTP_CLIENT_IP";
    private static final String HEADER_HTTP_X_FORWARDED_FOR = "HTTP_X_FORWARDED_FOR";


    // 内容类型
    private static final String CONTENT_TYPE_JSON = MediaType.APPLICATION_JSON_VALUE;
    private static final String CONTENT_TYPE_MULTIPART = MediaType.MULTIPART_FORM_DATA_VALUE;

    /* ------------------------- 请求参数获取 ------------------------- */

    /**
     * 获取指定名称的 String 类型的请求参数
     *
     * @param name 参数名
     * @return 参数值，不存在则返回 null
     */
    public static String getParameter(String name) {
        HttpServletRequest request = getRequest();
        return request != null ? request.getParameter(name) : null;
    }

    /**
     * 获取指定名称的请求参数，支持默认值
     *
     * @param name         参数名
     * @param defaultValue 默认值
     * @return 参数值或默认值
     */
    public static String getParameter(String name, String defaultValue) {
        String value = getParameter(name);
        return value != null ? value : defaultValue;
    }

    /**
     * 获取指定名称的 Integer 类型的请求参数
     *
     * @param name 参数名
     * @return 参数值，不存在则返回 null
     */
    public static Integer getParameterToInt(String name) {
        return getParameter(name, Convert::toInt);
    }

    /**
     * 获取指定名称的 Integer 类型的请求参数，支持默认值
     *
     * @param name         参数名
     * @param defaultValue 默认值
     * @return 参数值或默认值
     */
    public static Integer getParameterToInt(String name, Integer defaultValue) {
        Integer value = getParameterToInt(name);
        return value != null ? value : defaultValue;
    }

    /**
     * 获取指定名称的 Long 类型的请求参数
     *
     * @param name 参数名
     * @return 参数值，不存在则返回 null
     */
    public static Long getParameterToLong(String name) {
        return getParameter(name, Convert::toLong);
    }

    /**
     * 获取指定名称的 Long 类型的请求参数，支持默认值
     *
     * @param name         参数名
     * @param defaultValue 默认值
     * @return 参数值或默认值
     */
    public static Long getParameterToLong(String name, Long defaultValue) {
        Long value = getParameterToLong(name);
        return value != null ? value : defaultValue;
    }

    /**
     * 获取指定名称的 Boolean 类型的请求参数
     *
     * @param name 参数名
     * @return 参数值，不存在则返回 null
     */
    public static Boolean getParameterToBool(String name) {
        return getParameter(name, Convert::toBool);
    }

    /**
     * 获取指定名称的 Boolean 类型的请求参数，支持默认值
     *
     * @param name         参数名
     * @param defaultValue 默认值
     * @return 参数值或默认值
     */
    public static Boolean getParameterToBool(String name, Boolean defaultValue) {
        Boolean value = getParameterToBool(name);
        return value != null ? value : defaultValue;
    }

    /**
     * 获取指定名称的 Double 类型的请求参数
     *
     * @param name 参数名
     * @return 参数值，不存在则返回 null
     */
    public static Double getParameterToDouble(String name) {
        return getParameter(name, Convert::toDouble);
    }

    /**
     * 获取指定名称的 Date 类型的请求参数
     *
     * @param name 参数名
     * @return 参数值，不存在则返回 null
     */
    public static Date getParameterToDate(String name) {
        return getParameter(name, Convert::toDate);
    }

    /**
     * 获取指定名称的请求参数，并转换为指定类型
     *
     * @param name     参数名
     * @param function 转换函数
     * @param <T>      目标类型
     * @return 转换后的值，不存在则返回 null
     */
    public static <T> T getParameter(String name, Function<String, T> function) {
        String value = getParameter(name);
        return value != null ? function.apply(value) : null;
    }

    /**
     * 获取指定名称的请求参数数组
     *
     * @param name 参数名
     * @return 参数值数组，不存在则返回空数组
     */
    public static String[] getParameterValues(String name) {
        HttpServletRequest request = getRequest();
        return request != null ? request.getParameterValues(name) : new String[0];
    }

    /**
     * 获取所有请求参数（以 Map 的形式返回）
     *
     * @param request 请求对象
     * @return 请求参数的 Map，键为参数名，值为参数值数组
     */
    public static Map<String, String[]> getParams(ServletRequest request) {
        if (request == null) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(request.getParameterMap());
    }

    /**
     * @param request 请求
     * @return ua
     */
    public static String getUserAgent(HttpServletRequest request) {
        String ua = request.getHeader("User-Agent");
        return ua != null ? ua : "";
    }


    public static boolean isJsonRequest(ServletRequest request) {
        return StrUtil.startWithIgnoreCase(request.getContentType(), MediaType.APPLICATION_JSON_VALUE);
    }



    /* ------------------------- 请求对象获取 ------------------------- */

    /**
     * 获取当前 HTTP 请求对象
     *
     * @return 当前 HTTP 请求对象，可能为 null
     */
    public static HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }

    /**
     * 获取当前 HTTP 响应对象
     *
     * @return 当前 HTTP 响应对象，可能为 null
     */
    public static HttpServletResponse getResponse() {
        ServletRequestAttributes attributes = getRequestAttributes();
        return attributes != null ? attributes.getResponse() : null;
    }

    /**
     * 获取当前请求的 HttpSession 对象（不创建新会话）
     *
     * @return HttpSession 对象，可能为 null
     */
    public static HttpSession getSession() {
        HttpServletRequest request = getRequest();
        return request != null ? request.getSession(false) : null;
    }

    /**
     * 获取或创建当前请求的 HttpSession 对象
     *
     * @return HttpSession 对象
     */
    public static HttpSession getSession(boolean create) {
        HttpServletRequest request = getRequest();
        return request != null ? request.getSession(create) : null;
    }

    /**
     * 获取当前请求的请求属性
     *
     * @return ServletRequestAttributes 请求属性对象，可能为 null
     */
    public static ServletRequestAttributes getRequestAttributes() {
        try {
            RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
            return attributes instanceof ServletRequestAttributes ? (ServletRequestAttributes) attributes : null;
        } catch (Exception e) {
            log.warn("获取请求属性失败", e);
            return null;
        }
    }

    /* ------------------------- 请求头操作 ------------------------- */

    /**
     * 获取指定请求头的值，如果头部为空则返回空字符串
     *
     * @param request 请求对象
     * @param name    头部名称
     * @return 头部值
     */
    public static String getHeader(HttpServletRequest request, String name) {
        if (request == null || name == null) {
            return StringUtils.EMPTY;
        }
        String value = request.getHeader(name);
        return StringUtils.isNotEmpty(value) ? urlDecode(value) : StringUtils.EMPTY;
    }

    /**
     * 获取指定请求头的值
     *
     * @param name 头部名称
     * @return 头部值
     */
    public static String getHeader(String name) {
        return getHeader(getRequest(), name);
    }

    /**
     * 获取所有请求头的 Map
     *
     * @param request 请求对象
     * @return 请求头的 Map
     */
    public static Map<String, String> getHeaders(HttpServletRequest request) {
        Map<String, String> map = new LinkedCaseInsensitiveMap<>();
        if (request != null) {
            Enumeration<String> enumeration = request.getHeaderNames();
            while (enumeration != null && enumeration.hasMoreElements()) {
                String key = enumeration.nextElement();
                String value = request.getHeader(key);
                map.put(key, value);
            }
        }
        return map;
    }

    /**
     * 获取 User-Agent
     *
     * @return User-Agent 字符串
     */
    public static String getUserAgent() {
        return getHeader(HEADER_USER_AGENT);
    }

    /* ------------------------- 响应处理 ------------------------- */

    /**
     * 将字符串渲染到客户端（以 JSON 格式返回）
     *
     * @param response 响应对象
     * @param string   待渲染的字符串
     */
    public static void renderString(HttpServletResponse response, String string) {
        renderString(response, string, HttpStatus.HTTP_OK);
    }

    /**
     * 将字符串渲染到客户端（以 JSON 格式返回）
     *
     * @param response 响应对象
     * @param string   待渲染的字符串
     * @param status   HTTP 状态码
     */
    public static void renderString(HttpServletResponse response, String string, int status) {
        if (response == null || response.isCommitted()) {
            return;
        }

        try {
            response.setStatus(status);
            response.setContentType(CONTENT_TYPE_JSON);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-Control", "no-cache, no-store");
            response.setDateHeader("Expires", 0);

            PrintWriter writer = response.getWriter();
            writer.print(string);
            writer.flush();
        } catch (IOException e) {
            log.error("渲染响应内容失败", e);
        }
    }

    /**
     * 设置响应头为 JSON 格式
     *
     * @param response 响应对象
     */
    public static void setJsonResponse(HttpServletResponse response) {
        if (response != null) {
            response.setContentType(CONTENT_TYPE_JSON);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        }
    }

    /**
     * 重定向到指定 URL
     *
     * @param response 响应对象
     * @param url      目标 URL
     */
    public static void redirect(HttpServletResponse response, String url) {
        if (response != null && StringUtils.isNotBlank(url)) {
            try {
                response.sendRedirect(url);
            } catch (IOException e) {
                log.error("重定向失败", e);
            }
        }
    }

    /* ------------------------- 请求判断 ------------------------- */


    /**
     * 判断请求是否为 GET 方法
     *
     * @param request 请求对象
     * @return 是否为 GET 请求
     */
    public static boolean isGetRequest(HttpServletRequest request) {
        return request != null && "GET".equalsIgnoreCase(request.getMethod());
    }

    /**
     * 判断请求是否为 POST 方法
     *
     * @param request 请求对象
     * @return 是否为 POST 请求
     */
    public static boolean isPostRequest(HttpServletRequest request) {
        return request != null && "POST".equalsIgnoreCase(request.getMethod());
    }

    /**
     * 判断请求是否为 PUT 方法
     *
     * @param request 请求对象
     * @return 是否为 PUT 请求
     */
    public static boolean isPutRequest(HttpServletRequest request) {
        return request != null && "PUT".equalsIgnoreCase(request.getMethod());
    }

    /**
     * 判断请求是否为 DELETE 方法
     *
     * @param request 请求对象
     * @return 是否为 DELETE 请求
     */
    public static boolean isDeleteRequest(HttpServletRequest request) {
        return request != null && "DELETE".equalsIgnoreCase(request.getMethod());
    }

    /**
     * 判断请求是否为 multipart/form-data 类型（文件上传）
     *
     * @param request 请求对象
     * @return 是否为文件上传请求
     */
    public static boolean isMultipartRequest(HttpServletRequest request) {
        if (request == null) {
            return false;
        }
        String contentType = request.getContentType();
        return contentType != null && contentType.toLowerCase().startsWith(CONTENT_TYPE_MULTIPART);
    }

    /* ------------------------- IP 地址处理 ------------------------- */

    /**
     * 获取客户端真实 IP 地址（支持代理）
     *
     * @param request 请求对象
     * @return 客户端真实 IP
     */
    public static String getClientIP(HttpServletRequest request) {
        if (request == null) {
            return StringUtils.EMPTY;
        }

        // 检查各种代理头部 (安全加固：优先使用网关重写的头部)
        String[] headerNames = {
                HEADER_X_REAL_IP,       // 优先使用 X-Real-IP，通常由 Nginx/Gateway 重写
                HEADER_X_FORWARDED_FOR,
                HEADER_PROXY_CLIENT_IP,
                HEADER_WL_PROXY_CLIENT_IP,
                HEADER_HTTP_CLIENT_IP,
                HEADER_HTTP_X_FORWARDED_FOR
        };

        for (String header : headerNames) {
            String ip = request.getHeader(header);
            if (StringUtils.isNotEmpty(ip) && !StrUtil.equalsIgnoreCase("unknown", ip)) {
                // 对于 X-Forwarded-For，取第一个 IP（最接近真实客户端的 IP）
                if (HEADER_X_FORWARDED_FOR.equalsIgnoreCase(header)) {
                    int index = ip.indexOf(',');
                    return (index != -1) ? ip.substring(0, index).trim() : ip;
                }
                return ip;
            }
        }

        // 如果没有代理头部，使用远程地址
        return request.getRemoteAddr();
    }

    /**
     * 获取当前请求的客户端 IP
     *
     * @return 客户端 IP
     */
    public static String getClientIP() {
        return getClientIP(getRequest());
    }

    /* ------------------------- 编码解码 ------------------------- */

    /**
     * 对内容进行 URL 编码
     *
     * @param str 内容
     * @return 编码后的内容
     */
    public static String urlEncode(String str) {
        try {
            return str != null ? URLEncoder.encode(str, StandardCharsets.UTF_8) : null;
        } catch (Exception e) {
            log.warn("URL 编码失败", e);
            return str;
        }
    }

    /**
     * 对内容进行 URL 解码
     *
     * @param str 内容
     * @return 解码后的内容
     */
    public static String urlDecode(String str) {
        try {
            return str != null ? URLDecoder.decode(str, StandardCharsets.UTF_8) : null;
        } catch (Exception e) {
            log.warn("URL 解码失败", e);
            return str;
        }
    }

    /* ------------------------- 其他工具方法 ------------------------- */

    /**
     * 获取请求完整 URL
     *
     * @param request 请求对象
     * @return 完整 URL
     */
    public static String getFullUrl(HttpServletRequest request) {
        if (request == null) {
            return StringUtils.EMPTY;
        }
        StringBuffer url = request.getRequestURL();
        String queryString = request.getQueryString();
        if (StringUtils.isNotEmpty(queryString)) {
            url.append('?').append(queryString);
        }
        return url.toString();
    }

    /**
     * 获取请求的 Base URL（协议+域名+端口）
     *
     * @param request 请求对象
     * @return Base URL
     */
    public static String getBaseUrl(HttpServletRequest request) {
        if (request == null) {
            return StringUtils.EMPTY;
        }
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();

        StringBuilder url = new StringBuilder();
        url.append(scheme).append("://").append(serverName);

        // 如果是标准端口，省略端口号
        if (!(("http".equals(scheme) && serverPort == 80) ||
                ("https".equals(scheme) && serverPort == 443))) {
            url.append(':').append(serverPort);
        }

        return url.toString();
    }

    /**
     * 获取请求的相对路径（不包含上下文路径）
     *
     * @param request 请求对象
     * @return 相对路径
     */
    public static String getRequestPath(HttpServletRequest request) {
        if (request == null) {
            return StringUtils.EMPTY;
        }
        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();
        if (StringUtils.isNotEmpty(contextPath) && uri.startsWith(contextPath)) {
            return uri.substring(contextPath.length());
        }
        return uri;
    }

}
