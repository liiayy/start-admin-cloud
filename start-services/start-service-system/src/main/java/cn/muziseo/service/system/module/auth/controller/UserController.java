package cn.muziseo.service.system.module.auth.controller;

import cn.muziseo.common.core.domain.dto.ResponseDTO;
import cn.muziseo.service.system.module.auth.controller.request.UserAddRequest;
import cn.muziseo.service.system.module.auth.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户管理 Controller
 * <p>
 * 提供用户的增删改查、分配角色、重置密码等功能
 *
 * @author 木子软件
 * @Date 2026-01-07
 * @Wechat liiayy
 * @Email 773582348@qq.com
 * @Copyright <a href="https://code.muziseo.cn">木子软件</a>
 */
@Tag(name = "用户管理")
@RestController
@Slf4j
@RequestMapping("/system/auth/user")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 新增用户
     * <p>
     * 创建新的用户账号，系统会自动生成初始密码
     *
     * @param request 用户新增请求参数
     * @return 空
     */
    @Operation(summary = "新增用户")
    @PostMapping("/add")
    public ResponseDTO<Void> add(@Valid @RequestBody UserAddRequest request) {
        log.info("新增用户: username={}", request.getUsername());
        userService.addUser(request);
        log.info("新增用户成功: username={}", request.getUsername());
        return ResponseDTO.success();
    }

}
