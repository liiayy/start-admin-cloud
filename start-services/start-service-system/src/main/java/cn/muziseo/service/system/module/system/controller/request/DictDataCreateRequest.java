package cn.muziseo.service.system.module.system.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 字典数据添加请求
 *
 * @author 木子软件
 * @Date 2026-02-27
 */
@Data
@Schema(description = "字典数据添加请求")
public class DictDataCreateRequest {

    @Schema(description = "字典类型", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "字典类型不能为空")
    private String dictType;

    @Schema(description = "字典标签", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "字典标签不能为空")
    private String label;

    @Schema(description = "字典键值", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "字典键值不能为空")
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
}
