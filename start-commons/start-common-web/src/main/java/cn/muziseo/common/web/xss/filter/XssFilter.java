package cn.muziseo.common.web.xss.filter;

import cn.muziseo.common.web.xss.clean.XssCleaner;
import cn.muziseo.common.web.xss.config.properties.XssProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.util.PathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * XSS 过滤器
 *
 * @author 木子软件
 * @Date 2026-01-26
 * @Copyright <a href="https://code.muziseo.cn">木子软件</a>
 */
@AllArgsConstructor
public class XssFilter extends OncePerRequestFilter {
    /**
     * 属性
     */
    private final XssProperties properties;
    /**
     * 路径匹配器
     */
    private final PathMatcher pathMatcher;

    /**
     * XSS 清理器
     */
    private final XssCleaner xssCleaner;

    /**
     * 执行过滤
     *
     * @param request     请求对象
     * @param response    响应对象
     * @param filterChain 过滤器链
     * @throws IOException      IO异常
     * @throws ServletException Servlet异常
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        filterChain.doFilter(new XssRequestWrapper(request, xssCleaner), response);
    }

    /**
     * 判断是否应该过滤
     *
     * @param request 请求对象
     * @return boolean
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // 如果关闭，则不过滤
        if (!properties.getEnable()) {
            return true;
        }

        // 如果匹配到无需过滤，则不过滤
        String uri = request.getRequestURI();
        return properties.getExcludePaths().stream().anyMatch(path -> pathMatcher.match(path, uri));
    }
}
