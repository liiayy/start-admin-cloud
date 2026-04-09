package cn.muziseo.service.demo.module.external.controller;

import cn.muziseo.common.core.domain.dto.ResponseDTO;
import cn.muziseo.service.system.module.demo.api.DemoApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Feign 调用示例 Controller
 *
 * @author 木子软件
 */
@Tag(name = "Feign 调用示例")
@RestController
@RequestMapping("/demo")
public class DemoController {

    @Resource
    DemoApi demoApi;

    @Operation(summary = "通过 Feign 调用 system 服务")
    @GetMapping("/feign-test")
    public ResponseDTO<String> feignTest() {
        String result = demoApi.demo();
        return ResponseDTO.success(result);
    }
}
