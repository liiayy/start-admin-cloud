package cn.muziseo.common.web.core.config;

import cn.muziseo.common.core.factory.YmlPropertySourceFactory;
import cn.muziseo.common.web.core.trace.TraceInterceptor;
import jakarta.servlet.Filter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@PropertySource(value = "classpath:common-web.yml", factory = YmlPropertySourceFactory.class)
public class StartWebConfig implements WebMvcConfigurer {
 
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册链路追踪拦截器，优先级设置为最高
        registry.addInterceptor(new TraceInterceptor()).order(Integer.MIN_VALUE);
    }

    public static <T extends Filter> FilterRegistrationBean<T> createFilterBean(T filter, Integer order) {
        FilterRegistrationBean<T> bean = new FilterRegistrationBean<>(filter);
        bean.setOrder(order);
        return bean;
    }
}
