package cn.muziseo.service.system.module.system.controller.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 字典类型 VO
 *
 * @author 木子软件
 */
@Data
@Builder
@Schema(description = "字典类型VO")
public class DictTypeVO {

    @Schema(description = "字典主键")
    private Long id;

    @Schema(description = "字典名称")
    private String name;

    @Schema(description = "字典类型")
    private String type;

    @Schema(description = "状态（0正常 1停用）")
    private Integer status;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
