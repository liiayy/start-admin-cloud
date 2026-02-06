package cn.muziseo.service.system.module.auth.controller;

import cn.muziseo.common.core.domain.dto.ResponseDTO;
import cn.muziseo.service.system.module.auth.controller.request.MenuAddRequest;
import cn.muziseo.service.system.module.auth.repository.entity.MenuEntity;
import cn.muziseo.service.system.module.auth.service.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单管理 Controller
 * <p>
 * 提供菜单的增删改查、分配权限等功能
 *
 * @author 木子软件
 * @Date 2026-01-07
 * @Wechat liiayy
 * @Email 773582348@qq.com
 * @Copyright <a href="https://code.muziseo.cn">木子软件</a>
 */
@Tag(name = "菜单管理")
@RestController
@Slf4j
@RequestMapping("/system/auth/menu")
public class MenuController {

    @Resource
    private MenuService menuService;

    /**
     * 新增菜单
     * <p>
     * 创建新的菜单项，支持创建目录、菜单、按钮等类型
     *
     * @param request 菜单新增请求参数
     * @return 新增后的菜单列表
     */
    @Operation(summary = "新增菜单")
    @PostMapping("/add")
    public ResponseDTO<List<MenuEntity>> add(@Valid @RequestBody MenuAddRequest request) {
        log.info("新增菜单: title={}, type={}", request.getName(), request.getType());
        List<MenuEntity> menuList = menuService.addMenu(request);
        log.info("新增菜单成功: title={}, menuCount={}", request.getName(), menuList.size());
        return ResponseDTO.success(menuList);
    }

    /**
     * 根据角色ID列表获取菜单
     * <p>
     * 查询指定角色拥有的菜单列表，用于前端动态渲染菜单
     *
     * @param roleIds 角色 ID 列表
     * @return 菜单列表
     */
    @Operation(summary = "获取当前用户菜单（示例）")
    @GetMapping("/list-by-role")
    public ResponseDTO<List<MenuEntity>> listByRoles(@RequestParam List<Long> roleIds) {
        log.debug("查询角色菜单: roleIds={}", roleIds);
        List<MenuEntity> menuList = menuService.getMenusByRoleIds(roleIds);
        log.info("查询角色菜单成功: roleIds={}, menuCount={}", roleIds, menuList.size());
        return ResponseDTO.success(menuList);
    }

}
