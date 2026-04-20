package cn.muziseo.common.log.listener;

import cn.muziseo.common.core.event.OperLogEvent;
import cn.muziseo.service.system.module.monitor.api.OperLogApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 远程日志监听器，将本地事件转发给系统服务 RPC
 */
@Slf4j
@RequiredArgsConstructor
public class RemoteLogEventListener {

    private final OperLogApi operLogApi;

    @Async
    @EventListener
    public void onOperLogEvent(OperLogEvent event) {
        log.info("转发远程操作日志：{}", event.getTitle());
        try {
            operLogApi.saveOperLog(event);
        } catch (Exception e) {
            log.error("转发远程操作日志失败：{}", e.getMessage());
        }
    }
}
