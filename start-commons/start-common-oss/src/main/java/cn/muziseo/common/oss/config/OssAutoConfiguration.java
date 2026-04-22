package cn.muziseo.common.oss.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * OSS 模块自动配置类
 *
 * @author 木子软件
 */
@Configuration
@EnableConfigurationProperties(OssConfigProperties.class)
public class OssAutoConfiguration {
}
