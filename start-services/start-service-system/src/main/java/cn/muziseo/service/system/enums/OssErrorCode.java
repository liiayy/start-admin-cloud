package cn.muziseo.service.system.enums;


import cn.muziseo.common.core.exception.errorCode.IErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * OSS 模块错误码
 *
 * @author 木子软件
 */
@Getter
@AllArgsConstructor
public enum OssErrorCode implements IErrorCode {

    OSS_CONFIG_NOT_EXISTS(40001, "对象存储配置不存在"),
    OSS_CONFIG_KEY_EXISTS(40002, "对象存储配置键已存在"),
    OSS_CLIENT_NOT_FOUND(40003, "未找到有效的存储客户端"),
    UPLOAD_FILE_EMPTY(40004, "上传文件不能为空"),
    FILE_NOT_EXISTS(40005, "文件记录不存在"),
    FILE_DELETE_FAILED(40006, "文件物理删除失败"),
    UPLOAD_MODULE_INVALID(40007, "非法模块名");

    private final int code;
    private final String message;

}
