package cn.muziseo.service.system.module.permission.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.muziseo.common.core.domain.dto.ResponseDTO;
import cn.muziseo.service.system.module.permission.controller.request.MenuAddRequest;
import cn.muziseo.service.system.module.permission.controller.request.MenuUpdateRequest;
import cn.muziseo.service.system.module.permission.controller.vo.MenuTreeVO;
import cn.muziseo.service.system.module.permission.controller.vo.MenuVO;
import cn.muziseo.service.system.module.permission.repository.entity.MenuEntity;
import cn.muziseo.service.system.module.permission.service.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单管理 Controller
 *
 * @author 木子软件
 */
@Tag(name = "菜单管理")
@RestController
@Slf4j
@RequestMapping("/permission/menu")
public class MenuController {

    @Resource
    private MenuService menuService;

    @Operation(summary = "获取菜单树")
    @GetMapping("/tree")
    public ResponseDTO<List<MenuTreeVO>> tree() {
        return ResponseDTO.success(menuService.getMenuTree());
    }

    @Operation(summary = "获取菜单详情")
    @GetMapping("/get")
    public ResponseDTO<MenuVO> get(@RequestParam Long id) {
        return ResponseDTO.success(menuService.getMenu(id));
    }

    @Operation(summary = "新增菜单")
    @PostMapping("/add")
    public ResponseDTO<Void> add(@Valid @RequestBody MenuAddRequest request) {
        log.info("新增菜单: name={}, type={}", request.getName(), request.getType());
        menuService.addMenu(request);
        return ResponseDTO.success();
    }

    @Operation(summary = "修改菜单")
    @PutMapping("/update")
    public ResponseDTO<Void> update(@Valid @RequestBody MenuUpdateRequest request) {
        log.info("修改菜单: id={}", request.getId());
        menuService.updateMenu(request);
        return ResponseDTO.success();
    }

    @Operation(summary = "删除菜单")
    @DeleteMapping("/delete")
    public ResponseDTO<Void> delete(@RequestParam Long id) {
        log.info("删除菜单: id={}", id);
        menuService.deleteMenu(id);
        return ResponseDTO.success();
    }

    @Operation(summary = "获取当前用户菜单")
    @GetMapping("/list-by-role")
    public ResponseDTO<List<MenuEntity>> listByRoles(@RequestParam List<Long> roleIds) {
        log.debug("查询角色菜单: roleIds={}", roleIds);
        return ResponseDTO.success(menuService.getMenusByRoleIds(roleIds));
    }
}
