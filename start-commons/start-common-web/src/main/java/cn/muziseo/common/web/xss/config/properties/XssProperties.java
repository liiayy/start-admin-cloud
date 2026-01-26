package cn.muziseo.common.web.xss.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.Collections;
import java.util.List;

/**
 * XSS 配置属性类
 *
 * @author 木子软件
 * @Date 2026-01-26
 * @Copyright <a href="https://code.muziseo.cn">木子软件</a>
 */
@ConfigurationProperties(prefix = "start.xss")
@Data
@Validated
public class XssProperties {

    /**
     * 是否启用 XSS 过滤
     */
    private Boolean enable = true;

    /**
     * 排除的路径列表
     */
    private List<String> excludePaths = Collections.emptyList();
}
