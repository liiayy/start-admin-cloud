package cn.muziseo.common.web.core.trace;
 
import cn.muziseo.common.core.constant.TraceConstants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.web.servlet.HandlerInterceptor;
 
/**
 * 链路追踪拦截器
 * <p>
 * 从请求头中提取 TraceId 并存入日志上下文。
 *
 * @author 木子软件
 */
public class TraceInterceptor implements HandlerInterceptor {
 
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String traceId = request.getHeader(TraceConstants.TRACE_ID_HEADER);
        // 1. 如果请求头没有（如内网直接调用），则生成一个兜底，保证链路不断
        if (cn.hutool.core.util.StrUtil.isBlank(traceId)) {
            traceId = cn.hutool.core.util.IdUtil.fastSimpleUUID();
        }
        
        // 2. 存入 MDC
        MDC.put(TraceConstants.TRACE_ID, traceId);
        
        // 3. 回显到响应头，方便前端/用户在遇到问题时提供该 ID
        response.setHeader(TraceConstants.TRACE_ID_HEADER, traceId);
        return true;
    }
 
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        MDC.remove(TraceConstants.TRACE_ID);
    }
}
