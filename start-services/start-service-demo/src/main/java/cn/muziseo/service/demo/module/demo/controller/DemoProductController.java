package cn.muziseo.service.demo.module.demo.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.muziseo.common.core.domain.dto.ResponseDTO;
import cn.muziseo.common.db.page.PageResponse;
import cn.muziseo.common.log.annotation.Log;
import cn.muziseo.common.log.enums.BusinessType;
import cn.muziseo.service.demo.module.demo.controller.request.DemoAddRequest;
import cn.muziseo.service.demo.module.demo.controller.request.DemoPageRequest;
import cn.muziseo.service.demo.module.demo.controller.vo.DemoVO;
import cn.muziseo.service.demo.module.demo.service.DemoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 演示产品管理 Controller
 *
 * @author Antigravity
 */
@Tag(name = "产品演示管理")
@RestController
@Validated
@RequestMapping("/admin/demo/product")
public class DemoProductController {

    @Resource
    private DemoService demoService;

    @Operation(summary = "获取产品列表（分页）")
    @GetMapping("/page")
    @SaCheckPermission("demo:product:query")
    public ResponseDTO<PageResponse<DemoVO>> page(DemoPageRequest request) {
        return ResponseDTO.success(demoService.page(request));
    }

    @Operation(summary = "获取产品详情")
    @GetMapping("/{id}")
    @SaCheckPermission("demo:product:query")
    public ResponseDTO<DemoVO> getById(@PathVariable Long id) {
        return ResponseDTO.success(demoService.getById(id));
    }

    @Operation(summary = "新增产品")
    @PostMapping("/create")
    @SaCheckPermission("demo:product:create")
    @Log(title = "产品演示管理", businessType = BusinessType.INSERT)
    public ResponseDTO<Void> create(@Valid @RequestBody DemoAddRequest request) {
        demoService.create(request);
        return ResponseDTO.success();
    }

    @Operation(summary = "更新产品")
    @PutMapping("/{id}")
    @SaCheckPermission("demo:product:update")
    @Log(title = "产品演示管理", businessType = BusinessType.UPDATE)
    public ResponseDTO<Void> update(@PathVariable Long id, @Valid @RequestBody DemoAddRequest request) {
        demoService.update(id, request);
        return ResponseDTO.success();
    }

    @Operation(summary = "删除产品")
    @DeleteMapping("/{id}")
    @SaCheckPermission("demo:product:delete")
    @Log(title = "产品演示管理", businessType = BusinessType.DELETE)
    public ResponseDTO<Void> delete(@PathVariable Long id) {
        demoService.delete(id);
        return ResponseDTO.success();
    }
}
