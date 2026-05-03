package cn.muziseo.common.social.config;

import cn.muziseo.common.social.config.properties.SocialProperties;
import cn.muziseo.common.social.utils.AuthRedisStateCache;
import me.zhyd.oauth.cache.AuthStateCache;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;


/**
 * 社交登录自动配置类
 *
 * @author 木子软件
 */
@AutoConfiguration
@EnableConfigurationProperties(SocialProperties.class)
public class SocialAutoConfiguration {

    /**
     * 配置认证状态缓存
     * 使用 Redis 作为 OAuth 认证过程中的状态缓存存储
     */
    @Bean
    @ConditionalOnMissingBean
    public AuthStateCache authStateCache() {
        return new AuthRedisStateCache();
    }
}

