package cn.muziseo.service.system.enums;

import cn.muziseo.common.core.exception.errorCode.IErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 字典模块错误码（7xxxx）
 *
 * @author 木子软件
 */
@Getter
@AllArgsConstructor
public enum DictErrorCode implements IErrorCode {

    DICT_TYPE_NOT_EXISTS(10106001, "字典类型不存在"),
    DICT_TYPE_CODE_EXISTS(10106002, "字典类型编码已存在"),
    DICT_TYPE_HAS_DATA(10106003, "该字典类型下存在字典数据，无法删除"),
    DICT_DATA_NOT_EXISTS(10106004, "字典数据不存在"),
    ;

    private final int code;
    private final String message;
}
