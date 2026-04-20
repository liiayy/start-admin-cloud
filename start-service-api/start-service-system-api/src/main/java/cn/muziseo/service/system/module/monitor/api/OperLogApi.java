package cn.muziseo.service.system.module.monitor.api;

import cn.muziseo.common.core.event.OperLogEvent;
import cn.muziseo.service.system.constants.ApiConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 操作日志 RPC 接口
 */
@FeignClient(name = ApiConstants.NAME, contextId = "operLogApi")
public interface OperLogApi {

    String PREFIX = ApiConstants.PREFIX + "/monitor/operlog";

    @PostMapping(PREFIX + "/save")
    void saveOperLog(@RequestBody OperLogEvent operLog);
}
