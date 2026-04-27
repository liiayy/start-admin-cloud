package cn.muziseo.common.web.core.idempotent;
 
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
 
/**
 * 幂等性自动配置
 *
 * @author 木子软件
 */
@AutoConfiguration
@EnableAspectJAutoProxy
public class IdempotentAutoConfiguration {
 
    @Bean
    public IdempotentAspect idempotentAspect() {
        return new IdempotentAspect();
    }
}
