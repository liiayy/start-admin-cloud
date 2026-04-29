package cn.muziseo.service.system.module.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.muziseo.common.core.domain.dto.ResponseDTO;
import cn.muziseo.common.log.annotation.Log;
import cn.muziseo.common.log.enums.BusinessType;
import cn.muziseo.service.system.module.system.controller.request.SysOssConfigAddRequest;
import cn.muziseo.service.system.module.system.controller.vo.SysOssConfigVO;
import cn.muziseo.service.system.module.system.convert.SysOssConfigConverter;
import cn.muziseo.service.system.module.system.repository.entity.SysOssConfigEntity;
import cn.muziseo.service.system.module.system.service.SysOssConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * OSS 存储配置 Controller
 *
 * @author 木子软件
 */
@Tag(name = "OSS配置管理")
@RestController
@Validated
@Slf4j
@RequestMapping("/admin/system/oss-config")
public class SysOssConfigController {

    @Resource
    private SysOssConfigService sysOssConfigService;

    @Resource
    private SysOssConfigConverter sysOssConfigConverter;

    @Operation(summary = "查询所有配置")
    @GetMapping("/list")
    @SaCheckPermission("system:oss:config")
    public ResponseDTO<List<SysOssConfigVO>> list() {
        List<SysOssConfigEntity> entities = sysOssConfigService.listAll();
        return ResponseDTO.success(entities.stream().map(sysOssConfigConverter::toVO).toList());
    }

    @Operation(summary = "保存/修改配置")
    @PostMapping("/save")
    @SaCheckPermission("system:oss:config:add")
    @Log(title = "OSS配置", businessType = BusinessType.UPDATE)
    public ResponseDTO<Void> save(@Valid @RequestBody SysOssConfigAddRequest request) {
        SysOssConfigEntity entity = sysOssConfigConverter.toEntity(request);
        sysOssConfigService.saveConfig(entity);
        return ResponseDTO.success();
    }

    @Operation(summary = "删除配置")
    @DeleteMapping("/delete")
    @SaCheckPermission("system:oss:config:delete")
    @Log(title = "OSS配置", businessType = BusinessType.DELETE)
    public ResponseDTO<Void> delete(@RequestParam Long id) {
        sysOssConfigService.deleteConfig(id);
        return ResponseDTO.success();
    }

    @Operation(summary = "刷新客户端配置")
    @PostMapping("/reload")
    @SaCheckPermission("system:oss:config:update")
    @Log(title = "OSS配置", businessType = BusinessType.OTHER)
    public ResponseDTO<Void> reload(@RequestParam String configKey) {
        sysOssConfigService.reloadClient(configKey);
        return ResponseDTO.success();
    }

    @Operation(summary = "测试配置是否可用")
    @PostMapping("/test")
    @SaCheckPermission("system:oss:config:add")
    public ResponseDTO<Void> test(@RequestBody SysOssConfigAddRequest request) {
        SysOssConfigEntity entity = sysOssConfigConverter.toEntity(request);
        sysOssConfigService.testConfig(entity);
        return ResponseDTO.success();
    }
}
