package cn.muziseo.service.system.enums;

import cn.muziseo.common.core.exception.errorCode.IErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 通知公告模块错误码（9xxxx）
 *
 * @author 木子软件
 */
@Getter
@AllArgsConstructor
public enum NoticeErrorCode implements IErrorCode {

    NOTICE_NOT_EXISTS(90001, "通知公告不存在"),
    NOTICE_STATUS_ERROR(90002, "仅正常状态下的公告支持发布"),
    ;

    private final int code;
    private final String message;
}
