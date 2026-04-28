package cn.muziseo.service.system.module.monitor.api;

import cn.hutool.core.bean.BeanUtil;
import cn.muziseo.common.core.event.OperLogEvent;
import cn.muziseo.service.system.module.monitor.convert.LogConverter;
import cn.muziseo.service.system.module.monitor.repository.entity.OperLogEntity;
import cn.muziseo.service.system.module.monitor.service.OperLogService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 操作日志 RPC 实现
 */
@RestController
@RequiredArgsConstructor
public class OperLogApiImpl implements OperLogApi {

    private final OperLogService operLogService;
    private final LogConverter logConverter;

    @Override
    @Operation(summary = "保存操作日志")
    public void saveOperLog(@RequestBody OperLogEvent operLog) {
        OperLogEntity entity = new OperLogEntity();
        logConverter.copyToEntity(operLog, entity);
        operLogService.save(entity);
    }
}
