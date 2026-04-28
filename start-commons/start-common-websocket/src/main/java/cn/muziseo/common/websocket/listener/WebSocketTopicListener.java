package cn.muziseo.common.websocket.listener;

import cn.hutool.core.collection.CollUtil;
import cn.muziseo.common.websocket.dto.WebSocketMessageDTO;
import cn.muziseo.common.websocket.holder.WebSocketSessionHolder;
import cn.muziseo.common.websocket.utils.WebSocketUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

/**
 * WebSocket Redis 主题监听器
 * <p>
 * 应用启动后初始化 Redis 订阅，处理来自其他服务实例的跨实例消息分发。
 *
 * @author 木子软件
 */
@Slf4j
public class WebSocketTopicListener {

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        WebSocketUtils.subscribeMessage(this::handleMessage);
        log.info("[WebSocket] Redis 消息订阅已初始化, topic={}", WebSocketUtils.WS_TOPIC);
    }

    /**
     * 处理来自 Redis 的消息
     */
    private void handleMessage(WebSocketMessageDTO message) {
        try {
            if (Boolean.TRUE.equals(message.getBroadcast())) {
                // 广播模式：向本实例所有在线用户发送
                WebSocketSessionHolder.getAllOnlineUserIds().forEach(userId ->
                        WebSocketUtils.sendMessage(userId, message.getMessage()));
                log.debug("[WebSocket] 处理跨实例广播消息");
                return;
            }

            // 定向模式：向本实例中的目标用户发送
            if (CollUtil.isNotEmpty(message.getUserIds())) {
                for (Long userId : message.getUserIds()) {
                    if (WebSocketSessionHolder.isUserOnline(userId)) {
                        WebSocketUtils.sendMessage(userId, message.getMessage());
                    }
                }
                log.debug("[WebSocket] 处理跨实例定向消息: 目标用户={}", message.getUserIds());
            }

        } catch (Exception e) {
            log.error("[WebSocket] 处理跨实例消息异常: {}", e.getMessage(), e);
        }
    }
}
