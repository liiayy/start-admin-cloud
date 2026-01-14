package cn.muziseo.gateway.config.ratelimit;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 限流配置属性
 *
 * @author  Administrator
 * @since 2026-01-06
 * @see <a href="https://github.com/muziseo">木子软件</a>
 */
@Data
@Component
@ConfigurationProperties(prefix = "gateway.ratelimit")
public class RateLimitProperties {

    /**
     * 是否启用限流
     */
    private boolean enabled = true;

    /**
     * Redis连接配置
     */
    private RedisConfig redis = new RedisConfig();

    /**
     * 限流规则配置
     */
    private Map<String, RateLimitRule> rules;

    /**
     * Redis配置
     */
    @Data
    public static class RedisConfig {
        /**
         * Redis主机地址
         */
        private String host = "localhost";

        /**
         * Redis端口
         */
        private int port = 6379;

        /**
         * Redis密码
         */
        private String password = "";

        /**
         * Redis数据库索引
         */
        private int database = 0;

        /**
         * Redis连接超时时间（毫秒）
         */
        private int timeout = 10000;
    }

    /**
     * 限流规则
     */
    @Data
    public static class RateLimitRule {
        /**
         * 路径模式
         */
        private String pattern;

        /**
         * 时间窗口（秒）
         */
        private int window = 60;

        /**
         * 时间窗口内允许的最大请求数
         */
        private int limit = 100;

        /**
         * 是否启用
         */
        private boolean enabled = true;
    }
}