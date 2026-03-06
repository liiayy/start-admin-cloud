package cn.muziseo.service.system.module.system.controller;

import cn.muziseo.common.core.domain.dto.ResponseDTO;
import cn.muziseo.service.system.module.system.controller.request.DictTypeAddRequest;
import cn.muziseo.service.system.module.system.repository.entity.DictTypeEntity;
import cn.muziseo.service.system.module.system.service.DictTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 字典类型管理 Controller
 *
 * @author 木子软件
 * @Date 2026-02-27
 */
@Tag(name = "字典类型管理")
@RestController
@Slf4j
@RequestMapping("/system/dict-type")
public class DictTypeController {

    @Resource
    private DictTypeService dictTypeService;

    /**
     * 获取字典类型列表
     */
    @Operation(summary = "获取字典类型列表")
    @GetMapping("/list")
    public ResponseDTO<List<DictTypeEntity>> list() {
        List<DictTypeEntity> list = dictTypeService.list();
        return ResponseDTO.success(list);
    }

    /**
     * 根据ID获取字典类型
     */
    @Operation(summary = "获取字典类型详情")
    @GetMapping("/{id}")
    public ResponseDTO<DictTypeEntity> getById(@PathVariable Long id) {
        DictTypeEntity dictType = dictTypeService.getById(id);
        return ResponseDTO.success(dictType);
    }

    /**
     * 新增字典类型
     */
    @Operation(summary = "新增字典类型")
    @PostMapping("/add")
    public ResponseDTO<Void> add(@Valid @RequestBody DictTypeAddRequest request) {
        log.info("新增字典类型: code={}, name={}", request.getCode(), request.getName());
        dictTypeService.addDictType(request);
        log.info("新增字典类型成功: code={}", request.getCode());
        return ResponseDTO.success();
    }

    /**
     * 更新字典类型
     */
    @Operation(summary = "更新字典类型")
    @PutMapping("/{id}")
    public ResponseDTO<Void> update(@PathVariable Long id, @Valid @RequestBody DictTypeAddRequest request) {
        log.info("更新字典类型: id={}", id);
        dictTypeService.updateDictType(id, request);
        log.info("更新字典类型成功: id={}", id);
        return ResponseDTO.success();
    }

    /**
     * 删除字典类型
     */
    @Operation(summary = "删除字典类型")
    @DeleteMapping("/{id}")
    public ResponseDTO<Void> delete(@PathVariable Long id) {
        log.info("删除字典类型: id={}", id);
        dictTypeService.deleteDictType(id);
        log.info("删除字典类型成功: id={}", id);
        return ResponseDTO.success();
    }
}
