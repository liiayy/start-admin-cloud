package cn.muziseo.service.system.module.auth.controller.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * 验证码返回 VO
 */
@Data
@Builder
@Schema(description = "验证码返回信息")
public class CaptchaVO {

    @Schema(description = "验证码唯一标识")
    private String uuid;

    @Schema(description = "验证码图片（Base64 编码）")
    private String img;

    @Schema(description = "是否开启验证码")
    private Boolean enabled;

}
