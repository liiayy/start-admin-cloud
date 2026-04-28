package cn.muziseo.common.websocket.handler;

import cn.muziseo.common.websocket.holder.WebSocketSessionHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * WebSocket 消息处理器
 * <p>
 * 处理 WebSocket 连接的生命周期事件和消息收发。
 * 支持心跳检测（客户端发送 "ping"，服务端回复 "pong"）。
 *
 * @author 木子软件
 */
@Slf4j
public class WebSocketMessageHandler extends TextWebSocketHandler {

    /**
     * 连接建立
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        Long userId = getUserId(session);
        if (userId == null) {
            log.warn("[WebSocket] 连接建立但无法获取 userId，关闭连接: sessionId={}", session.getId());
            try {
                session.close(CloseStatus.NOT_ACCEPTABLE);
            } catch (Exception e) {
                log.error("[WebSocket] 关闭连接异常: {}", e.getMessage());
            }
            return;
        }

        WebSocketSessionHolder.addSession(userId, session.getId(), session);
    }

    /**
     * 处理文本消息
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        Long userId = getUserId(session);
        String payload = message.getPayload();

        // 心跳处理：客户端发送 "ping"，服务端回复 "pong"
        if ("ping".equalsIgnoreCase(payload.trim())) {
            try {
                session.sendMessage(new TextMessage("pong"));
            } catch (Exception e) {
                log.warn("[WebSocket] 发送心跳回复失败: userId={}, sessionId={}, error={}",
                        userId, session.getId(), e.getMessage());
            }
            return;
        }

        // JSON 格式心跳兼容处理
        if (payload.contains("\"type\"") && payload.contains("\"ping\"")) {
            try {
                session.sendMessage(new TextMessage("{\"type\":\"pong\"}"));
            } catch (Exception e) {
                log.warn("[WebSocket] 发送 JSON 心跳回复失败: userId={}, error={}", userId, e.getMessage());
            }
            return;
        }

        // 其他业务消息（当前版本仅记录日志，后续可扩展自定义处理器链）
        log.debug("[WebSocket] 收到业务消息: userId={}, sessionId={}, payload={}",
                userId, session.getId(), payload);
    }

    /**
     * 连接关闭
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Long userId = getUserId(session);
        if (userId != null) {
            WebSocketSessionHolder.removeSession(userId, session.getId());
        }
        log.debug("[WebSocket] 连接关闭: sessionId={}, status={}", session.getId(), status);
    }

    /**
     * 传输错误
     */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        Long userId = getUserId(session);
        log.error("[WebSocket] 传输错误: userId={}, sessionId={}, error={}",
                userId, session.getId(), exception.getMessage());

        // 移除异常会话
        if (userId != null) {
            WebSocketSessionHolder.removeSession(userId, session.getId());
        }
        try {
            if (session.isOpen()) {
                session.close(CloseStatus.SERVER_ERROR);
            }
        } catch (Exception e) {
            log.warn("[WebSocket] 关闭异常会话失败: {}", e.getMessage());
        }
    }

    /**
     * 从会话属性中获取用户 ID
     */
    private Long getUserId(WebSocketSession session) {
        Object userId = session.getAttributes().get(WebSocketAuthInterceptor.ATTR_USER_ID);
        return userId instanceof Long ? (Long) userId : null;
    }
}
