package cn.muziseo.service.system.module.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.muziseo.common.cache.config.ConfigUtils;
import cn.muziseo.common.core.domain.dto.ResponseDTO;
import cn.muziseo.common.db.page.PageResponse;
import cn.muziseo.service.system.module.system.controller.request.SystemConfigAddRequest;
import cn.muziseo.service.system.module.system.controller.request.SystemConfigPageRequest;
import cn.muziseo.service.system.module.system.controller.vo.SystemConfigVO;
import cn.muziseo.service.system.module.system.service.SystemConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 系统参数管理 Controller
 *
 * @author 木子软件
 * @Date 2026-02-27
 */
@Tag(name = "系统参数管理")
@RestController
@Slf4j
@RequestMapping("/system/config")
public class SystemConfigController {

    @Resource
    private SystemConfigService systemConfigService;

    @Operation(summary = "分页查询系统参数")
    @SaCheckPermission("system:config:query")
    @GetMapping("/page")
    public ResponseDTO<PageResponse<SystemConfigVO>> page(SystemConfigPageRequest request) {
        return ResponseDTO.success(systemConfigService.pageConfig(request));
    }

    @Operation(summary = "获取系统参数详情")
    @SaCheckPermission("system:config:query")
    @GetMapping("/get")
    public ResponseDTO<SystemConfigVO> get(@RequestParam Long id) {
        return ResponseDTO.success(systemConfigService.getConfigById(id));
    }

    @Operation(summary = "根据键名获取参数值")
    @GetMapping("/value")
    public ResponseDTO<String> getConfigValue(@RequestParam String configKey) {
        String value = ConfigUtils.getString(configKey);
        return ResponseDTO.success("操作成功", value);
    }

    @Operation(summary = "批量获取系统参数值")
    @GetMapping("/list-values")
    public ResponseDTO<java.util.Map<String, String>> getConfigValues(@RequestParam java.util.List<String> configKeys) {
        return ResponseDTO.success(systemConfigService.getConfigValues(configKeys));
    }

    @Operation(summary = "新增系统参数")
    @SaCheckPermission("system:config:add")
    @PostMapping("/add")
    public ResponseDTO<Void> add(@Valid @RequestBody SystemConfigAddRequest request) {
        systemConfigService.addConfig(request);
        return ResponseDTO.success();
    }

    @Operation(summary = "更新系统参数")
    @SaCheckPermission("system:config:update")
    @PutMapping("/update")
    public ResponseDTO<Void> update(@RequestParam Long id, @Valid @RequestBody SystemConfigAddRequest request) {
        systemConfigService.updateConfig(id, request);
        return ResponseDTO.success();
    }

    @Operation(summary = "删除系统参数")
    @SaCheckPermission("system:config:delete")
    @DeleteMapping("/delete")
    public ResponseDTO<Void> delete(@RequestParam Long id) {
        systemConfigService.deleteConfig(id);
        return ResponseDTO.success();
    }
}
