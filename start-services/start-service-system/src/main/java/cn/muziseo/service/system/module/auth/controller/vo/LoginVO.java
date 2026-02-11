package cn.muziseo.service.system.module.auth.controller.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "登录信息")
public class LoginVO {
    @Schema(description = "用户ID")
    private Long id;

    @Schema(description = "用户账号")
    private String username;

    @Schema(description = "用户昵称")
    private String nickname;

    @Schema(description = "头像地址")
    private String avatar;

    @Schema(description = "Token名称")
    private String tokenName;

    @Schema(description = "Token值")
    private String tokenValue;
}
