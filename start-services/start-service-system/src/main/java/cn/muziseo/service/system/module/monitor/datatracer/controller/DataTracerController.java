package cn.muziseo.service.system.module.monitor.datatracer.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.muziseo.common.core.domain.dto.ResponseDTO;
import cn.muziseo.common.db.page.PageResponse;
import cn.muziseo.service.system.module.monitor.datatracer.repository.entity.DataTracerEntity;
import cn.muziseo.service.system.module.monitor.datatracer.service.DataTracerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 数据变更记录 Controller
 */
@Tag(name = "数据变更记录管理")
@RestController
@Validated
@RequestMapping("/admin/system/data-tracer")
@RequiredArgsConstructor
public class DataTracerController {

    private final DataTracerService dataTracerService;

    @Operation(summary = "根据业务ID和类型分页查询变更记录")
    @GetMapping("/page")
    @SaCheckLogin
    public ResponseDTO<PageResponse<cn.muziseo.service.system.module.monitor.datatracer.controller.vo.DataTracerVO>> page(
            cn.muziseo.service.system.module.monitor.datatracer.controller.request.DataTracerPageRequest request) {
        return ResponseDTO.success(dataTracerService.pageTracer(request));
    }

    @Operation(summary = "批量删除变更记录")
    @DeleteMapping("/{ids}")
    @cn.dev33.satoken.annotation.SaCheckPermission("system:datatracer:remove")
    public ResponseDTO<Void> delete(@PathVariable java.util.List<Long> ids) {
        dataTracerService.deleteByIds(ids);
        return ResponseDTO.success();
    }

    @Operation(summary = "清空变更记录")
    @DeleteMapping("/clean")
    @cn.dev33.satoken.annotation.SaCheckPermission("system:datatracer:remove")
    public ResponseDTO<Void> clean() {
        dataTracerService.clean();
        return ResponseDTO.success();
    }
}
