package cn.muziseo.common.web.xss.config;

import cn.muziseo.common.core.enums.WebFilterOrderEnum;
import cn.muziseo.common.web.xss.clean.JsoupXssCleaner;
import cn.muziseo.common.web.xss.clean.XssCleaner;
import cn.muziseo.common.web.xss.config.properties.XssProperties;
import cn.muziseo.common.web.xss.filter.XssFilter;
import cn.muziseo.common.web.xss.json.XssStringJsonDeserializer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.util.PathMatcher;

import static cn.muziseo.common.web.core.config.StartWebConfig.createFilterBean;

/**
 * XSS 自动配置类
 *
 * @author 木子软件
 * @Date 2026-01-26
 * @Copyright <a href="https://code.muziseo.cn">木子软件</a>
 */
@AutoConfiguration
@EnableConfigurationProperties(XssProperties.class)
public class StartXssAutoConfiguration {

    /**
     * 创建 XssCleaner Bean
     * <p>
     * 如果没有自定义的 XssCleaner 则使用默认的 JsoupXssCleaner
     *
     * @return XssCleaner
     */
    @Bean
    @ConditionalOnMissingBean(XssCleaner.class)
    public XssCleaner xssCleaner() {
        return new JsoupXssCleaner();
    }

    /**
     * 注册 Jackson 的序列化器，用于处理 json 类型参数的 xss 过滤
     * <p>
     * <a href="https://code.muziseo.cn/archives/wei-ming-ming-wen-zhang-dYUhovoZ">ConditionalOnMissingBean条件注解</a>
     * <a href="https://code.muziseo.cn/archives/conditionalonpropertytiao-jian-zhu-jie">ConditionalOnProperty条件注解</a>
     * <a href="https://spring-doc.muziseo.cn/spring-boot/reference/using/auto-configuration.html">参考文档</a>
     *
     * @param properties  XSS 配置属性
     * @param pathMatcher 路径匹配器
     * @param xssCleaner  XSS 清理器
     * @return Jackson2ObjectMapperBuilderCustomizer
     */
    @Bean
    @ConditionalOnMissingBean(name = "xssJacksonCustomizer")
    @ConditionalOnProperty(value = "start.xss.enable", havingValue = "true")
    public Jackson2ObjectMapperBuilderCustomizer xssJacksonCustomizer(XssProperties properties,
                                                                      PathMatcher pathMatcher,
                                                                      XssCleaner xssCleaner) {
        // 在反序列化时进行 xss 过滤，可以替换使用 XssStringJsonSerializer，在序列化时进行处理
        return builder ->
                builder.deserializerByType(String.class, new XssStringJsonDeserializer(properties, pathMatcher, xssCleaner));
    }

    /**
     * 创建 XssFilter Bean，解决 Xss 安全问题
     * <p>
     * <a href="https://code.muziseo.cn/archives/conditionalonbeantiao-jian-zhu-jie">ConditionalOnBean条件注解</a>
     *
     * @param properties  XSS 配置属性
     * @param pathMatcher 路径匹配器
     * @param xssCleaner  XSS 清理器
     * @return FilterRegistrationBean<XssFilter>
     */
    @Bean
    @ConditionalOnBean(XssCleaner.class)
    public FilterRegistrationBean<XssFilter> xssFilter(XssProperties properties, PathMatcher pathMatcher, XssCleaner xssCleaner) {
        return createFilterBean(new XssFilter(properties, pathMatcher, xssCleaner), WebFilterOrderEnum.XSS_FILTER);
    }
}
