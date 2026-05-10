package cn.muziseo.service.system.module.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.muziseo.common.core.domain.dto.ResponseDTO;
import cn.muziseo.common.log.annotation.Log;
import cn.muziseo.common.log.enums.BusinessType;
import cn.muziseo.common.db.page.PageResponse;
import cn.muziseo.service.system.module.system.controller.request.DictTypeCreateRequest;
import cn.muziseo.service.system.module.system.controller.request.DictTypePageRequest;
import cn.muziseo.service.system.module.system.controller.vo.DictTypeVO;
import cn.muziseo.service.system.module.system.service.DictTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
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
@Validated
@Slf4j
@RequestMapping("/admin/system/dict-type")
public class DictTypeController {

    @Resource
    private DictTypeService dictTypeService;

    @Operation(summary = "获取字典类型列表")
    @SaCheckPermission("system:dict:query")
    @GetMapping("/list")
    public ResponseDTO<List<DictTypeVO>> list() {
        return ResponseDTO.success(dictTypeService.list());
    }

    @Operation(summary = "分页查询字典类型")
    @SaCheckPermission("system:dict:query")
    @GetMapping("/page")
    public ResponseDTO<PageResponse<DictTypeVO>> page(DictTypePageRequest request) {
        return ResponseDTO.success(dictTypeService.pageDictType(request));
    }

    @Operation(summary = "获取字典类型详情")
    @SaCheckPermission("system:dict:query")
    @GetMapping("/get")
    public ResponseDTO<DictTypeVO> get(@RequestParam Long id) {
        return ResponseDTO.success(dictTypeService.getDictTypeById(id));
    }

    @Operation(summary = "新增字典类型")
    @SaCheckPermission("system:dict:create")
    @PostMapping("/create")
    @Log(title = "字典类型", businessType = BusinessType.INSERT)
    public ResponseDTO<Void> create(@Valid @RequestBody DictTypeCreateRequest request) {
        dictTypeService.createDictType(request);
        return ResponseDTO.success();
    }

    @Operation(summary = "更新字典类型")
    @SaCheckPermission("system:dict:update")
    @PutMapping("/update")
    @Log(title = "字典类型", businessType = BusinessType.UPDATE)
    public ResponseDTO<Void> update(@RequestParam Long id, @Valid @RequestBody DictTypeCreateRequest request) {
        dictTypeService.updateDictType(id, request);
        return ResponseDTO.success();
    }

    @Operation(summary = "删除字典类型")
    @SaCheckPermission("system:dict:delete")
    @DeleteMapping("/delete")
    @Log(title = "字典类型", businessType = BusinessType.DELETE)
    public ResponseDTO<Void> delete(@RequestParam Long id) {
        dictTypeService.deleteDictType(id);
        return ResponseDTO.success();
    }
}
