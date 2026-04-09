package cn.muziseo.service.system.module.auth.controller.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
@Builder
@Schema(description = "用户详情VO")
public class UserDetailVO {

    @Schema(description = "用户基本信息")
    private UserVO user;

    @Schema(description = "角色编码列表")
    private Set<String> roles;

    @Schema(description = "权限标识列表")
    private Set<String> permissions;
}
