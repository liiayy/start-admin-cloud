package cn.muziseo.common.log.config;

import cn.muziseo.common.log.aspect.LogAspect;
import cn.muziseo.common.log.listener.RemoteLogEventListener;
import cn.muziseo.common.log.utils.IpLocationUtils;
import cn.muziseo.service.system.module.monitor.api.ErrorLogApi;
import cn.muziseo.service.system.module.monitor.api.OperLogApi;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 日志模块自动配类
 */
@AutoConfiguration
@EnableAsync
public class LogAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public IpLocationUtils ipLocationUtils() {
        return new IpLocationUtils();
    }

    @Bean
    @ConditionalOnMissingBean
    public LogAspect logAspect(IpLocationUtils ipLocationUtils, ApplicationEventPublisher eventPublisher) {
        return new LogAspect(ipLocationUtils, eventPublisher);
    }

    /**
     * 只有在远程模式开启且存在 OperLogApi Bean 时才注册转发监听器
     */
    @Bean
    @ConditionalOnProperty(name = "start.log.remote.enabled", havingValue = "true")
    @ConditionalOnBean({OperLogApi.class, ErrorLogApi.class})
    public RemoteLogEventListener remoteLogEventListener(OperLogApi operLogApi, ErrorLogApi errorLogApi) {
        return new RemoteLogEventListener(operLogApi, errorLogApi);
    }
}
