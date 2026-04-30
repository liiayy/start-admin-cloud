package cn.muziseo.common.websocket.holder;

import cn.muziseo.common.cache.utils.RedisUtils;
import cn.muziseo.common.core.constant.CacheConstants;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket 会话管理器
 * <p>
 * 采用二级映射结构：userId → (sessionId → WebSocketSession)
 * 支持同一用户在多个设备/标签页建立多个连接，避免互相挤号。
 * <p>
 * 线程安全：基于 ConcurrentHashMap + synchronized 关键字保证并发安全。
 *
 * @author 木子软件
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WebSocketSessionHolder {

    /**
     * 二级映射：userId → (sessionId → WebSocketSession)
     */
    private static final ConcurrentHashMap<Long, ConcurrentHashMap<String, WebSocketSession>> USER_SESSION_MAP
            = new ConcurrentHashMap<>();

    /**
     * 集群节点独立唯一标识（重启时自动隔离死连接）
     */
    public static final String NODE_ID = java.util.UUID.randomUUID().toString();



    static {
        java.util.concurrent.Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            try {
                String key = CacheConstants.WS_ONLINE_PREFIX + NODE_ID + ":sessions";
                org.redisson.api.RMap<String, Integer> nodeMap = RedisUtils.getClient().getMap(key);
                if (!nodeMap.isEmpty()) {
                    nodeMap.expire(java.time.Duration.ofSeconds(60));
                }
            } catch (Exception ignored) {}
        }, 20, 20, java.util.concurrent.TimeUnit.SECONDS);
    }

    /**
     * 注册会话
     *
     * @param userId    用户 ID
     * @param sessionId 会话 ID
     * @param session   WebSocket 会话
     */
    public static void addSession(Long userId, String sessionId, WebSocketSession session) {
        ConcurrentHashMap<String, WebSocketSession> userSessions =
                USER_SESSION_MAP.computeIfAbsent(userId, k -> new ConcurrentHashMap<>());

        // 如果 sessionId 已存在（理论上不会），先关闭旧连接
        WebSocketSession existingSession = userSessions.get(sessionId);
        if (existingSession != null && existingSession.isOpen()) {
            try {
                existingSession.close();
            } catch (IOException e) {
                log.warn("[WebSocket] 关闭旧会话异常: userId={}, sessionId={}, error={}",
                        userId, sessionId, e.getMessage());
            }
        }

        WebSocketSession oldSession = userSessions.put(sessionId, session);

        // 分布式集群状态同步：若为新会话，递增本节点的全局连接引用计数
        if (oldSession == null) {
            try {
                String key = CacheConstants.WS_ONLINE_PREFIX + NODE_ID + ":sessions";
                org.redisson.api.RMap<String, Integer> nodeMap = RedisUtils.getClient().getMap(key);
                nodeMap.addAndGet(userId.toString(), 1);
                nodeMap.expire(java.time.Duration.ofSeconds(60));
            } catch (Exception e) {
                log.error("[WebSocket] 同步 Redis 在线状态异常 (Incr): userId={}, error={}", userId, e.getMessage());
            }
        }

        log.info("[WebSocket] 用户连接建立: userId={}, sessionId={}, 当前连接数={}",
                userId, sessionId, userSessions.size());
    }

    /**
     * 移除指定会话
     *
     * @param userId    用户 ID
     * @param sessionId 会话 ID
     */
    public static void removeSession(Long userId, String sessionId) {
        ConcurrentHashMap<String, WebSocketSession> userSessions = USER_SESSION_MAP.get(userId);
        if (userSessions == null) {
            return;
        }

        WebSocketSession removedSession = userSessions.remove(sessionId);

        // 分布式集群状态同步：仅当该会话确实存在并被移除时，才扣减引用计数
        if (removedSession != null) {
            try {
                String key = CacheConstants.WS_ONLINE_PREFIX + NODE_ID + ":sessions";
                org.redisson.api.RMap<String, Integer> nodeMap = RedisUtils.getClient().getMap(key);
                long remaining = nodeMap.addAndGet(userId.toString(), -1);
                if (remaining <= 0) {
                    nodeMap.remove(userId.toString());
                }
            } catch (Exception e) {
                log.error("[WebSocket] 同步 Redis 在线状态异常 (Decr): userId={}, error={}", userId, e.getMessage());
            }
        }

        // 如果该用户已无连接，清理映射
        if (userSessions.isEmpty()) {
            USER_SESSION_MAP.remove(userId);
        }

        log.info("[WebSocket] 用户连接关闭: userId={}, sessionId={}, 剩余连接数={}",
                userId, sessionId, userSessions.size());
    }

    /**
     * 获取用户的所有会话
     *
     * @param userId 用户 ID
     * @return 会话映射（sessionId → WebSocketSession），如果用户不在线则返回空 Map
     */
    public static Map<String, WebSocketSession> getUserSessions(Long userId) {
        ConcurrentHashMap<String, WebSocketSession> userSessions = USER_SESSION_MAP.get(userId);
        return userSessions != null ? Collections.unmodifiableMap(userSessions) : Collections.emptyMap();
    }

    /**
     * 获取用户的特定会话
     *
     * @param userId    用户 ID
     * @param sessionId 会话 ID
     * @return WebSocket 会话，不存在则返回 null
     */
    public static WebSocketSession getSession(Long userId, String sessionId) {
        ConcurrentHashMap<String, WebSocketSession> userSessions = USER_SESSION_MAP.get(userId);
        return userSessions != null ? userSessions.get(sessionId) : null;
    }

    /**
     * 判断用户是否在线
     *
     * @param userId 用户 ID
     * @return true=在线
     */
    public static boolean isUserOnline(Long userId) {
        ConcurrentHashMap<String, WebSocketSession> userSessions = USER_SESSION_MAP.get(userId);
        return userSessions != null && !userSessions.isEmpty();
    }

    /**
     * 获取所有在线用户 ID
     *
     * @return 在线用户 ID 集合
     */
    public static Set<Long> getAllOnlineUserIds() {
        return Collections.unmodifiableSet(USER_SESSION_MAP.keySet());
    }

    /**
     * 获取连接统计信息
     *
     * @return [在线用户数, 总连接数]
     */
    public static int[] getConnectionStats() {
        int userCount = USER_SESSION_MAP.size();
        int sessionCount = USER_SESSION_MAP.values().stream()
                .mapToInt(ConcurrentHashMap::size)
                .sum();
        return new int[]{userCount, sessionCount};
    }
}
