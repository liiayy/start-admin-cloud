package cn.muziseo.service.system.module.monitor.api;

import cn.muziseo.common.core.event.ErrorLogEvent;
import cn.muziseo.service.system.module.monitor.service.SysErrorLogService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 错误日志 RPC 实现
 */
@RestController
@RequiredArgsConstructor
public class ErrorLogApiImpl implements ErrorLogApi {

    private final SysErrorLogService errorLogService;

    @Override
    @Operation(summary = "保存错误日志")
    public void saveErrorLog(@RequestBody ErrorLogEvent errorLog) {
        errorLogService.saveErrorLog(errorLog);
    }
}
