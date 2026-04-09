package cn.muziseo.service.system.module.demo.controller;

import cn.muziseo.common.core.domain.dto.ResponseDTO;
import cn.muziseo.service.system.module.demo.repository.entity.DemoEntity;
import cn.muziseo.service.system.module.demo.service.DemoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 示例模块 Controller
 *
 * @author 木子软件
 */
@Tag(name = "示例模块")
@RestController
@RequestMapping("/demo")
public class DemoController {

    @Resource
    DemoService demoService;

    @Operation(summary = "查询全部示例数据")
    @GetMapping("/list")
    public ResponseDTO<List<DemoEntity>> list() {
        return ResponseDTO.success(demoService.getAll());
    }
}
