package cn.muziseo.common.satoken.core.config;

import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.jwt.StpLogicJwtForSimple;
import cn.dev33.satoken.stp.StpLogic;
import cn.muziseo.common.core.factory.YmlPropertySourceFactory;
import cn.muziseo.common.satoken.core.dao.StartSaTokenDao;
import cn.muziseo.common.satoken.core.feign.FeignAuthInterceptor;
import cn.muziseo.common.satoken.core.handler.SaTokenExceptionHandler;
import feign.RequestInterceptor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Sa-Token 基础配置
 * <p>
 * 提供 JWT、TokenDao、异常处理等基础能力。
 * 权限加载（StpInterface）由 start-common-satoken-integration 模块提供。
 */
@AutoConfiguration
@PropertySource(value = "classpath:common-satoken.yml", factory = YmlPropertySourceFactory.class)
public class SaTokenConfiguration implements WebMvcConfigurer {

    @Bean
    public StpLogic getStpLogicJwt() {
        return new cn.dev33.satoken.jwt.StpLogicJwtForMixin();
    }

    @Bean
    public SaTokenDao saTokenDao() {
        return new StartSaTokenDao();
    }

    @Bean
    public SaTokenExceptionHandler saTokenExceptionHandler() {
        return new SaTokenExceptionHandler();
    }

    /**
     * Feign 拦截器：微服务间调用时透传认证上下文
     * <p>
     * 仅当 classpath 中存在 Feign 时生效（即引入了 spring-cloud-starter-openfeign 的服务）
     */
    @Bean
    @ConditionalOnClass(RequestInterceptor.class)
    public FeignAuthInterceptor feignAuthInterceptor() {
        return new FeignAuthInterceptor();
    }

    // 注册 Sa-Token 拦截器，打开注解式鉴权功能
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册 Sa-Token 拦截器，打开注解式鉴权功能
        registry.addInterceptor(new SaInterceptor()).addPathPatterns("/**");
    }
}
