package cn.muziseo.service.demo.module.external.aliyun.config;

import feign.Logger;
import feign.Request;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.TimeUnit;

/**
 * 阿里云系统独立配置
 */
public class AliyunFeignConfig {

    @Bean
    public Logger.Level aliyunLogger() {
        return Logger.Level.FULL;
    }

    @Bean
    public Request.Options aliyunOptions() {
        return new Request.Options(5, TimeUnit.SECONDS, 10, TimeUnit.SECONDS, true);
    }

    /**
     * 阿里云特有的签名验证拦截器
     */
    @Bean
    public RequestInterceptor aliyunAuthInterceptor() {
        return template -> {
            // 模拟签名逻辑
            template.header("X-Aliyun-AccessKey", "LTAI5t9xxxx");
            template.header("X-Aliyun-Signature", "SignatureString...");
        };
    }
}
