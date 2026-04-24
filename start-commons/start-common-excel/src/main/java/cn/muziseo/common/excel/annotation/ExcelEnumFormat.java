package cn.muziseo.common.excel.annotation;

import java.lang.annotation.*;

/**
 * 枚举格式化
 *
 * @author StartAdmin
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ExcelEnumFormat {

    /**
     * 枚举类
     */
    Class<? extends Enum<?>> enumClass();

    /**
     * 枚举值对应的字段名
     */
    String valueField() default "value";

    /**
     * 枚举标签对应的字段名
     */
    String labelField() default "label";

}
