package cn.muziseo.service.system.module.organization.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.muziseo.common.core.domain.dto.ResponseDTO;
import cn.muziseo.service.system.module.organization.controller.request.DeptAddRequest;
import cn.muziseo.service.system.module.organization.controller.vo.DeptTreeVO;
import cn.muziseo.service.system.module.organization.controller.vo.DeptVO;
import cn.muziseo.service.system.module.organization.service.DeptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部门管理 Controller
 *
 * @author 木子软件
 */
@Tag(name = "部门管理")
@RestController
@Validated
@Slf4j
@RequestMapping("/organization/dept")
public class DeptController {

    @Resource
    private DeptService deptService;

    @Operation(summary = "获取部门列表")
    @GetMapping("/list")
    public ResponseDTO<List<DeptVO>> list() {
        return ResponseDTO.success(deptService.list());
    }

    @Operation(summary = "获取部门树")
    @GetMapping("/tree")
    public ResponseDTO<List<DeptTreeVO>> tree() {
        return ResponseDTO.success(deptService.tree());
    }

    @Operation(summary = "获取部门详情")
    @GetMapping("/{id}")
    public ResponseDTO<DeptVO> getById(@PathVariable Long id) {
        return ResponseDTO.success(deptService.getById(id));
    }

    @Operation(summary = "新增部门")
    @PostMapping("/add")
    @SaCheckPermission("system:dept:add")
    public ResponseDTO<Void> add(@Valid @RequestBody DeptAddRequest request) {
        deptService.addDept(request);
        return ResponseDTO.success();
    }

    @Operation(summary = "更新部门")
    @PutMapping("/{id}")
    @SaCheckPermission("system:dept:update")
    public ResponseDTO<Void> update(@PathVariable Long id, @Valid @RequestBody DeptAddRequest request) {
        deptService.updateDept(id, request);
        return ResponseDTO.success();
    }

    @Operation(summary = "删除部门")
    @DeleteMapping("/{id}")
    @SaCheckPermission("system:dept:delete")
    public ResponseDTO<Void> delete(@PathVariable Long id) {
        deptService.deleteDept(id);
        return ResponseDTO.success();
    }

    @Operation(summary = "更新部门状态")
    @PutMapping("/update-status")
    @SaCheckPermission("system:dept:update")
    public ResponseDTO<Void> updateStatus(@RequestParam Long id, @RequestParam Integer status) {
        deptService.updateStatus(id, status);
        return ResponseDTO.success();
    }
}
