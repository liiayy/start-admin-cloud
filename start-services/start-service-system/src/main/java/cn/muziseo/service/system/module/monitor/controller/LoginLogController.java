package cn.muziseo.service.system.module.monitor.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.muziseo.common.core.domain.dto.ResponseDTO;
import cn.muziseo.common.db.page.PageResponse;
import cn.muziseo.common.log.annotation.Log;
import cn.muziseo.common.log.enums.BusinessType;
import cn.muziseo.service.system.module.monitor.repository.entity.LoginLogEntity;
import cn.muziseo.service.system.module.monitor.service.LoginLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 登录日志管理 Controller
 */
@Tag(name = "登录日志管理")
@RestController
@Validated
@RequestMapping("/admin/system/login-log")
@RequiredArgsConstructor
public class LoginLogController {

    private final LoginLogService loginLogService;

    @Operation(summary = "分页查询登录日志")
    @GetMapping("/page")
    @SaCheckPermission("monitor:loginlog:query")
    public ResponseDTO<PageResponse<LoginLogEntity>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            LoginLogEntity query) {
        return ResponseDTO.success(loginLogService.page(pageNum, pageSize, query));
    }

    @Operation(summary = "批量删除登录日志")
    @DeleteMapping("/{ids}")
    @SaCheckPermission("monitor:loginlog:remove")
    @Log(title = "登录日志", businessType = BusinessType.DELETE)
    public ResponseDTO<Void> delete(@PathVariable Long[] ids) {
        loginLogService.deleteByIds(ids);
        return ResponseDTO.success();
    }

    @Operation(summary = "清空登录日志")
    @PostMapping("/clean")
    @SaCheckPermission("monitor:loginlog:remove")
    @Log(title = "登录日志", businessType = BusinessType.CLEAN)
    public ResponseDTO<Void> clean() {
        loginLogService.clean();
        return ResponseDTO.success();
    }
}
