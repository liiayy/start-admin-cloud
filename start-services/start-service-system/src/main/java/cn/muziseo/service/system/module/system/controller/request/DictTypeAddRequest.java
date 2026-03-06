package cn.muziseo.service.system.module.system.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 字典类型添加请求
 *
 * @author 木子软件
 * @Date 2026-02-27
 */
@Data
@Schema(description = "字典类型添加请求")
public class DictTypeAddRequest {

    @Schema(description = "字典类型编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "字典类型编码不能为空")
    private String code;

    @Schema(description = "字典类型名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "字典类型名称不能为空")
    private String name;

    @Schema(description = "显示顺序")
    private Integer sort;

    @Schema(description = "状态（0正常 1停用）")
    private Integer status;

    @Schema(description = "备注")
    private String remark;
}
