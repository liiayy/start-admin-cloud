package cn.muziseo.service.demo.module.external.config.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;

/**
 * 示例：通用验证拦截器
 */
@Slf4j
public class ExternalAuthInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        log.info("【ExternalAuth】执行验证逻辑，当前URL: {}", template.url());
        // 这里可以执行通用的 Header 注入等
    }
}
