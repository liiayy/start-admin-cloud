package cn.muziseo.common.websocket.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * WebSocket 配置属性
 *
 * @author 木子软件
 */
@Data
@ConfigurationProperties(prefix = "websocket")
public class WebSocketProperties {

    /**
     * 是否启用 WebSocket 功能
     */
    private boolean enabled = false;

    /**
     * WebSocket 服务端点路径
     */
    private String path = "/websocket";

    /**
     * 允许的跨域源地址
     */
    private String allowedOrigins = "*";
}
