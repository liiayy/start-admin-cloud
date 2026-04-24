package cn.muziseo.common.excel.annotation;

import org.apache.poi.ss.usermodel.IndexedColors;

import java.lang.annotation.*;

/**
 * Excel 必填
 *
 * @author StartAdmin
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ExcelRequired {

    /**
     * 字体颜色
     */
    short fontColor() default 10; // IndexedColors.RED.getIndex() = 10

}
