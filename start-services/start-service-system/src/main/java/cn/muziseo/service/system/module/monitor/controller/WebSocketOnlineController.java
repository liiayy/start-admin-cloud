package cn.muziseo.service.system.module.monitor.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.exception.ApiDisabledException;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.json.JSONUtil;
import cn.muziseo.common.cache.utils.RedisUtils;
import cn.muziseo.common.core.domain.dto.ResponseDTO;
import cn.muziseo.common.websocket.dto.WebSocketMessageDTO;
import cn.muziseo.common.websocket.holder.WebSocketSessionHolder;
import cn.muziseo.common.websocket.utils.WebSocketUtils;
import cn.muziseo.common.log.annotation.Log;
import cn.muziseo.common.log.enums.BusinessType;
import cn.muziseo.service.system.module.auth.repository.entity.UserEntity;
import cn.muziseo.service.system.module.auth.service.UserService;
import cn.muziseo.service.system.module.permission.repository.entity.MenuEntity;
import cn.muziseo.service.system.module.permission.repository.mapper.MenuMapper;
import com.mybatisflex.core.query.QueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.PostConstruct;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * WebSocket 在线连接监控 Controller
 *
 * @author 木子软件
 */
@Tag(name = "WebSocket在线连接")
@RestController
@RequestMapping("/admin/system/websocket")
public class WebSocketOnlineController {

    private final UserService userService;


    public WebSocketOnlineController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "获取在线连接统计与用户列表")
    @GetMapping("/online")
    @SaCheckPermission("monitor:websocket:query")
    public ResponseDTO<WebSocketOnlineVO> getOnlineStats() {
        // 1. 查找所有活跃节点的前缀键
        Iterable<String> nodeKeys = RedisUtils.getClient().getKeys().getKeysByPattern("ws:cluster:node:*:sessions");
        Map<Long, Integer> aggregatedCounts = new java.util.HashMap<>();
        int totalSessionCount = 0;

        if (nodeKeys != null) {
            for (String key : nodeKeys) {
                try {
                    org.redisson.api.RMap<String, Integer> nodeMap = RedisUtils.getClient().getMap(key);
                    for (Map.Entry<String, Integer> entry : nodeMap.entrySet()) {
                        Long userId = Long.parseLong(entry.getKey());
                        Integer val = entry.getValue();
                        if (val != null && val > 0) {
                            aggregatedCounts.merge(userId, val, Integer::sum);
                            totalSessionCount += val;
                        }
                    }
                } catch (Exception ignored) {}
            }
        }

        List<OnlineUserDetailVO> details = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : aggregatedCounts.entrySet()) {
            Long userId = entry.getKey();
            int finalCount = entry.getValue();
            try {
                UserEntity user = userService.getUserById(userId);
                if (user != null) {
                    details.add(OnlineUserDetailVO.builder()
                            .userId(user.getId())
                            .username(user.getUsername())
                            .nickname(user.getNickname())
                            .loginIp(user.getLoginIp())
                            .loginDate(user.getLoginDate())
                            .sessionCount(finalCount)
                            .build());
                }
            } catch (Exception ignored) {}
        }

        WebSocketOnlineVO vo = WebSocketOnlineVO.builder()
                .userCount(aggregatedCounts.size())
                .sessionCount(totalSessionCount)
                .onlineUserIds(aggregatedCounts.keySet())
                .userDetails(details)
                .build();

        return ResponseDTO.success(vo);
    }

    @Operation(summary = "强制用户下线")
    @DeleteMapping("/online/{userId}")
    @Log(title = "在线用户", businessType = BusinessType.FORCE)
    @SaCheckPermission("monitor:websocket:kickout")
    public ResponseDTO<Void> forceLogout(@PathVariable Long userId) {
        // 1. 发送 WebSocket 强制下线消息给该用户
        // 组装强制下线消息 JSON
        Map<String, Object> map = new HashMap<>();
        map.put("type", "force_logout");
        map.put("title", "下线通知");
        Map<String, Object> data = new HashMap<>();
        data.put("message", "您的账号已被管理员强制下线！");
        map.put("data", data);
        map.put("timestamp", System.currentTimeMillis());
        
        WebSocketUtils.publishMessage(WebSocketMessageDTO.of(userId, JSONUtil.toJsonStr(map)));

        // 2. 通过 Sa-Token 尝试踢出该用户（若使用的 JWT Mixin 无状态模式抛出 ApiDisabledException 则忽略，主要依赖前端 WebSocket 收到指令后主动登出）
        try {
            StpUtil.kickout(userId);
        } catch (ApiDisabledException e) {
            // 无状态模式下该接口被禁用，忽略
        }

        return ResponseDTO.success();
    }

    @Data
    @Builder
    public static class WebSocketOnlineVO {
        /**
         * 在线用户数
         */
        private Integer userCount;

        /**
         * 总连接数（可能有同一用户多开标签页）
         */
        private Integer sessionCount;

        /**
         * 在线用户 ID 集合
         */
        private Set<Long> onlineUserIds;

        /**
         * 在线用户明细列表
         */
        private java.util.List<OnlineUserDetailVO> userDetails;
    }

    @Data
    @Builder
    public static class OnlineUserDetailVO {
        private Long userId;
        private String username;
        private String nickname;
        private String loginIp;
        private java.time.LocalDateTime loginDate;
        private Integer sessionCount;
    }
}
