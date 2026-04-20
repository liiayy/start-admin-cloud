package cn.muziseo.common.log.enums;

import lombok.Getter;

/**
 * 操作人类别
 */
@Getter
public enum OperatorType {
    /** 其它 */
    OTHER(0),
    /** 后台用户 */
    MANAGE(1),
    /** 手机端用户 */
    MOBILE(2);

    private final int value;

    OperatorType(int value) {
        this.value = value;
    }
}
