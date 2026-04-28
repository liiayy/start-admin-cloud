package cn.muziseo.common.websocket.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * WebSocket 消息传输对象
 * <p>
 * 用于 Redis Pub/Sub 跨实例消息分发
 *
 * @author 木子软件
 */
@Data
public class WebSocketMessageDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 目标用户 ID 列表（为空则表示广播）
     */
    private List<Long> userIds;

    /**
     * 消息内容（JSON 字符串）
     */
    private String message;

    /**
     * 是否为广播消息
     */
    private Boolean broadcast;

    /**
     * 创建定向消息（单个用户）
     */
    public static WebSocketMessageDTO of(Long userId, String message) {
        WebSocketMessageDTO dto = new WebSocketMessageDTO();
        dto.setUserIds(Collections.singletonList(userId));
        dto.setMessage(message);
        dto.setBroadcast(false);
        return dto;
    }

    /**
     * 创建定向消息（多个用户）
     */
    public static WebSocketMessageDTO of(List<Long> userIds, String message) {
        WebSocketMessageDTO dto = new WebSocketMessageDTO();
        dto.setUserIds(userIds);
        dto.setMessage(message);
        dto.setBroadcast(false);
        return dto;
    }

    /**
     * 创建广播消息（所有在线用户）
     */
    public static WebSocketMessageDTO broadcast(String message) {
        WebSocketMessageDTO dto = new WebSocketMessageDTO();
        dto.setUserIds(Collections.emptyList());
        dto.setMessage(message);
        dto.setBroadcast(true);
        return dto;
    }
}
