package cn.muziseo.service.system.module.monitor.api;

import cn.muziseo.common.core.event.DataTracerEvent;
import cn.muziseo.service.system.constants.ApiConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 数据变更记录 RPC 接口
 */
@FeignClient(name = ApiConstants.NAME, contextId = "dataTracerApi")
public interface DataTracerApi {

    String PREFIX = ApiConstants.PREFIX + "/monitor/datatracer";

    @PostMapping(PREFIX + "/save")
    void saveDataTracer(@RequestBody DataTracerEvent dataTracerEvent);
}
