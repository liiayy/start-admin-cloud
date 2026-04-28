package cn.muziseo.common.websocket.utils;

import cn.hutool.core.collection.CollUtil;
import cn.muziseo.common.cache.utils.RedisUtils;
import cn.muziseo.common.websocket.dto.WebSocketMessageDTO;
import cn.muziseo.common.websocket.holder.WebSocketSessionHolder;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * WebSocket 工具类
 * <p>
 * 提供消息发送、智能路由分发等核心功能。
 * 采用「本地优先 + Redis Pub/Sub 跨实例」的智能分发策略。
 *
 * @author 木子软件
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WebSocketUtils {

    /**
     * Redis Pub/Sub 主题名称
     */
    public static final String WS_TOPIC = "ws:message:topic";

    /**
     * 向指定用户发送消息（发送到该用户的所有连接）
     *
     * @param userId  目标用户 ID
     * @param message 消息内容
     */
    public static void sendMessage(Long userId, String message) {
        Map<String, WebSocketSession> userSessions = WebSocketSessionHolder.getUserSessions(userId);
        if (userSessions.isEmpty()) {
            return;
        }

        TextMessage textMessage = new TextMessage(message);
        userSessions.forEach((sessionId, session) -> {
            sendToSession(session, textMessage, userId, sessionId);
        });
    }

    /**
     * 向用户的指定连接发送消息
     *
     * @param userId    目标用户 ID
     * @param sessionId 目标会话 ID
     * @param message   消息内容
     * @return true=发送成功
     */
    public static boolean sendMessage(Long userId, String sessionId, String message) {
        WebSocketSession session = WebSocketSessionHolder.getSession(userId, sessionId);
        if (session == null || !session.isOpen()) {
            return false;
        }

        return sendToSession(session, new TextMessage(message), userId, sessionId);
    }

    /**
     * 智能消息发布（本地优先 + 跨实例分发）
     * <p>
     * 1. 先尝试在当前实例本地发送
     * 2. 未送达的用户通过 Redis Pub/Sub 分发到其他实例
     *
     * @param dto 消息传输对象
     */
    public static void publishMessage(WebSocketMessageDTO dto) {
        // 广播模式
        if (Boolean.TRUE.equals(dto.getBroadcast())) {
            // 本地广播
            WebSocketSessionHolder.getAllOnlineUserIds().forEach(userId ->
                    sendMessage(userId, dto.getMessage()));

            // 同时通过 Redis 分发到其他实例
            RedisUtils.publish(WS_TOPIC, dto);
            log.debug("[WebSocket] 广播消息已发布到 Redis 主题");
            return;
        }

        // 定向模式
        List<Long> unsentUserIds = new ArrayList<>();

        for (Long userId : dto.getUserIds()) {
            if (WebSocketSessionHolder.isUserOnline(userId)) {
                // 本地在线，直接发送
                sendMessage(userId, dto.getMessage());
            } else {
                // 本地不在线，标记待分发
                unsentUserIds.add(userId);
            }
        }

        // 将本地未送达的用户通过 Redis 分发到其他实例
        if (CollUtil.isNotEmpty(unsentUserIds)) {
            WebSocketMessageDTO redisMessage = WebSocketMessageDTO.of(unsentUserIds, dto.getMessage());
            RedisUtils.publish(WS_TOPIC, redisMessage);
            log.debug("[WebSocket] 跨实例分发: 目标用户={}", unsentUserIds);
        }
    }

    /**
     * 群发消息给所有在线用户
     *
     * @param message 消息内容
     */
    public static void publishAll(String message) {
        publishMessage(WebSocketMessageDTO.broadcast(message));
    }

    /**
     * 向指定用户推送通知消息
     *
     * @param userId  目标用户 ID
     * @param title   通知标题
     * @param message 通知内容
     */
    public static void pushNotification(Long userId, String title, String message) {
        String json = buildNotificationJson(title, message);
        publishMessage(WebSocketMessageDTO.of(userId, json));
    }

    /**
     * 广播通知消息给所有用户
     *
     * @param title   通知标题
     * @param message 通知内容
     */
    public static void pushNotificationAll(String title, String message) {
        String json = buildNotificationJson(title, message);
        publishMessage(WebSocketMessageDTO.broadcast(json));
    }

    /**
     * 构建通知 JSON 字符串
     */
    private static String buildNotificationJson(String title, String message) {
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("type", "notification");
        map.put("title", title);
        
        java.util.Map<String, Object> data = new java.util.HashMap<>();
        data.put("message", message);
        map.put("data", data);
        map.put("timestamp", System.currentTimeMillis());
        
        return cn.hutool.json.JSONUtil.toJsonStr(map);
    }

    /**
     * 订阅 Redis 消息主题
     *
     * @param consumer 消息处理回调
     */
    public static void subscribeMessage(Consumer<WebSocketMessageDTO> consumer) {
        RedisUtils.subscribe(WS_TOPIC, WebSocketMessageDTO.class, consumer);
    }

    /**
     * 向单个 Session 发送消息（线程安全）
     */
    private static boolean sendToSession(WebSocketSession session, TextMessage message,
                                         Long userId, String sessionId) {
        if (session == null || !session.isOpen()) {
            return false;
        }

        try {
            synchronized (session) {
                session.sendMessage(message);
            }
            return true;
        } catch (IOException e) {
            log.warn("[WebSocket] 消息发送失败: userId={}, sessionId={}, error={}",
                    userId, sessionId, e.getMessage());
            // 发送失败，移除异常会话
            WebSocketSessionHolder.removeSession(userId, sessionId);
            return false;
        }
    }
}
