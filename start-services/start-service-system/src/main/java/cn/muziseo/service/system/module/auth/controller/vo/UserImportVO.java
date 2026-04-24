package cn.muziseo.service.system.module.auth.controller.vo;

import cn.idev.excel.annotation.ExcelProperty;
import cn.muziseo.common.core.constant.DictConstants;
import cn.muziseo.common.excel.annotation.ExcelDictFormat;
import cn.muziseo.common.excel.annotation.ExcelRequired;
import cn.muziseo.common.excel.convert.ExcelDictConvert;
import jakarta.validation.constraints.NotBlank;
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
