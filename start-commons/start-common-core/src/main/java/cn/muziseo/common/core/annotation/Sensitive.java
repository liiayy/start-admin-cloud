package cn.muziseo.common.core.annotation;
 
import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import cn.muziseo.common.core.utils.mask.SensitiveSerializer;
 
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
 
/**
 * 敏感数据脱敏注解
 * <p>
 * 标注在字段上，结合 Jackson 序列化实现自动脱敏。
 * 同时也支持手动通过 SensitiveUtils 进行处理。
 *
 * @author 木子软件
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@JacksonAnnotationsInside
@JsonSerialize(using = SensitiveSerializer.class)
public @interface Sensitive {
 
    /**
     * 脱敏策略
     */
    SensitiveStrategy value();
}
