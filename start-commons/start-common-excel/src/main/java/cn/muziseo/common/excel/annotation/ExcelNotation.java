package cn.muziseo.common.excel.annotation;

import java.lang.annotation.*;

/**
 * Excel 批注
 *
 * @author StartAdmin
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ExcelNotation {

    /**
     * 批注内容
     */
    String value() default "";

}
