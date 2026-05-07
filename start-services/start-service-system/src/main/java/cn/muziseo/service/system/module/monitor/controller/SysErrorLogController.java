package cn.muziseo.service.system.module.monitor.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.muziseo.common.core.domain.dto.ResponseDTO;
import cn.muziseo.common.db.page.PageResponse;
import cn.muziseo.common.core.event.ErrorLogEvent;
import cn.muziseo.common.log.annotation.Log;
import cn.muziseo.common.log.enums.BusinessType;
import cn.muziseo.service.system.module.monitor.api.ErrorLogApi;
import cn.muziseo.service.system.module.monitor.repository.entity.SysErrorLogEntity;
import cn.muziseo.service.system.module.monitor.service.SysErrorLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 错误日志管理 Controller
 */
@Tag(name = "错误日志管理")
@RestController
@Validated
@RequestMapping("/admin/system/error-log")
@RequiredArgsConstructor
public class SysErrorLogController implements ErrorLogApi {

    private final SysErrorLogService errorLogService;

    @Override
    public void saveErrorLog(ErrorLogEvent errorLog) {
        errorLogService.saveErrorLog(errorLog);
    }

    @Operation(summary = "分页查询错误日志")
    @GetMapping("/page")
    @SaCheckPermission("monitor:errorlog:query")
    public ResponseDTO<PageResponse<SysErrorLogEntity>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            SysErrorLogEntity query) {
        return ResponseDTO.success(errorLogService.page(pageNum, pageSize, query));
    }

    @Operation(summary = "获取错误日志详情")
    @GetMapping("/{id}")
    @SaCheckPermission("monitor:errorlog:query")
    public ResponseDTO<SysErrorLogEntity> get(@PathVariable Long id) {
        return ResponseDTO.success(errorLogService.getById(id));
    }

    @Operation(summary = "批量删除错误日志")
    @DeleteMapping("/{ids}")
    @SaCheckPermission("monitor:errorlog:remove")
    @Log(title = "错误日志", businessType = BusinessType.DELETE)
    public ResponseDTO<Void> delete(@PathVariable Long[] ids) {
        errorLogService.deleteByIds(ids);
        return ResponseDTO.success();
    }

    @Operation(summary = "清空错误日志")
    @PostMapping("/clean")
    @SaCheckPermission("monitor:errorlog:remove")
    @Log(title = "错误日志", businessType = BusinessType.CLEAN)
    public ResponseDTO<Void> clean() {
        errorLogService.clean();
        return ResponseDTO.success();
    }

    @Operation(summary = "更新错误处理状态")
    @PutMapping("/update-status")
    @SaCheckPermission("monitor:errorlog:update")
    @Log(title = "错误日志", businessType = BusinessType.UPDATE)
    public ResponseDTO<Void> updateStatus(@RequestParam Long id, @RequestParam Integer status, @RequestParam(required = false) String remark) {
        errorLogService.updateHandleStatus(id, status, remark);
        return ResponseDTO.success();
    }

    @Operation(summary = "模拟产生一个异常（测试用）")
    @GetMapping("/test-error")
    public ResponseDTO<Void> triggerTestError() {
        throw new RuntimeException("这是一条由人工触发的模拟异常测试消息，用于验证错误日志监控模块是否正常上报！");
    }
}
