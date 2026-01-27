package cn.muziseo.service.system.module.demo.controller;

import cn.muziseo.service.system.module.demo.repository.entity.DemoEntity;
import cn.muziseo.service.system.module.demo.service.DemoService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/demo")
public class DemoController {

    @Resource
    DemoService demoService;


    @GetMapping("/test")
    public String test(HttpServletRequest request, HttpServletResponse response) {
        List<DemoEntity> all = demoService.getAll();
        System.out.println(all);
        return "Hello word";
    }
}
