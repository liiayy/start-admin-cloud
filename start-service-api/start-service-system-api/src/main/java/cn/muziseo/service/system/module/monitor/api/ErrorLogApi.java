package cn.muziseo.service.system.module.monitor.api;

import cn.muziseo.common.core.event.ErrorLogEvent;
import cn.muziseo.service.system.constants.ApiConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 错误日志 RPC 接口
 */
@FeignClient(name = ApiConstants.NAME, contextId = "errorLogApi")
public interface ErrorLogApi {

    String PREFIX = ApiConstants.PREFIX + "/monitor/error-log";

    @PostMapping(PREFIX + "/save")
    void saveErrorLog(@RequestBody ErrorLogEvent errorLog);
}
