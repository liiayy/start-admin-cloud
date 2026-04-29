package cn.muziseo.service.system.module.notice.service.impl;

import cn.muziseo.common.websocket.dto.WebSocketMessageDTO;
import cn.muziseo.common.websocket.utils.WebSocketUtils;
import cn.muziseo.service.system.module.auth.manager.UserManager;
import cn.muziseo.service.system.module.auth.repository.entity.UserEntity;
import cn.muziseo.service.system.module.notice.service.WebSocketSendService;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * WebSocket 多维路由灰度分发服务实现 (聚合 SQL 优化版本)
 * 
 * @author 木子软件
 */
@Service
@Slf4j
public class WebSocketSendServiceImpl implements WebSocketSendService {

    @Resource
    private UserManager userManager;

    /**
     * 多维路由灰度分发 WebSocket 消息
     * <p>
     * 根据部门、角色、岗位交叉范围定位用户，并发布消息
     *
     * @param deptIds 部门 ID 列表
     * @param roleIds 角色 ID 列表
     * @param postIds 岗位 ID 列表
     * @param payload 推送载荷
     */
    @Override
    public void sendToScope(List<Long> deptIds, List<Long> roleIds, List<Long> postIds, String payload) {
        QueryWrapper qw = QueryWrapper.create().where(UserEntity::getDeleted).eq(false);
        
        List<String> orConditions = new ArrayList<>();

        // 1. 部门灰度范围
        if (deptIds != null && !deptIds.isEmpty()) {
            orConditions.add("dept_id IN (" + cn.hutool.core.util.StrUtil.join(",", deptIds) + ")");
        }

        // 2. 角色灰度范围
        if (roleIds != null && !roleIds.isEmpty()) {
            orConditions.add("id IN (SELECT user_id FROM system_user_role WHERE role_id IN (" + cn.hutool.core.util.StrUtil.join(",", roleIds) + "))");
        }

        // 3. 岗位灰度范围 (适配 PostgreSQL 数组交集运算符 &&)
        if (postIds != null && !postIds.isEmpty()) {
            orConditions.add("post_ids && ARRAY[" + cn.hutool.core.util.StrUtil.join(",", postIds) + "]::bigint[]");
        }

        if (orConditions.isEmpty()) {
            log.warn("[WebSocketSend] 未匹配到任何定向条件，取消此次推送");
            return;
        }

        // 聚合 OR 语句
        String aggregatedOrSql = "(" + cn.hutool.core.util.StrUtil.join(" OR ", orConditions) + ")";
        qw.and(aggregatedOrSql);

        List<UserEntity> users = userManager.list(qw);
        if (users == null || users.isEmpty()) {
            log.warn("[WebSocketSend] 未匹配到满足条件的灰度有效用户范围");
            return;
        }

        List<Long> targetUserIds = users.stream()
            .map(UserEntity::getId)
            .collect(Collectors.toList());

        WebSocketUtils.publishMessage(WebSocketMessageDTO.of(targetUserIds, payload));
        log.info("[WebSocketSend] 定向推送成功: 发送用户数={}, 发送载荷={}", targetUserIds.size(), payload);
    }
}
