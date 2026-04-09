package cn.muziseo.gateway.config;

import cn.dev33.satoken.jwt.StpLogicJwtForSimple;
import cn.dev33.satoken.stp.StpLogic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Sa-Token 配置（Gateway Reactive）
 * <p>
 * 网关层只注册 JWT 的 StpLogic，用于解析和校验 Token。
 * 不需要 TokenDao、异常处理器等 servlet 相关组件。
 *
 * @author 木子软件
 */
@Configuration
public class SaTokenConfig {

    @Bean
    public StpLogic getStpLogicJwt() {
        return new StpLogicJwtForSimple();
    }
}
