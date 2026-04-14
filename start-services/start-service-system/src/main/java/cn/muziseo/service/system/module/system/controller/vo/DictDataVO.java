package cn.muziseo.service.system.module.system.controller.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 字典数据 VO
 *
 * @author 木子软件
 */
@Data
@Builder
@Schema(description = "字典数据VO")
public class DictDataVO {

    @Schema(description = "字典编码")
    private Long id;

    @Schema(description = "字典类型")
    private String dictType;

    @Schema(description = "字典标签")
    private String label;

    @Schema(description = "字典键值")
    private String value;

    @Schema(description = "字典排序")
    private Integer sort;

    @Schema(description = "状态（0正常 1停用）")
    private Integer status;

    @Schema(description = "颜色类型")
    private String colorType;

    @Schema(description = "CSS 样式")
    private String cssClass;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
