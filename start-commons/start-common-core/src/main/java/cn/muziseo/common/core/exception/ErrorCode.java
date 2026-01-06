package cn.muziseo.common.core.exception;


import lombok.Getter;

@Getter
public enum ErrorCode {

    INTERNAL_SERVER_ERROR(500000, "系统内部错误"),
    COMMON_ERROR_CODE(500002, "系统内部错误"),
    PARAM_VALID_ERROR(400001, "参数校验失败");




    private final int code;
    private final String message;


    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

}
