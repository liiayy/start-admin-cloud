package cn.muziseo.service.system.module.demo.api;

import cn.muziseo.service.system.constants.ApiConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = ApiConstants.NAME, contextId = "demoApi")
public interface DemoApi {

    String PREFIX = ApiConstants.PREFIX + "/demo";

    @GetMapping(PREFIX + "/demo")
    String demo();

}
