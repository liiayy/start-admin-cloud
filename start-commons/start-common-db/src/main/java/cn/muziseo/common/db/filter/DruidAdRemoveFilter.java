package cn.muziseo.common.db.filter;

import com.alibaba.druid.util.Utils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Druid 底部广告过滤器
 * <p>
 * 移除 Druid 监控页面底部的广告信息，通过重写 common.js 文件内容实现
 * 继承 Spring 的 OncePerRequestFilter 确保每个请求只过滤一次
 * </p>
 *
 * @author 木子软件
 * @Date 2026-01-15
 * @Wechat liiayy
 * @Email 773582348@qq.com
 * @Copyright <a href="https://code.muziseo.cn">木子软件</a>
 */
public class DruidAdRemoveFilter extends OncePerRequestFilter {

    /**
     * common.js 的路径
     */
    private static final String COMMON_JS_FILE_PATH = "support/http/resources/js/common.js";

    /**
     * 过滤 Druid 监控页面的底部广告
     * <p>
     * 通过重写 common.js 文件内容，移除底部的 banner 和 powered by 广告信息
     * </p>
     *
     * @param request  HTTP 请求
     * @param response HTTP 响应
     * @param chain    过滤器链
     * @throws ServletException Servlet 异常
     * @throws IOException      IO 异常
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        chain.doFilter(request, response);
        // 重置缓冲区，响应头不会被重置
        response.resetBuffer();
        // 获取 common.js
        String text = Utils.readFromResource(COMMON_JS_FILE_PATH);
        // 正则替换 banner, 除去底部的广告信息
        text = text.replaceAll("<a.*?banner\"></a><br/>", "");
        text = text.replaceAll("powered.*?shrek.wang</a>", "");
        response.getWriter().write(text);
    }
}
