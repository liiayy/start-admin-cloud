package cn.muziseo.common.core.datatracer.annotation;

import java.lang.annotation.*;

/**
 * 数据变更记录：枚举翻译
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataTracerFieldEnum {
    Class<? extends Enum<?>> enumClass();
}
