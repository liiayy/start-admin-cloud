package cn.muziseo.service.system.module.auth.controller;

import cn.muziseo.common.core.domain.dto.ResponseDTO;
import cn.muziseo.service.system.module.auth.controller.request.UserAddRequest;
import cn.muziseo.service.system.module.auth.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户管理 Controller
 */
@Tag(name = "用户管理")
@RestController
@RequestMapping("/system/auth/user")
public class UserController {

    @Resource
    private UserService userService;

    @Operation(summary = "新增用户")
    @PostMapping("/add")
    public ResponseDTO<Void> add(@Valid @RequestBody UserAddRequest request) {
        userService.addUser(request);
        return ResponseDTO.success();
    }

}
