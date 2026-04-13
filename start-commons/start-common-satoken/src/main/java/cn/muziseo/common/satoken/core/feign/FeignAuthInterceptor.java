package cn.muziseo.common.satoken.core.feign;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Feign 请求拦截器 —— 微服务间调用时透传用户认证上下文
 * <p>
 * 将当前请求的 Authorization 和 X-User-Id 头部传递给下游服务，
 * 使下游服务能够获取到调用者的身份信息。
 */
@Slf4j
public class FeignAuthInterceptor implements RequestInterceptor {

    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String HEADER_USER_ID = "X-User-Id";

    @Override
    public void apply(RequestTemplate template) {
        // 优先从当前 HTTP 请求上下文中获取原始 Authorization 头
        ServletRequestAttributes attributes = getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String authorization = request.getHeader(HEADER_AUTHORIZATION);
            if (StrUtil.isNotBlank(authorization)) {
                template.header(HEADER_AUTHORIZATION, authorization);
            }
        }

        // 透传用户 ID（与网关 AuthFilter 保持一致）
        Object loginId = StpUtil.getLoginIdDefaultNull();
        if (loginId != null) {
            template.header(HEADER_USER_ID, loginId.toString());
        }
    }

    private ServletRequestAttributes getRequestAttributes() {
        try {
            RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
            return attributes instanceof ServletRequestAttributes sra ? sra : null;
        } catch (Exception e) {
            log.warn("Feign 透传认证上下文时获取请求属性失败", e);
            return null;
        }
    }
}
