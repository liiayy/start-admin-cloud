package cn.muziseo.common.websocket.handler;

import cn.dev33.satoken.stp.StpUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * WebSocket 握手拦截器
 * <p>
 * 在 WebSocket 握手阶段校验用户身份。
 * 由于浏览器 WebSocket API 不支持自定义 Header，
 * Token 通过 URL 的 query 参数传递：ws://host/websocket?token=xxx
 *
 * @author 木子软件
 */
@Slf4j
public class WebSocketAuthInterceptor implements HandshakeInterceptor {

    /**
     * WebSocket 会话属性中存储用户 ID 的 key
     */
    public static final String ATTR_USER_ID = "ws_user_id";

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        try {
            // 1. 从 query 参数中获取 token
            String token = null;
            if (request instanceof ServletServerHttpRequest servletRequest) {
                HttpServletRequest httpRequest = servletRequest.getServletRequest();
                token = httpRequest.getParameter("token");
            }

            if (token == null || token.isBlank()) {
                log.warn("[WebSocket] 握手失败: 缺少 token 参数");
                return false;
            }

            // 2. 使用 Sa-Token 校验 token 有效性
            Object loginId = StpUtil.getLoginIdByToken(token);
            if (loginId == null) {
                log.warn("[WebSocket] 握手失败: token 无效或已过期");
                return false;
            }

            // 3. 将 userId 注入会话属性，供后续处理器使用
            Long userId = Long.parseLong(loginId.toString());
            attributes.put(ATTR_USER_ID, userId);

            log.debug("[WebSocket] 握手成功: userId={}", userId);
            return true;

        } catch (Exception e) {
            log.error("[WebSocket] 握手异常: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        // 握手完成后的回调，暂无额外逻辑
    }
}
