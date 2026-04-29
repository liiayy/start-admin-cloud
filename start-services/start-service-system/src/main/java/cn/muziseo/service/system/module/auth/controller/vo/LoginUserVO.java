package cn.muziseo.service.system.module.auth.controller.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

/**
 * 登录用户信息
 */
@Data
@Builder
@Schema(description = "登录用户信息")
public class LoginUserVO {

    @Schema(description = "用户ID")
    private Long id;

    @Schema(description = "用户账号")
    private String username;

    @Schema(description = "用户昵称")
    private String nickname;

    @Schema(description = "头像地址")
    private String avatar;

    @Schema(description = "邮箱地址")
    private String email;

    @Schema(description = "角色列表")
    private Set<String> roles;

    @Schema(description = "权限列表")
    private Set<String> permissions;

}
