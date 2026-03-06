package cn.muziseo.service.system.module.system.controller;

import cn.muziseo.common.core.domain.dto.ResponseDTO;
import cn.muziseo.service.system.module.system.controller.request.SystemConfigAddRequest;
import cn.muziseo.service.system.module.system.repository.entity.SystemConfigEntity;
import cn.muziseo.service.system.module.system.service.SystemConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统配置管理 Controller
 *
 * @author 木子软件
 * @Date 2026-02-27
 */
@Tag(name = "系统配置管理")
@RestController
@Slf4j
@RequestMapping("/system/config")
public class SystemConfigController {

    @Resource
    private SystemConfigService systemConfigService;

    /**
     * 获取系统配置列表
     */
    @Operation(summary = "获取系统配置列表")
    @GetMapping("/list")
    public ResponseDTO<List<SystemConfigEntity>> list() {
        List<SystemConfigEntity> list = systemConfigService.list();
        return ResponseDTO.success(list);
    }

    /**
     * 根据ID获取系统配置
     */
    @Operation(summary = "获取系统配置详情")
    @GetMapping("/{id}")
    public ResponseDTO<SystemConfigEntity> getById(@PathVariable Long id) {
        SystemConfigEntity config = systemConfigService.getById(id);
        return ResponseDTO.success(config);
    }

    /**
     * 根据配置键获取配置值
     */
    @Operation(summary = "获取配置值")
    @GetMapping("/value")
    public ResponseDTO<String> getConfigValue(@RequestParam String configKey) {
        String value = systemConfigService.getConfigValue(configKey);
        return ResponseDTO.success(value);
    }

    /**
     * 新增系统配置
     */
    @Operation(summary = "新增系统配置")
    @PostMapping("/add")
    public ResponseDTO<Void> add(@Valid @RequestBody SystemConfigAddRequest request) {
        log.info("新增系统配置: configKey={}", request.getConfigKey());
        systemConfigService.addConfig(request);
        log.info("新增系统配置成功: configKey={}", request.getConfigKey());
        return ResponseDTO.success();
    }

    /**
     * 更新系统配置
     */
    @Operation(summary = "更新系统配置")
    @PutMapping("/{id}")
    public ResponseDTO<Void> update(@PathVariable Long id, @Valid @RequestBody SystemConfigAddRequest request) {
        log.info("更新系统配置: id={}", id);
        systemConfigService.updateConfig(id, request);
        log.info("更新系统配置成功: id={}", id);
        return ResponseDTO.success();
    }

    /**
     * 删除系统配置
     */
    @Operation(summary = "删除系统配置")
    @DeleteMapping("/{id}")
    public ResponseDTO<Void> delete(@PathVariable Long id) {
        log.info("删除系统配置: id={}", id);
        systemConfigService.deleteConfig(id);
        log.info("删除系统配置成功: id={}", id);
        return ResponseDTO.success();
    }
}
