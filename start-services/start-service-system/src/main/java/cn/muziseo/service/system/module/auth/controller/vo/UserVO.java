package cn.muziseo.service.system.module.auth.controller.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@Schema(description = "用户信息VO")
public class UserVO {

    @Schema(description = "用户ID")
    private Long id;

    @Schema(description = "用户账号")
    private String username;

    @Schema(description = "用户昵称")
    private String nickname;

    @Schema(description = "部门ID")
    private Long deptId;

    @Schema(description = "部门名称")
    private String deptName;

    @Schema(description = "岗位ID数组")
    private List<Long> postIds;

    @Schema(description = "手机号码")
    private String mobile;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "用户性别")
    private Integer sex;

    @Schema(description = "用户头像")
    private String avatar;

    @Schema(description = "帐号状态（0正常 1停用）")
    private Integer status;

    @Schema(description = "最后登录IP")
    private String loginIp;

    @Schema(description = "最后登录时间")
    private LocalDateTime loginDate;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
