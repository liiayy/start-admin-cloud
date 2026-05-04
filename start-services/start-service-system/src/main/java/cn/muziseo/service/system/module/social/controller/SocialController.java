package cn.muziseo.service.system.module.social.controller;

import cn.muziseo.common.core.domain.dto.ResponseDTO;
import cn.muziseo.common.log.annotation.Log;
import cn.muziseo.common.log.enums.BusinessType;
import cn.muziseo.service.system.module.auth.controller.vo.LoginVO;
import cn.muziseo.service.system.module.social.service.SocialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

/**
 * 社交登录管理 Controller
 *
 * @author 木子软件
 */
@Tag(name = "社交登录")
@RestController
@RequestMapping("/admin/system/social")
public class SocialController {

    @Resource
    private SocialService socialService;

    @Operation(summary = "获取授权跳转地址")
    @GetMapping("/auth/{source}")
    public ResponseDTO<String> getAuthorizeUrl(@PathVariable String source) {
        return ResponseDTO.success(socialService.getAuthorizeUrl(source));
    }

    @Operation(summary = "社交登录回调")
    @PostMapping("/callback/{source}")
    public ResponseDTO<LoginVO> callback(
            @PathVariable String source,
            @RequestParam String code,
            @RequestParam String state) {
        return ResponseDTO.success(socialService.callback(source, code, state));
    }

    @Operation(summary = "绑定社交账号")
    @PostMapping("/bind/{source}")
    @Log(title = "社交登录", businessType = BusinessType.UPDATE)
    public ResponseDTO<Void> bind(
            @PathVariable String source,
            @RequestParam String code,
            @RequestParam String state) {
        socialService.bind(source, code, state);
        return ResponseDTO.success();
    }

    @Operation(summary = "解绑社交账号")
    @DeleteMapping("/unbind/{source}")
    @Log(title = "社交登录", businessType = BusinessType.DELETE)
    public ResponseDTO<Void> unbind(@PathVariable String source) {
        socialService.unbind(source);
        return ResponseDTO.success();
    }
}
