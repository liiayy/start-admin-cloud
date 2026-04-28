package cn.muziseo.service.system.module.notice.service;

import java.util.List;

/**
 * WebSocket 多维路由灰度分发服务
 * 
 * @author 木子软件
 */
public interface WebSocketSendService {

    /**
     * 按部门、角色、岗位多维精准派发实时消息
     * 
     * @param deptIds 部门 ID 集合 (为空忽略)
     * @param roleIds 角色 ID 集合 (为空忽略)
     * @param postIds 岗位 ID 集合 (为空忽略)
     * @param payload 消息文本载荷
     */
    void sendToScope(List<Long> deptIds, List<Long> roleIds, List<Long> postIds, String payload);
}
