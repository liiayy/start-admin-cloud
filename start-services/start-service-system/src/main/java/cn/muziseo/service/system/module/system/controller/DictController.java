package cn.muziseo.service.system.module.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.muziseo.common.core.domain.dto.ResponseDTO;
import cn.muziseo.common.db.page.PageResponse;
import cn.muziseo.service.system.module.system.controller.request.DictDataAddRequest;
import cn.muziseo.service.system.module.system.controller.request.DictDataPageRequest;
import cn.muziseo.service.system.module.system.controller.vo.DictDataVO;
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
@RequestMapping("/system/dict-data")
public class DictController {

    @Resource
    private DictService dictService;

    @Operation(summary = "根据类型获取字典数据列表")
    @GetMapping("/list-by-type")
    public ResponseDTO<List<DictDataVO>> listByType(@RequestParam String dictType) {
        return ResponseDTO.success(dictService.listByDictType(dictType));
    }

    @Operation(summary = "分页查询字典数据")
    @GetMapping("/page")
    public ResponseDTO<PageResponse<DictDataVO>> page(DictDataPageRequest request) {
        return ResponseDTO.success(dictService.pageDictData(request));
    }

    @Operation(summary = "获取字典数据详情")
    @GetMapping("/get")
    public ResponseDTO<DictDataVO> get(@RequestParam Long id) {
        return ResponseDTO.success(dictService.getDictDataById(id));
    }

    @Operation(summary = "新增字典数据")
    @SaCheckPermission("system:dict:add")
    @PostMapping("/add")
    public ResponseDTO<Void> add(@Valid @RequestBody DictDataAddRequest request) {
        dictService.addDictData(request);
        return ResponseDTO.success();
    }

    @Operation(summary = "更新字典数据")
    @SaCheckPermission("system:dict:update")
    @PutMapping("/update")
    public ResponseDTO<Void> update(@RequestParam Long id, @Valid @RequestBody DictDataAddRequest request) {
        dictService.updateDictData(id, request);
        return ResponseDTO.success();
    }

    @Operation(summary = "删除字典数据")
    @SaCheckPermission("system:dict:delete")
    @DeleteMapping("/delete")
    public ResponseDTO<Void> delete(@RequestParam Long id) {
        dictService.deleteDictData(id);
        return ResponseDTO.success();
    }
}
