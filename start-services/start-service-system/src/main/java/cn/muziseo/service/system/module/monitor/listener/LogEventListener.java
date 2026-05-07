package cn.muziseo.service.system.module.monitor.listener;

import cn.hutool.core.bean.BeanUtil;
import cn.muziseo.common.core.event.ErrorLogEvent;
import cn.muziseo.common.core.event.LoginLogEvent;
import cn.muziseo.common.core.event.OperLogEvent;
import cn.muziseo.service.system.module.monitor.repository.entity.LoginLogEntity;
import cn.muziseo.service.system.module.monitor.repository.entity.OperLogEntity;
import cn.muziseo.service.system.module.monitor.service.SysErrorLogService;
import cn.muziseo.service.system.module.monitor.convert.LogConverter;
import cn.muziseo.service.system.module.monitor.repository.mapper.LoginLogMapper;
import cn.muziseo.service.system.module.monitor.repository.mapper.OperLogMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 日志事件监听，异步写入数据库
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LogEventListener {

    private final OperLogMapper operLogMapper;
    private final LoginLogMapper loginLogMapper;
    private final SysErrorLogService errorLogService;
    private final LogConverter logConverter;

    /**
     * 保存操作日志
     */
    @Async
    @EventListener
    public void saveOperLog(OperLogEvent event) {
        log.info("收到操作日志事件：{}", event.getTitle());
        try {
            OperLogEntity entity = new OperLogEntity();
            logConverter.copyToEntity(event, entity);
            // FIXME: id 这里需要全局唯一ID生成器，如果数据库没有自动递增
            // 由于项目配置可能使用了分布式ID，这里我们暂且让数据库处理或后续接入Snowflake
            operLogMapper.insert(entity);
        } catch (Exception e) {
            log.error("保存操作日志失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 保存登录日志
     */
    @Async
    @EventListener
    public void saveLoginLog(LoginLogEvent event) {
        log.info("收到登录日志事件：{}", event.getUsername());
        try {
            LoginLogEntity entity = new LoginLogEntity();
            logConverter.copyToEntity(event, entity);
            loginLogMapper.insert(entity);
        } catch (Exception e) {
            log.error("保存登录日志失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 保存错误日志
     */
    @Async
    @EventListener
    public void saveErrorLog(ErrorLogEvent event) {
        log.info("收到错误日志事件：{}", event.getErrorType());
        try {
            errorLogService.saveErrorLog(event);
        } catch (Exception e) {
            log.error("保存错误日志失败: {}", e.getMessage(), e);
        }
    }
}
