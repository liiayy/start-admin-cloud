package cn.muziseo.service.system.module.permission.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.muziseo.common.core.domain.dto.ResponseDTO;
import cn.muziseo.common.db.page.PageResponse;
import cn.muziseo.service.system.module.permission.controller.request.RoleAddRequest;
import cn.muziseo.service.system.module.permission.controller.request.RolePageRequest;
import cn.muziseo.service.system.module.permission.controller.request.RoleUpdateRequest;
import cn.muziseo.service.system.module.permission.controller.request.RoleUpdateStatusRequest;
import cn.muziseo.service.system.module.permission.controller.vo.RoleVO;
import cn.muziseo.service.system.module.permission.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理 Controller
 *
 * @author 木子软件
 */
@Tag(name = "角色管理")
@RestController
@Slf4j
@RequestMapping("/permission/role")
public class RoleController {

    @Resource
    private RoleService roleService;

    @Operation(summary = "分页查询角色")
    @GetMapping("/page")
    @SaCheckPermission("system:role:query")
    public ResponseDTO<PageResponse<RoleVO>> page(RolePageRequest request) {
        return ResponseDTO.success(roleService.pageRole(request));
    }

    @Operation(summary = "获取角色详情")
    @GetMapping("/get")
    @SaCheckPermission("system:role:query")
    public ResponseDTO<RoleVO> get(@RequestParam Long id) {
        return ResponseDTO.success(roleService.getRole(id));
    }

    @Operation(summary = "新增角色")
    @PostMapping("/add")
    @SaCheckPermission("system:role:add")
    public ResponseDTO<Void> add(@Valid @RequestBody RoleAddRequest request) {
        log.info("新增角色: code={}, name={}", request.getCode(), request.getName());
        roleService.addRole(request);
        return ResponseDTO.success();
    }

    @Operation(summary = "修改角色")
    @PutMapping("/update")
    @SaCheckPermission("system:role:update")
    public ResponseDTO<Void> update(@Valid @RequestBody RoleUpdateRequest request) {
        log.info("修改角色: id={}", request.getId());
        roleService.updateRole(request);
        return ResponseDTO.success();
    }

    @Operation(summary = "删除角色")
    @DeleteMapping("/delete")
    @SaCheckPermission("system:role:delete")
    public ResponseDTO<Void> delete(@RequestParam Long id) {
        log.info("删除角色: id={}", id);
        roleService.deleteRole(id);
        return ResponseDTO.success();
    }

    @Operation(summary = "更新角色状态")
    @PutMapping("/update-status")
    @SaCheckPermission("system:role:update")
    public ResponseDTO<Void> updateStatus(@Valid @RequestBody RoleUpdateStatusRequest request) {
        log.info("更新角色状态: id={}, status={}", request.getId(), request.getStatus());
        roleService.updateStatus(request.getId(), request.getStatus());
        return ResponseDTO.success();
    }

    @Operation(summary = "分配菜单")
    @PostMapping("/assign-menus")
    @SaCheckPermission("system:role:update")
    public ResponseDTO<Void> assignMenus(@RequestParam Long roleId, @RequestBody List<Long> menuIds) {
        log.info("分配角色菜单: roleId={}, menuCount={}", roleId, menuIds.size());
        roleService.assignMenus(roleId, menuIds);
        return ResponseDTO.success();
    }
}
