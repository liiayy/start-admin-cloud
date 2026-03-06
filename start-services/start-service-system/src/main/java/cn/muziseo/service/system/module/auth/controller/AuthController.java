package cn.muziseo.service.system.module.auth.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.muziseo.common.core.domain.dto.ResponseDTO;
import cn.muziseo.common.core.exception.BusinessException;
import cn.muziseo.service.system.module.auth.controller.request.LoginRequest;
import cn.muziseo.service.system.module.auth.controller.vo.LoginUserVO;
import cn.muziseo.service.system.module.auth.controller.vo.LoginVO;
import cn.muziseo.service.system.module.auth.repository.entity.UserEntity;
import cn.muziseo.service.system.module.auth.service.AuthService;
import cn.muziseo.service.system.module.auth.service.UserService;
import cn.muziseo.service.system.module.permission.repository.entity.MenuEntity;
import cn.muziseo.service.system.module.permission.repository.entity.RoleEntity;
import cn.muziseo.service.system.module.permission.service.MenuService;
import cn.muziseo.service.system.module.permission.service.RoleService;
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
 * <p>
 * 提供用户登录、登出、获取当前用户信息等认证相关功能
 *
 * @author 木子软件
 * @Date 2026-01-07
 * @Wechat liiayy
 * @Email 773582348@qq.com
 * @Copyright <a href="https://code.muziseo.cn">木子软件</a>
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

    /**
     * 用户登录
     * <p>
     * 验证用户账号密码，成功后返回 Token 信息
     *
     * @param request 登录请求参数（包含账号、密码、验证码等）
     * @return Token 信息（包含 token、tokenName 等）
     */
    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public ResponseDTO<LoginVO> login(@Valid @RequestBody LoginRequest request) {
        log.info("用户登录: username={}", request.getUsername());
        LoginVO tokenInfo = authService.login(request);
        log.info("用户登录成功: username={}, tokenName={}", request.getUsername(), tokenInfo.getTokenName());
        return ResponseDTO.success(tokenInfo);
    }

    /**
     * 用户退出
     * <p>
     * 退出当前登录用户，清除 Token 信息
     *
     * @return 空
     */
    @Operation(summary = "用户退出")
    @PostMapping("/logout")
    public ResponseDTO<Void> logout() {
        authService.logout();
        return ResponseDTO.success();
    }

    /**
     * 获取当前用户信息
     * <p>
     * 获取当前登录用户的基本信息、角色列表和权限列表
     *
     * @return 当前用户信息（包含用户基本信息、角色、权限等）
     */
    @Operation(summary = "获取当前用户信息")
    @GetMapping("/get-info")
    public ResponseDTO<LoginUserVO> getInfo() {
        // 1. 获取当前用户ID
        Long userId = StpUtil.getLoginIdAsLong();
        UserEntity user = userService.getUserById(userId);
        if (user == null) {
            log.warn("用户不存在: userId={}", userId);
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
