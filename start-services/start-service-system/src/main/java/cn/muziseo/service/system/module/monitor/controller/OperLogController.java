package cn.muziseo.service.system.module.monitor.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.muziseo.common.core.domain.dto.ResponseDTO;
import cn.muziseo.common.db.page.PageResponse;
import cn.muziseo.common.log.annotation.Log;
import cn.muziseo.common.log.enums.BusinessType;
import cn.muziseo.service.system.module.monitor.repository.entity.OperLogEntity;
import cn.muziseo.service.system.module.monitor.service.OperLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 操作日志管理 Controller
 */
@Tag(name = "操作日志管理")
@RestController
@Validated
@RequestMapping("/admin/system/oper-log")
@RequiredArgsConstructor
public class OperLogController {

    private final OperLogService operLogService;

    @Operation(summary = "分页查询操作日志")
    @GetMapping("/page")
    @SaCheckPermission("monitor:operlog:query")
    public ResponseDTO<PageResponse<OperLogEntity>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            OperLogEntity query) {
        return ResponseDTO.success(operLogService.page(pageNum, pageSize, query));
    }

    @Operation(summary = "获取操作日志详情")
    @GetMapping("/{id}")
    @SaCheckPermission("monitor:operlog:query")
    public ResponseDTO<OperLogEntity> get(@PathVariable Long id) {
        return ResponseDTO.success(operLogService.getById(id));
    }

    @Operation(summary = "批量删除操作日志")
    @DeleteMapping("/{ids}")
    @SaCheckPermission("monitor:operlog:remove")
    @Log(title = "操作日志", businessType = BusinessType.DELETE)
    public ResponseDTO<Void> delete(@PathVariable Long[] ids) {
        operLogService.deleteByIds(ids);
        return ResponseDTO.success();
    }

    @Operation(summary = "清空操作日志")
    @PostMapping("/clean")
    @SaCheckPermission("monitor:operlog:remove")
    @Log(title = "操作日志", businessType = BusinessType.CLEAN)
    public ResponseDTO<Void> clean() {
        operLogService.clean();
        return ResponseDTO.success();
    }
}
