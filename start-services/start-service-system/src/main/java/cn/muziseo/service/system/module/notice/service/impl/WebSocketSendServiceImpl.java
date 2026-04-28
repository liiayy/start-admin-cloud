package cn.muziseo.service.system.module.notice.service.impl;

import cn.muziseo.common.websocket.dto.WebSocketMessageDTO;
import cn.muziseo.common.websocket.utils.WebSocketUtils;
import cn.muziseo.service.system.module.notice.service.WebSocketSendService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * WebSocket 多维路由灰度分发服务实现
 * 
 * @author 木子软件
 */
@Service
@Slf4j
public class WebSocketSendServiceImpl implements WebSocketSendService {

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Override
    public void sendToScope(List<Long> deptIds, List<Long> roleIds, List<Long> postIds, String payload) {
        Set<Long> targetUserIds = new HashSet<>();

        // 1. 部门筛选
        if (deptIds != null && !deptIds.isEmpty()) {
            for (Long dId : deptIds) {
                try {
                    List<Long> uIds = jdbcTemplate.queryForList("SELECT id FROM system_user WHERE dept_id = ? AND deleted = false", Long.class, dId);
                    if (uIds != null) targetUserIds.addAll(uIds);
                } catch (Exception ignored) {}
            }
        }

        // 2. 角色筛选
        if (roleIds != null && !roleIds.isEmpty()) {
            for (Long rId : roleIds) {
                try {
                    List<Long> uIds = jdbcTemplate.queryForList("SELECT user_id FROM system_user_role WHERE role_id = ?", Long.class, rId);
                    if (uIds != null) targetUserIds.addAll(uIds);
                } catch (Exception ignored) {}
            }
        }

        // 3. 岗位筛选
        if (postIds != null && !postIds.isEmpty()) {
            for (Long pId : postIds) {
                try {
                    List<Long> uIds = jdbcTemplate.queryForList("SELECT id FROM system_user WHERE post_ids LIKE ? AND deleted = false", Long.class, "%" + pId + "%");
                    if (uIds != null) targetUserIds.addAll(uIds);
                } catch (Exception ignored) {}
            }
        }

        if (!targetUserIds.isEmpty()) {
            WebSocketUtils.publishMessage(WebSocketMessageDTO.of(new ArrayList<>(targetUserIds), payload));
            log.info("[WebSocketSend] 触发定向灰度发布成功: usersCount={}, targetScopes(depts={}, roles={}, posts={})", 
                targetUserIds.size(), deptIds, roleIds, postIds);
        } else {
            log.warn("[WebSocketSend] 未匹配到任何范围内的有效接收用户: targetScopes(depts={}, roles={}, posts={})",
                deptIds, roleIds, postIds);
        }
    }
}
