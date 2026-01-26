package cn.muziseo.common.web.xss.filter;

import cn.muziseo.common.web.xss.clean.XssCleaner;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * XSS 请求包装器
 * <p>
 * 对请求参数进行 XSS 过滤
 *
 * @author 木子软件
 * @Date 2026-01-26
 * @Copyright <a href="https://code.muziseo.cn">木子软件</a>
 */
public class XssRequestWrapper extends HttpServletRequestWrapper {

    private final XssCleaner xssCleaner;

    /**
     * 构造方法
     *
     * @param request    请求对象
     * @param xssCleaner XSS 清理器
     */
    public XssRequestWrapper(HttpServletRequest request, XssCleaner xssCleaner) {
        super(request);
        this.xssCleaner = xssCleaner;
    }

    // ============================ parameter ============================

    /**
     * 获取参数映射
     *
     * @return Map<String, String[]>
     */
    @Override
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> map = new LinkedHashMap<>();
        Map<String, String[]> parameters = super.getParameterMap();
        for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
            String[] values = entry.getValue();
            for (int i = 0; i < values.length; i++) {
                values[i] = xssCleaner.clean(values[i]);
            }
            map.put(entry.getKey(), values);
        }
        return map;
    }

    /**
     * 获取参数值数组
     *
     * @param name 参数名
     * @return String[]
     */
    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        if (values == null) {
            return null;
        }
        int count = values.length;
        String[] encodedValues = new String[count];
        for (int i = 0; i < count; i++) {
            encodedValues[i] = xssCleaner.clean(values[i]);
        }
        return encodedValues;
    }

    /**
     * 获取参数值
     *
     * @param name 参数名
     * @return String
     */
    @Override
    public String getParameter(String name) {
        String value = super.getParameter(name);
        if (value == null) {
            return null;
        }
        return xssCleaner.clean(value);
    }

    // ============================ attribute ============================

    /**
     * 获取属性
     *
     * @param name 属性名
     * @return Object
     */
    @Override
    public Object getAttribute(String name) {
        Object value = super.getAttribute(name);
        if (value instanceof String) {
            return xssCleaner.clean((String) value);
        }
        return value;
    }

    // ============================ header ============================

    /**
     * 获取头信息
     *
     * @param name 头名称
     * @return String
     */
    @Override
    public String getHeader(String name) {
        String value = super.getHeader(name);
        if (value == null) {
            return null;
        }
        return xssCleaner.clean(value);
    }

    // ============================ queryString ============================

    /**
     * 获取查询字符串
     *
     * @return String
     */
    @Override
    public String getQueryString() {
        String value = super.getQueryString();
        if (value == null) {
            return null;
        }
        return xssCleaner.clean(value);
    }

}
