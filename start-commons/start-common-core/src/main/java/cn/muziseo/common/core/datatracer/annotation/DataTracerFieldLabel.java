package cn.muziseo.common.core.datatracer.annotation;

import java.lang.annotation.*;

/**
 * 数据变更记录：字段名称标签
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataTracerFieldLabel {
    String value();
}
