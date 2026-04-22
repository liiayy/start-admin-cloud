package cn.muziseo.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * 通用状态枚举
 * <p>
 * 统一管理系统中的 0-正常/启用, 1-停用/禁用 逻辑。
 * </p>
 *
 * @author 木子软件
 */
@Getter
@AllArgsConstructor
public enum CommonStatus {

    /**
     * 正常 / 启用
     */
    NORMAL(0, "正常"),

    /**
     * 停用 / 禁用
     */
    DISABLE(1, "停用");

    private final int value;
    private final String description;

    /**
     * 判断状态是否为正常
     *
     * @param status 状态值
     * @return true 为正常
     */
    public static boolean isNormal(Integer status) {
        return Objects.equals(NORMAL.getValue(), status);
    }

    /**
     * 判断状态是否为停用
     *
     * @param status 状态值
     * @return true 为停用
     */
    public static boolean isDisable(Integer status) {
        return Objects.equals(DISABLE.getValue(), status);
    }
}
