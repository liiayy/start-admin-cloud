package cn.muziseo.service.system.module.auth.controller;

import cn.muziseo.common.core.domain.dto.ResponseDTO;
import cn.muziseo.service.system.module.auth.controller.request.MenuAddRequest;
import cn.muziseo.service.system.module.auth.repository.entity.MenuEntity;
import cn.muziseo.service.system.module.auth.service.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单管理 Controller
 */
@Tag(name = "菜单管理")
@RestController
@RequestMapping("/system/auth/menu")
public class MenuController {

    @Resource
    private MenuService menuService;

    @Operation(summary = "新增菜单")
    @PostMapping("/add")
    public ResponseDTO<List<MenuEntity>> add(@Valid @RequestBody MenuAddRequest request) {
        return ResponseDTO.success(menuService.addMenu(request));
    }

    @Operation(summary = "获取当前用户菜单（示例）")
    @GetMapping("/list-by-role")
    public ResponseDTO<List<MenuEntity>> listByRoles(@RequestParam List<Long> roleIds) {
        return ResponseDTO.success(menuService.getMenusByRoleIds(roleIds));
    }

}
