package cn.muziseo.common.websocket.config;

import cn.muziseo.common.websocket.handler.WebSocketAuthInterceptor;
import cn.muziseo.common.websocket.handler.WebSocketMessageHandler;
import cn.muziseo.common.websocket.listener.WebSocketTopicListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * WebSocket 自动配置类
 * <p>
 * 当 websocket.enabled=true 时自动激活，注册 WebSocket 端点、拦截器和 Redis 监听器。
 *
 * @author 木子软件
 */
@Slf4j
@AutoConfiguration
@EnableWebSocket
@EnableConfigurationProperties(WebSocketProperties.class)
@ConditionalOnProperty(name = "websocket.enabled", havingValue = "true")
@RequiredArgsConstructor
public class WebSocketAutoConfiguration implements WebSocketConfigurer {

    private final WebSocketProperties properties;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketMessageHandler(), properties.getPath())
                .addInterceptors(webSocketAuthInterceptor())
                .setAllowedOrigins(properties.getAllowedOrigins());

        log.info("[WebSocket] 端点已注册: path={}, allowedOrigins={}",
                properties.getPath(), properties.getAllowedOrigins());
    }

    @Bean
    public WebSocketMessageHandler webSocketMessageHandler() {
        return new WebSocketMessageHandler();
    }

    @Bean
    public WebSocketAuthInterceptor webSocketAuthInterceptor() {
        return new WebSocketAuthInterceptor();
    }

    @Bean
    public WebSocketTopicListener webSocketTopicListener() {
        return new WebSocketTopicListener();
    }
}
