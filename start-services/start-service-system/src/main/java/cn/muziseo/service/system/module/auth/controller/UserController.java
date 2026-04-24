package cn.muziseo.service.system.module.auth.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.muziseo.common.core.domain.dto.ResponseDTO;
import cn.muziseo.common.log.annotation.Log;
import cn.muziseo.common.log.enums.BusinessType;
import cn.muziseo.common.db.page.PageResponse;
import cn.muziseo.common.excel.utils.ExcelUtil;
import cn.muziseo.service.system.module.auth.controller.request.*;
import cn.muziseo.service.system.module.auth.controller.vo.UserDetailVO;
import cn.muziseo.service.system.module.auth.controller.vo.UserExportVO;
import cn.muziseo.service.system.module.auth.controller.vo.UserImportVO;
import cn.muziseo.service.system.module.auth.controller.vo.UserVO;
import cn.muziseo.service.system.module.auth.service.UserService;
import cn.hutool.core.convert.Convert;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 用户管理 Controller
 *
 * @author 木子软件
 */
@Tag(name = "用户管理")
@RestController
@Slf4j
@RequestMapping("/admin/system/user")
public class UserController {

    @Resource
    private UserService userService;

    @Operation(summary = "分页查询用户")
    @GetMapping("/page")
    @SaCheckPermission("system:user:query")
    public ResponseDTO<PageResponse<UserVO>> page(UserPageRequest request) {
        return ResponseDTO.success(userService.pageUser(request));
    }

    @Operation(summary = "导出用户")
    @PostMapping("/export")
    @SaCheckPermission("system:user:export")
    @Log(title = "用户管理", businessType = BusinessType.EXPORT)
    public void export(UserPageRequest request, HttpServletResponse response) {
        List<UserVO> userVOList = userService.listUser(request);
        List<UserExportVO> list = Convert.toList(UserExportVO.class, userVOList);
        ExcelUtil.exportExcel(list, "用户数据", UserExportVO.class, response);
    }

    @Operation(summary = "导入用户")
    @PostMapping("/import")
    @SaCheckPermission("system:user:import")
    @Log(title = "用户管理", businessType = BusinessType.IMPORT)
    public ResponseDTO<String> importExcel(@RequestPart("file") MultipartFile file, boolean updateSupport) throws Exception {
        List<UserImportVO> userList = ExcelUtil.importExcel(file.getInputStream(), UserImportVO.class);
        return ResponseDTO.success(userService.importUsers(userList, updateSupport));
    }

    @Operation(summary = "下载导入模板")
    @PostMapping("/import-template")
    @SaCheckPermission("system:user:import")
    public void importTemplate(HttpServletResponse response) {
        ExcelUtil.exportExcel(List.of(), "用户导入模板", UserImportVO.class, response);
    }

    @Operation(summary = "获取用户详情")
    @GetMapping("/get")
    @SaCheckPermission("system:user:query")
    public ResponseDTO<UserVO> get(@RequestParam Long id) {
        return ResponseDTO.success(userService.getUser(id));
    }

    @Operation(summary = "创建用户")
    @PostMapping("/create")
    @SaCheckPermission("system:user:add")
    @Log(title = "用户管理", businessType = BusinessType.INSERT)
    public ResponseDTO<Void> create(@Valid @RequestBody UserAddRequest request) {
        log.info("创建用户: username={}", request.getUsername());
        userService.createUser(request);
        return ResponseDTO.success();
    }

    @Operation(summary = "更新用户")
    @PutMapping("/update")
    @SaCheckPermission("system:user:update")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    public ResponseDTO<Void> update(@Valid @RequestBody UserUpdateRequest request) {
        log.info("更新用户: id={}", request.getId());
        userService.updateUser(request);
        return ResponseDTO.success();
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("/delete")
    @SaCheckPermission("system:user:delete")
    @Log(title = "用户管理", businessType = BusinessType.DELETE)
    public ResponseDTO<Void> delete(@RequestParam Long id) {
        log.info("删除用户: id={}", id);
        userService.deleteUser(id);
        return ResponseDTO.success();
    }

    @Operation(summary = "更新用户状态")
    @PutMapping("/update-status")
    @SaCheckPermission("system:user:update")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    public ResponseDTO<Void> updateStatus(@Valid @RequestBody UserUpdateStatusRequest request) {
        log.info("更新用户状态: id={}, status={}", request.getId(), request.getStatus());
        userService.updateStatus(request);
        return ResponseDTO.success();
    }

    @Operation(summary = "重置密码（管理员）")
    @PutMapping("/reset-password")
    @SaCheckPermission("system:user:reset-password")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    public ResponseDTO<Void> resetPassword(@Valid @RequestBody UserResetPasswordRequest request) {
        log.info("重置用户密码: id={}", request.getId());
        userService.resetPassword(request);
        return ResponseDTO.success();
    }

    @Operation(summary = "修改密码（用户自己）")
    @PutMapping("/update-password")
    @SaCheckLogin
    public ResponseDTO<Void> updatePassword(@Valid @RequestBody UserUpdatePasswordRequest request) {
        userService.updatePassword(request);
        return ResponseDTO.success();
    }

    @Operation(summary = "分配用户角色")
    @PostMapping("/assign-role")
    @SaCheckPermission("system:user:assign-role")
    @Log(title = "用户管理", businessType = BusinessType.GRANT)
    public ResponseDTO<Void> assignRole(@Valid @RequestBody UserRoleAssignRequest request) {
        log.info("分配用户角色: userId={}", request.getUserId());
        userService.assignRole(request);
        return ResponseDTO.success();
    }
}
