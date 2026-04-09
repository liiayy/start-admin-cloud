package cn.muziseo.service.system.module.auth.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "用户更新请求")
public class UserUpdateRequest {

    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "用户ID不能为空")
    private Long id;

    @Schema(description = "用户昵称")
    private String nickname;

    @Schema(description = "部门ID")
    private Long deptId;

    @Schema(description = "岗位ID数组")
    private java.util.List<Long> postIds;

    @Schema(description = "手机号码")
    private String mobile;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "用户性别")
    private Integer sex;

    @Schema(description = "用户头像")
    private String avatar;

    @Schema(description = "备注")
    private String remark;
}
