package cn.muziseo.common.core.utils.mask;
 
import cn.muziseo.common.core.annotation.Sensitive;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
 
import java.io.IOException;
import java.util.Objects;
 
/**
 * 基于 Jackson 的脱敏序列化器
 */
public class SensitiveSerializer extends JsonSerializer<String> implements ContextualSerializer {
 
    private Sensitive sensitive;
 
    public SensitiveSerializer() {
    }
 
    public SensitiveSerializer(Sensitive sensitive) {
        this.sensitive = sensitive;
    }
 
    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (Objects.isNull(sensitive)) {
            gen.writeString(value);
            return;
        }
        gen.writeString(sensitive.value().getDesensitizer().apply(value));
    }
 
    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
        if (property != null) {
            Sensitive annotation = property.getAnnotation(Sensitive.class);
            if (annotation != null) {
                return new SensitiveSerializer(annotation);
            }
        }
        return this;
    }
}
