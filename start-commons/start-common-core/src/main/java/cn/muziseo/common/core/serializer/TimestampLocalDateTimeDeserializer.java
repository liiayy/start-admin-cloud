package cn.muziseo.common.core.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * LocalDateTime 反序列化器
 * 支持多种输入格式：时间戳（毫秒）、字符串（yyyy-MM-dd HH:mm:ss / ISO）
 *
 * @author 木子软件
 * @Date 2026-01-23
 * @Copyright <a href="https://code.muziseo.cn">木子软件</a>
 */
public class TimestampLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

    public static final TimestampLocalDateTimeDeserializer INSTANCE = new TimestampLocalDateTimeDeserializer();

    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (p.currentToken() == JsonToken.VALUE_NUMBER_INT) {
            // 时间戳（毫秒）
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(p.getValueAsLong()), ZoneId.systemDefault());
        }
        if (p.currentToken() == JsonToken.VALUE_STRING) {
            String text = p.getText().trim();
            if (text.isEmpty()) {
                return null;
            }
            // 尝试默认格式
            try {
                return LocalDateTime.parse(text, DEFAULT_FORMATTER);
            }
            catch (Exception ignored) {
            }
            // 尝试 ISO 格式（带 T）
            try {
                return LocalDateTime.parse(text);
            }
            catch (Exception ignored) {
            }
            throw new IOException("无法解析日期时间: " + text);
        }
        // 数组格式 [2026, 4, 13, 10, 30, 0]
        if (p.currentToken() == JsonToken.START_ARRAY) {
            p.nextToken(); // skip START_ARRAY
            int year = p.nextIntValue(0);
            int month = p.nextIntValue(0);
            int day = p.nextIntValue(0);
            int hour = p.nextIntValue(0);
            int minute = p.nextIntValue(0);
            int second = p.nextIntValue(0);
            p.nextToken(); // skip END_ARRAY
            return LocalDateTime.of(year, month, day, hour, minute, second);
        }
        throw new IOException("无法解析日期时间: " + p.currentToken());
    }

}
