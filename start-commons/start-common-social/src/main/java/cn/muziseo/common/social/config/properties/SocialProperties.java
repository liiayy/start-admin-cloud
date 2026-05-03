package cn.muziseo.common.social.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 社交登录主配置属性
 *
 * @author 木子软件
 */
@Data
@Component
@ConfigurationProperties(prefix = "justauth")
public class SocialProperties {

    /**
     * 社交登录类型配置映射
     * key: 登录类型（如：wechat_enterprise、github等）
     * value: 对应的登录配置
     */
    private Map<String, SocialLoginConfigProperties> type;
}
