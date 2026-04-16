package cn.muziseo.common.http.client.gaode;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 高德地图配置属性
 *
 * @author 木子软件
 */
@Data
@Component
@ConfigurationProperties(prefix = "gaode.map")
public class GaodeMapProperties {

    /** 是否启用 */
    private Boolean enabled = false;

    /** API Key */
    private String apiKey;

    /** 超时时间 (ms) */
    private Integer timeout = 5000;
}
