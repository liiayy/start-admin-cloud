package cn.muziseo.common.core.serializer;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * 基于时间戳的 LocalDateTime 序列化器
 * 1. 默认情况下，序列化为毫秒时间戳
 * 2. 如果带有 @JsonFormat 注解，则按照 pattern 格式化为字符串
 *
 * @author 木子软件
 * @Date 2026-01-23
 * @Copyright <a href="https://code.muziseo.cn">木子软件</a>
 */
@Slf4j
@NoArgsConstructor
public class TimestampLocalDateTimeSerializer extends JsonSerializer<LocalDateTime> implements ContextualSerializer {

    public static final TimestampLocalDateTimeSerializer INSTANCE = new TimestampLocalDateTimeSerializer();

    /**
     * 持有当前上下文的格式化器
     */
    private DateTimeFormatter formatter;

    /**
     * 构造方法
     *
     * @param formatter 日期时间格式化器
     */
    private TimestampLocalDateTimeSerializer(DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    /**
     * 序列化方法
     *
     * @param value       LocalDateTime 值
     * @param gen         JsonGenerator
     * @param serializers SerializerProvider
     * @throws IOException IO异常
     */
    @Override
    public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeNull();
            return;
        }

        // 优先级 1：如果有 formatter（来自 @JsonFormat），则输出字符串
        if (formatter != null) {
            gen.writeString(formatter.format(value));
            return;
        }

        // 优先级 2：默认输出毫秒时间戳
        gen.writeNumber(value.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
    }

    /**
     * Jackson 提供的上下文回调，用于根据字段上的注解动态生成序列化器实例
     *
     * @param prov     SerializerProvider
     * @param property BeanProperty
     * @return JsonSerializer
     * @throws JsonMappingException Json映射异常
     */
    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
        if (property == null) {
            return INSTANCE;
        }

        // 获取字段上的 @JsonFormat 注解
        JsonFormat ann = property.getAnnotation(JsonFormat.class);
        if (ann == null) {
            // 如果字段上没有，尝试获取类上的
            ann = property.getContextAnnotation(JsonFormat.class);
        }

        if (ann != null && ann.pattern() != null && !ann.pattern().isEmpty()) {
            // 如果存在 pattern，创建一个带有 formatter 的新实例
            // 注意：这里返回的是新实例，不会修改 INSTANCE，保证了线程安全
            return new TimestampLocalDateTimeSerializer(DateTimeFormatter.ofPattern(ann.pattern()));
        }

        return INSTANCE;
    }
}
