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
        return new cn.dev33.satoken.jwt.StpLogicJwtForStateless();
    }

    @org.springframework.beans.factory.annotation.Autowired
    public void setStpLogic(StpLogic stpLogic) {
        cn.dev33.satoken.stp.StpUtil.setStpLogic(stpLogic);
    }

    /**
     * 注册 Sa-Token 全局过滤器，解决 WebFlux 环境下的上下文初始化问题
     */
    @Bean
    public cn.dev33.satoken.reactor.filter.SaReactorFilter getSaReactorFilter() {
        return new cn.dev33.satoken.reactor.filter.SaReactorFilter()
                .addInclude("/**")
                .setAuth(obj -> {})
                .setError(e -> e.getMessage());
    }
}
