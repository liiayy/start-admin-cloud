package cn.muziseo.common.oss.enums;

import com.mybatisflex.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * OSS 桶访问权限策略
 *
 * @author 木子软件
 */
@Getter
@AllArgsConstructor
public enum AccessPolicyType {

    /**
     * 私有 (Private)
     */
    PRIVATE(0),

    /**
     * 公共读 (PublicRead)
     */
    PUBLIC_READ(1),

    /**
     * 公共读写 (PublicReadWrite)
     */
    PUBLIC_READ_WRITE(2);

    @EnumValue
    @com.fasterxml.jackson.annotation.JsonValue
    private final int type;

}
