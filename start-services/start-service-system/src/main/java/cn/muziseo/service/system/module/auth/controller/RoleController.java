package cn.muziseo.service.system.module.auth.controller;

import cn.muziseo.common.core.domain.dto.ResponseDTO;
import cn.muziseo.service.system.module.auth.controller.request.RoleAddRequest;
import cn.muziseo.service.system.module.auth.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理 Controller
 * <p>
 * 提供角色的增删改查、分配菜单等功能
 *
 * @author 木子软件
 * @Date 2026-01-07
 * @Wechat liiayy
 * @Email 773582348@qq.com
 * @Copyright <a href="https://code.muziseo.cn">木子软件</a>
 */
@Tag(name = "角色管理")
@RestController
@Slf4j
@RequestMapping("/auth/role")
public class RoleController {

    @Resource
    private RoleService roleService;

    /**
     * 新增角色
     * <p>
     * 创建新的角色，用于用户权限管理
     *
     * @param request 角色新增请求参数
     * @return 空
     */
    @Operation(summary = "新增角色")
    @PostMapping("/add")
    public ResponseDTO<Void> add(@Valid @RequestBody RoleAddRequest request) {
        log.info("新增角色: code={}, name={}", request.getCode(), request.getName());
        roleService.addRole(request);
        log.info("新增角色成功: code={}", request.getCode());
        return ResponseDTO.success();
    }

    /**
     * 为角色分配菜单
     * <p>
     * 将指定菜单分配给角色，控制角色可访问的菜单和权限
     *
     * @param roleId  角色 ID
     * @param menuIds 菜单 ID 列表
     * @return 空
     */
    @Operation(summary = "分配菜单")
    @PostMapping("/assign-menus")
    public ResponseDTO<Void> assignMenus(@RequestParam Long roleId, @RequestBody List<Long> menuIds) {
        log.info("为角色分配菜单: roleId={}, menuCount={}", roleId, menuIds.size());
        roleService.assignMenus(roleId, menuIds);
        log.info("为角色分配菜单成功: roleId={}", roleId);
        return ResponseDTO.success();
    }

}
