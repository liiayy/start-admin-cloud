package cn.muziseo.service.demo.module.external.config;


import cn.muziseo.service.demo.module.external.config.decoder.ExternalResultDecoder;
import cn.muziseo.service.demo.module.external.config.interceptor.ExternalAuthInterceptor;
import feign.Logger;
import feign.Request;
import feign.RequestInterceptor;
import feign.codec.Decoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.TimeUnit;

/**
 * 外部接口 Feign 特殊配置
 */
public class ExternalFeignConfig {

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL; // 开发环境建议全量日志，方便调试
    }

    @Bean
    public Request.Options options() {
        // 连接超时 3s，读取超时 10s
        return new Request.Options(3, TimeUnit.SECONDS, 10, TimeUnit.SECONDS, true);
    }

    @Bean
    public RequestInterceptor externalAuthInterceptor() {
        return new ExternalAuthInterceptor();
    }

    @Bean
    public Decoder feignDecoder(ObjectFactory<HttpMessageConverters> messageConverters) {
        // 传入 Spring 默认的解码器作为委托
        return new ExternalResultDecoder(new ResponseEntityDecoder(new SpringDecoder(messageConverters)));
    }
}
