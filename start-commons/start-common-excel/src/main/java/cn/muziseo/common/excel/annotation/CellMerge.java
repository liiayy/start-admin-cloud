package cn.muziseo.common.excel.annotation;

import java.lang.annotation.*;

/**
 * Excel 单元格合并
 *
 * @author StartAdmin
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface CellMerge {

    /**
     * 列索引 (从0开始)
     */
    int index() default -1;

    /**
     * 合并依据 (不指定则依据当前字段)
     */
    String[] mergeBy() default {};

}
