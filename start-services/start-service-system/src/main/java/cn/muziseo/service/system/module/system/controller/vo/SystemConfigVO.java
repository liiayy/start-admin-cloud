package cn.muziseo.service.system.module.system.controller.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统参数 VO
 *
 * @author 木子软件
 */
@Data
@Builder
@Schema(description = "系统参数VO")
public class SystemConfigVO {

    @Schema(description = "参数主键")
    private Long id;

    @Schema(description = "参数名称")
    private String name;

    @Schema(description = "参数键名")
    private String configKey;

    @Schema(description = "参数键值")
    private String configValue;

    @Schema(description = "是否系统内置")
    private Boolean builtin;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
