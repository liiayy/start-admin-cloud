package cn.muziseo.common.http.config;

import cn.muziseo.common.core.factory.YmlPropertySourceFactory;
import com.dtflys.forest.converter.json.ForestJacksonConverter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

/**
 * HTTP 模块自动配置类
 *
 * @author 木子软件
 */
@AutoConfiguration
@PropertySource(
    value = "classpath:http-client-${spring.profiles.active:dev}.yml",
    factory = YmlPropertySourceFactory.class,
    ignoreResourceNotFound = true
)
public class HttpAutoConfiguration {

    /**
     * 配置 Forest Jackson 转换器
     * 通过自定义 ObjectMapper 增强 JSON 解析的容错性
     */
    @Bean
    public ForestJacksonConverter forestJacksonConverter(ObjectMapper objectMapper) {
        ObjectMapper forestMapper = objectMapper.copy();
        // 容错配置：允许单值作为数组、空数组作为 null、忽略未知属性
        forestMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        forestMapper.enable(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT);
        forestMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return new ForestJacksonConverter(forestMapper);
    }
}
