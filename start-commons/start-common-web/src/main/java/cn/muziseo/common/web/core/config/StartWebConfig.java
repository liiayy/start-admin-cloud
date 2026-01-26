package cn.muziseo.common.web.core.config;

import cn.muziseo.common.core.factory.YmlPropertySourceFactory;
import jakarta.servlet.Filter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.PropertySource;

@PropertySource(value = "classpath:common-web.yml", factory = YmlPropertySourceFactory.class)
public class StartWebConfig {

    public static <T extends Filter> FilterRegistrationBean<T> createFilterBean(T filter, Integer order) {
        FilterRegistrationBean<T> bean = new FilterRegistrationBean<>(filter);
        bean.setOrder(order);
        return bean;
    }
}
