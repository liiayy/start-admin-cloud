package cn.muziseo.service.system.module.system.controller;

import cn.muziseo.common.core.domain.dto.ResponseDTO;
import cn.muziseo.service.system.module.system.controller.request.DictAddRequest;
import cn.muziseo.service.system.module.system.repository.entity.DictEntity;
import cn.muziseo.service.system.module.system.service.DictService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 字典数据管理 Controller
 *
 * @author 木子软件
 * @Date 2026-02-27
 */
@Tag(name = "字典数据管理")
@RestController
@Slf4j
@RequestMapping("/system/dict")
public class DictController {

    @Resource
    private DictService dictService;

    /**
     * 获取字典数据列表
     */
    @Operation(summary = "获取字典数据列表")
    @GetMapping("/list")
    public ResponseDTO<List<DictEntity>> list() {
        List<DictEntity> list = dictService.list();
        return ResponseDTO.success(list);
    }

    /**
     * 根据字典类型编码获取字典数据列表
     */
    @Operation(summary = "根据类型获取字典数据")
    @GetMapping("/list-by-type")
    public ResponseDTO<List<DictEntity>> listByDictTypeCode(@RequestParam String dictTypeCode) {
        List<DictEntity> list = dictService.listByDictTypeCode(dictTypeCode);
        return ResponseDTO.success(list);
    }

    /**
     * 根据ID获取字典数据
     */
    @Operation(summary = "获取字典数据详情")
    @GetMapping("/{id}")
    public ResponseDTO<DictEntity> getById(@PathVariable Long id) {
        DictEntity dict = dictService.getById(id);
        return ResponseDTO.success(dict);
    }

    /**
     * 新增字典数据
     */
    @Operation(summary = "新增字典数据")
    @PostMapping("/add")
    public ResponseDTO<Void> add(@Valid @RequestBody DictAddRequest request) {
        log.info("新增字典数据: dictTypeCode={}, label={}", request.getDictTypeCode(), request.getLabel());
        dictService.addDict(request);
        log.info("新增字典数据成功");
        return ResponseDTO.success();
    }

    /**
     * 更新字典数据
     */
    @Operation(summary = "更新字典数据")
    @PutMapping("/{id}")
    public ResponseDTO<Void> update(@PathVariable Long id, @Valid @RequestBody DictAddRequest request) {
        log.info("更新字典数据: id={}", id);
        dictService.updateDict(id, request);
        log.info("更新字典数据成功: id={}", id);
        return ResponseDTO.success();
    }

    /**
     * 删除字典数据
     */
    @Operation(summary = "删除字典数据")
    @DeleteMapping("/{id}")
    public ResponseDTO<Void> delete(@PathVariable Long id) {
        log.info("删除字典数据: id={}", id);
        dictService.deleteDict(id);
        log.info("删除字典数据成功: id={}", id);
        return ResponseDTO.success();
    }
}
