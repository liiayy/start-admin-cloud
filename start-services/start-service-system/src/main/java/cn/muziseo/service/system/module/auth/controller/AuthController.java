package cn.muziseo.service.system.module.auth.controller;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.muziseo.common.core.domain.dto.ResponseDTO;
import cn.muziseo.common.core.exception.BusinessException;
import cn.muziseo.service.system.module.auth.controller.request.LoginRequest;
import cn.muziseo.service.system.module.auth.controller.vo.LoginUserVO;
import cn.muziseo.service.system.module.auth.repository.entity.MenuEntity;
import cn.muziseo.service.system.module.auth.repository.entity.RoleEntity;
import cn.muziseo.service.system.module.auth.repository.entity.UserEntity;
import cn.muziseo.service.system.module.auth.service.AuthService;
import cn.muziseo.service.system.module.auth.service.MenuService;
import cn.muziseo.service.system.module.auth.service.RoleService;
import cn.muziseo.service.system.module.auth.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 认证管理 Controller
 */
@Tag(name = "认证管理")
@RestController
@Validated
@Slf4j
@RequestMapping("/auth")
public class AuthController {

    @Resource
    private AuthService authService;

    @Resource
    private UserService userService;

    @Resource
    private RoleService roleService;

    @Resource
    private MenuService menuService;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public ResponseDTO<SaTokenInfo> login(@Valid @RequestBody LoginRequest request) {
        return ResponseDTO.success(authService.login(request));
    }

    @Operation(summary = "用户退出")
    @PostMapping("/logout")
    public ResponseDTO<Void> logout() {
        authService.logout();
        return ResponseDTO.success();
    }

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/get-info")
    public ResponseDTO<LoginUserVO> getInfo() {
        // 1. 获取当前用户ID
        Long userId = StpUtil.getLoginIdAsLong();
        UserEntity user = userService.getUserById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 2. 获取角色列表
        List<RoleEntity> roles = roleService.getRolesByUserId(userId);
        Set<String> roleCodes = roles.stream().map(RoleEntity::getCode).collect(Collectors.toSet());
        List<Long> roleIds = roles.stream().map(RoleEntity::getId).collect(Collectors.toList());

        // 3. 获取权限列表
        Set<String> permissions = Set.of();
        if (!roleIds.isEmpty()) {
            List<MenuEntity> menus = menuService.getMenusByRoleIds(roleIds);
            permissions = menus.stream()
                    .map(MenuEntity::getPermission)
                    .filter(p -> p != null && !p.isEmpty())
                    .collect(Collectors.toSet());
        }

        // 4. 构建返回结果
        LoginUserVO vo = LoginUserVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .roles(roleCodes)
                .permissions(permissions)
                .build();

        return ResponseDTO.success(vo);
    }
}
