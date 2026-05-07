package cn.muziseo.service.system.module.auth.controller.vo;

import cn.idev.excel.annotation.ExcelProperty;
import cn.muziseo.common.core.constant.DictConstants;
import cn.muziseo.common.excel.annotation.ExcelDictFormat;
import cn.muziseo.common.excel.annotation.ExcelRequired;
import cn.muziseo.common.excel.convert.ExcelDictConvert;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户导入 VO
 *
 * @author StartAdmin
 */
@Data
public class UserImportVO {

    @ExcelProperty(value = "用户账号")
    @ExcelRequired
    @NotBlank(message = "用户账号不能为空")
    @Size(min = 4, max = 20, message = "用户账号长度必须在 4 到 20 个字符之间")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户账号只能包含字母、数字和下划线")
    private String username;

    @ExcelProperty(value = "用户昵称")
    @ExcelRequired
    @NotBlank(message = "用户昵称不能为空")
    private String nickname;

    @ExcelProperty(value = "部门名称")
    private String deptName;

    @ExcelProperty(value = "手机号码")
    private String mobile;

    @ExcelProperty(value = "邮箱")
    private String email;

    @ExcelProperty(value = "用户性别", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = DictConstants.SYS_USER_SEX)
    private Integer sex;

    @ExcelProperty(value = "帐号状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = DictConstants.SYS_STATUS)
    private Integer status;

}
