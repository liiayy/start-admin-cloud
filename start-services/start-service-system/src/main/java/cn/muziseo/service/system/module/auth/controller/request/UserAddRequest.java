package cn.muziseo.service.system.module.auth.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 用户添加请求
 */
@Data
@Schema(description = "用户添加请求")
public class UserAddRequest {

    @Schema(description = "用户账号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "用户账号不能为空")
    private String username;

    @Schema(description = "密码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "密码不能为空")
    private String password;

    @Schema(description = "用户昵称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "用户昵称不能为空")
    private String nickname;

    @Schema(description = "手机号码")
    private String mobile;

    @Schema(description = "用户邮箱")
    private String email;

    @Schema(description = "部门ID")
    private Long deptId;

    @Schema(description = "岗位ID列表")
    private java.util.List<Long> postIds;

    @Schema(description = "状态（0正常 1停用）")
    private Integer status;

}
