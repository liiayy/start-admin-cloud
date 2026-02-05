package cn.muziseo.service.demo.module.external.controller;

import cn.muziseo.service.system.module.demo.api.DemoApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/demo")
@Tag(name = "Demo")
public class DemoController {


    @Resource
    DemoApi demoApi;

    @GetMapping("/test")
    @Operation(summary = "test")
    public String test(HttpServletRequest request, HttpServletResponse response) {
        String demo = demoApi.demo();
        System.out.println(demo);
        return Thread.currentThread().toString();
    }
}
