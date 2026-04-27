package cn.muziseo.common.cache.annotation;
 
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
 
/**
 * 字典翻译注解
 * <p>
 * 标注在 DTO 字段上，由 DictTranslationAdvice 自动处理。
 *
 * @author 木子软件
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Dict {
 
    /**
     * 字典类型（对应 sys_dict_type 中的 dict_type 字段）
     */
    String type();
 
    /**
     * 翻译结果填充的目标字段名。
     * 如果不指定，则默认为：当前字段名 + "Label"。
     * 例如：sex -> sexLabel
     */
    String target() default "";
}
