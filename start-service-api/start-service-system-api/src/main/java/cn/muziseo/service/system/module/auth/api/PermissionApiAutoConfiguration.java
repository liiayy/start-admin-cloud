package cn.muziseo.service.system.module.auth.api;

import cn.muziseo.service.system.module.auth.api.aspect.RemoteDataScopeAspect;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * 权限控制自动装配
 *
 * @author 木子软件
 */
@AutoConfiguration
public class PermissionApiAutoConfiguration {

    /**
     * 当且仅当引入了 PermissionApi (即 Consumer 端且有 Feign 扫描) 
     * 且当前环境中没有本地 DataScopeAspect (即排除 system 服务自身) 时，
     * 自动装配远程数据权限切面。
     */
    @Bean
    @ConditionalOnBean(PermissionApi.class)
    @ConditionalOnMissingBean(name = "dataScopeAspect")
    public RemoteDataScopeAspect remoteDataScopeAspect() {
        return new RemoteDataScopeAspect();
    }
}
