package cn.muziseo.gateway.config.jackson;

import cn.muziseo.common.core.serializer.NumberSerializer;
import cn.muziseo.common.core.serializer.TimestampLocalDateTimeDeserializer;
import cn.muziseo.common.core.serializer.TimestampLocalDateTimeSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Jackson 自动配置类
 *
 * @author 木子软件
 * @Date 2026-01-23
 * @Copyright <a href="https://code.muziseo.cn">木子软件</a>
 */
@AutoConfiguration(after = JacksonAutoConfiguration.class) // 抢在标准配置前，确保定制生效
@Slf4j
@ConditionalOnClass(ObjectMapper.class) // 只有当ObjectMapper类存在时，才执行
public class StartJacksonAutoConfiguration {

    /**
     * 配置 Jackson 自定义序列化和反序列化规则
     *
     * @return Jackson2ObjectMapperBuilderCustomizer
     */
    @Bean
    @Primary
    public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
        return builder -> {
            // 1. 基础特性配置
            builder.failOnEmptyBeans(false); // 允许序列化空对象
            builder.failOnUnknownProperties(false); // 忽略 JSON 中多余但 Java 中没有的字段

            // 2. 序列化特性：排序字段，方便前端调试和查看
            builder.featuresToEnable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS);

            // 3. 核心：处理 Long 类型精度丢失（转为 String 返回给前端）
            builder.serializerByType(Long.class, NumberSerializer.INSTANCE);
            builder.serializerByType(Long.TYPE, NumberSerializer.INSTANCE);

            // 4. Java 8 时间日期处理
            // LocalDate / LocalTime 使用标准格式
            builder.serializerByType(LocalDate.class, LocalDateSerializer.INSTANCE);
            builder.deserializerByType(LocalDate.class, LocalDateDeserializer.INSTANCE);
            builder.serializerByType(LocalTime.class, LocalTimeSerializer.INSTANCE);
            builder.deserializerByType(LocalTime.class, LocalTimeDeserializer.INSTANCE);

            // LocalDateTime 特殊处理：统一转为时间戳（Epoch Millis）
            builder.serializerByType(LocalDateTime.class, TimestampLocalDateTimeSerializer.INSTANCE);
            builder.deserializerByType(LocalDateTime.class, TimestampLocalDateTimeDeserializer.INSTANCE);

            log.info("[start-common-web][Jackson 核心配置完成 - 已处理 Long 精度与 Java8 时间格式]");
        };
    }
}
