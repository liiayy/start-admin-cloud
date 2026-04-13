package cn.muziseo.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 网关认证白名单配置
 *
 * @author 木子软件
 */
@Data
@Component
@ConfigurationProperties(prefix = "gateway.auth")
public class AuthWhiteProperties {

    /**
     * 精确匹配的业务路径白名单
     * <p>
     * 匹配的是去掉路由前缀后的实际路径，如 /auth/login
     */
    private List<String> whitePaths = new ArrayList<>();

    /**
     * 前缀匹配的全局路径白名单
     * <p>
     * 直接匹配网关看到的完整路径前缀，如 /swagger-ui、/actuator
     */
    private List<String> whitePrefixes = new ArrayList<>();
}
