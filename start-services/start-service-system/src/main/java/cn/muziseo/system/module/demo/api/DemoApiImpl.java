package cn.muziseo.system.module.demo.api;

import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoApiImpl implements DemoApi {
    @Override
    public String demo() {
        return "测试一下,看看如何";
    }
}
