package cn.muziseo.common.core.datatracer.annotation;

import java.lang.annotation.*;

/**
 * 数据变更记录：字典翻译
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataTracerFieldDict {
    String dictType();
}
