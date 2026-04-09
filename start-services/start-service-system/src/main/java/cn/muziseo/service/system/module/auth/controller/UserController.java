package cn.muziseo.service.system.module.auth.controller;

import cn.muziseo.common.core.domain.dto.ResponseDTO;
import cn.muziseo.common.db.page.PageResponse;
import cn.muziseo.service.system.module.auth.controller.request.*;
import cn.muziseo.service.system.module.auth.controller.vo.UserDetailVO;
import cn.muziseo.service.system.module.auth.controller.vo.UserVO;
import cn.muziseo.service.system.module.auth.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理 Controller
 *
 * @author 木子软件
 */
@Tag(name = "用户管理")
@RestController
@Slf4j
@RequestMapping("/auth/user")
public class UserController {

    @Resource
    private UserService userService;

    @Operation(summary = "分页查询用户")
    @GetMapping("/page")
    public ResponseDTO<PageResponse<UserVO>> page(UserPageRequest request) {
        return ResponseDTO.success(userService.pageUser(request));
    }

    @Operation(summary = "获取用户详情")
    @GetMapping("/get")
    public ResponseDTO<UserVO> get(@RequestParam Long id) {
        return ResponseDTO.success(userService.getUser(id));
    }

    @Operation(summary = "创建用户")
    @PostMapping("/create")
    public ResponseDTO<Void> create(@Valid @RequestBody UserAddRequest request) {
        log.info("创建用户: username={}", request.getUsername());
        userService.createUser(request);
        return ResponseDTO.success();
    }

    @Operation(summary = "更新用户")
    @PutMapping("/update")
    public ResponseDTO<Void> update(@Valid @RequestBody UserUpdateRequest request) {
        log.info("更新用户: id={}", request.getId());
        userService.updateUser(request);
        return ResponseDTO.success();
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("/delete")
    public ResponseDTO<Void> delete(@RequestParam Long id) {
        log.info("删除用户: id={}", id);
        userService.deleteUser(id);
        return ResponseDTO.success();
    }

    @Operation(summary = "更新用户状态")
    @PutMapping("/update-status")
    public ResponseDTO<Void> updateStatus(@Valid @RequestBody UserUpdateStatusRequest request) {
        log.info("更新用户状态: id={}, status={}", request.getId(), request.getStatus());
        userService.updateStatus(request);
        return ResponseDTO.success();
    }

    @Operation(summary = "重置密码（管理员）")
    @PutMapping("/reset-password")
    public ResponseDTO<Void> resetPassword(@Valid @RequestBody UserResetPasswordRequest request) {
        log.info("重置用户密码: id={}", request.getId());
        userService.resetPassword(request);
        return ResponseDTO.success();
    }

    @Operation(summary = "修改密码（用户自己）")
    @PutMapping("/update-password")
    public ResponseDTO<Void> updatePassword(@Valid @RequestBody UserUpdatePasswordRequest request) {
        userService.updatePassword(request);
        return ResponseDTO.success();
    }

    @Operation(summary = "分配用户角色")
    @PostMapping("/assign-role")
    public ResponseDTO<Void> assignRole(@Valid @RequestBody UserRoleAssignRequest request) {
        log.info("分配用户角色: userId={}", request.getUserId());
        userService.assignRole(request);
        return ResponseDTO.success();
    }
}
