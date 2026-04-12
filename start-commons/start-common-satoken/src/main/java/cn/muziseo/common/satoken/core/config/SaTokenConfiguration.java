package cn.muziseo.common.satoken.core.config;

import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.jwt.StpLogicJwtForSimple;
import cn.dev33.satoken.stp.StpLogic;
import cn.muziseo.common.core.factory.YmlPropertySourceFactory;
import cn.muziseo.common.satoken.core.dao.StartSaTokenDao;
import cn.muziseo.common.satoken.core.handler.SaTokenExceptionHandler;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

/**
 * Sa-Token 基础配置
 * <p>
 * 提供 JWT、TokenDao、异常处理等基础能力。
 * 权限加载（StpInterface）由 start-common-satoken-integration 模块提供。
 */
@AutoConfiguration
@PropertySource(value = "classpath:common-satoken.yml", factory = YmlPropertySourceFactory.class)
public class SaTokenConfiguration {

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
}
