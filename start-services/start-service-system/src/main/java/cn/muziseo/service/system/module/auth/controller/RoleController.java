package cn.muziseo.service.system.module.auth.controller;

import cn.muziseo.common.core.domain.dto.ResponseDTO;
import cn.muziseo.service.system.module.auth.controller.request.RoleAddRequest;
import cn.muziseo.service.system.module.auth.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理 Controller
 */
@Tag(name = "角色管理")
@RestController
@RequestMapping("/system/auth/role")
public class RoleController {

    @Resource
    private RoleService roleService;

    @Operation(summary = "新增角色")
    @PostMapping("/add")
    public ResponseDTO<Void> add(@Valid @RequestBody RoleAddRequest request) {
        roleService.addRole(request);
        return ResponseDTO.success();
    }

    @Operation(summary = "分配菜单")
    @PostMapping("/assign-menus")
    public ResponseDTO<Void> assignMenus(@RequestParam Long roleId, @RequestBody List<Long> menuIds) {
        roleService.assignMenus(roleId, menuIds);
        return ResponseDTO.success();
    }

}
