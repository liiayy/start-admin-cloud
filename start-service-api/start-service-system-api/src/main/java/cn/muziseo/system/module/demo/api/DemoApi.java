package cn.muziseo.system.module.demo.api;

import cn.muziseo.system.constants.ApiConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = ApiConstants.NAME)
public interface DemoApi {

    String PREFIX = ApiConstants.PREFIX + "/demo";

    @GetMapping(PREFIX + "/demo")
    String demo();

}
