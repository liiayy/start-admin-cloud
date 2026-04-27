package cn.muziseo.common.web.core.dict;
 
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.muziseo.common.cache.annotation.Dict;
import cn.muziseo.common.cache.dict.DictUtils;
import cn.muziseo.common.core.domain.dto.ResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
 
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
 
/**
 * 字典翻译响应切面
 * <p>
 * 自动扫描响应结果中的 @Dict 注解并进行翻译填充。
 *
 * @author 木子软件
 */
@Slf4j
@RestControllerAdvice
public class DictTranslationAdvice implements ResponseBodyAdvice<Object> {
 
    /** 缓存类字段信息，提升性能 */
    private static final Map<Class<?>, Field[]> FIELD_CACHE = new ConcurrentHashMap<>();
 
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // 仅处理返回类型为 ResponseDTO 的接口
        return ResponseDTO.class.isAssignableFrom(returnType.getParameterType());
    }
 
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        if (body instanceof ResponseDTO<?> responseDTO) {
            Object data = responseDTO.getData();
            if (data != null) {
                translateData(data);
            }
        }
        return body;
    }
 
    private void translateData(Object data) {
        if (data instanceof Collection<?> collection) {
            collection.forEach(this::translateObject);
        } else {
            translateObject(data);
        }
    }
 
    private void translateObject(Object obj) {
        if (obj == null) return;
        
        Class<?> clazz = obj.getClass();
        Field[] fields = FIELD_CACHE.computeIfAbsent(clazz, k -> ReflectUtil.getFields(k));
 
        for (Field field : fields) {
            Dict dictAnno = field.getAnnotation(Dict.class);
            if (dictAnno != null) {
                try {
                    // 1. 获取原值
                    Object value = ReflectUtil.getFieldValue(obj, field);
                    if (value == null) continue;
 
                    // 2. 执行翻译
                    String label = DictUtils.getDictLabel(dictAnno.type(), String.valueOf(value));
 
                    // 3. 填充目标字段
                    String targetFieldName = dictAnno.target();
                    if (StrUtil.isBlank(targetFieldName)) {
                        targetFieldName = field.getName() + "Label";
                    }
                    
                    // 检查目标字段是否存在
                    if (ReflectUtil.hasField(clazz, targetFieldName)) {
                        ReflectUtil.setFieldValue(obj, targetFieldName, label);
                    }
                } catch (Exception e) {
                    log.error("字典翻译失败: field={}", field.getName(), e);
                }
            }
        }
    }
}
