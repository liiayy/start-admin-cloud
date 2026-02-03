package cn.muziseo.service.demo.module.external.weather.config;

import feign.Logger;
import feign.Request;
import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.TimeUnit;

/**
 * 天气系统独立配置
 */
public class WeatherFeignConfig {

    @Value("${external.weather.key:your-key}")
    private String apiKey;

    @Bean
    public Logger.Level weatherLogger() {
        return Logger.Level.BASIC;
    }

    @Bean
    public Request.Options weatherOptions() {
        return new Request.Options(2, TimeUnit.SECONDS, 5, TimeUnit.SECONDS, true);
    }

    /**
     * 天气系统特有的查询参数验证拦截器
     */
    @Bean
    public RequestInterceptor weatherAuthInterceptor() {
        return template -> {
            // 所有天气请求自动加上 key 参数
            template.query("key", apiKey);
        };
    }
}
