package cn.muziseo.service.system.module.monitor.api;

import cn.muziseo.common.core.event.DataTracerEvent;
import cn.muziseo.service.system.module.monitor.datatracer.service.DataTracerService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 数据变更记录 RPC 实现
 */
@RestController
@RequiredArgsConstructor
public class DataTracerApiImpl implements DataTracerApi {

    private final DataTracerService dataTracerService;

    @Override
    @Operation(summary = "保存数据变更记录")
    public void saveDataTracer(@RequestBody DataTracerEvent dataTracerEvent) {
        dataTracerService.addTrace(dataTracerEvent);
    }
}
