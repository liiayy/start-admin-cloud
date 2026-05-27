package cn.muziseo.service.demo.module.demo.controller.vo;

import cn.muziseo.common.cache.annotation.Dict;
import cn.muziseo.common.core.constant.DictConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 演示字典与系统参数视图对象
 *
 * @author Antigravity
 */
@Data
@Schema(description = "演示字典与系统参数视图对象")
public class DemoDictVO {

    @Schema(description = "用户性别 (1-男 2-女 3-未知)")
    @Dict(type = DictConstants.SYS_USER_SEX)
    private Integer sex;

    @Schema(description = "用户性别标签 (自动填充)")
    private String sexLabel;

    @Schema(description = "启用状态 (0-正常 1-停用)")
    @Dict(type = DictConstants.SYS_STATUS, target = "statusName")
    private String status;

    @Schema(description = "启用状态描述 (自定义目标字段填充)")
    private String statusName;

    @Schema(description = "验证码开关系统配置")
    private Boolean captchaEnabled;

    @Schema(description = "系统用户默认密码配置")
    private String defaultPassword;

    @Schema(description = "手动使用DictUtils翻译的性别标签")
    private String manualSexLabel;
}
