package cn.muziseo.common.core.utils.json;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.muziseo.common.core.utils.spring.SpringUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * JSON 工具类
 * <p>
 * 基于 Jackson ObjectMapper 实现的 JSON 序列化和反序列化工具类
 * 提供对象与 JSON 字符串之间的转换、JSON 路径解析、类型转换等功能
 * </p>
 *
 * @author 木子软件:李彦军
 * @Date 2026-01-15
 * @Wechat liiayy
 * @Email 773582348@qq.com
 * @Copyright <a href="https://code.muziseo.cn">木子软件</a>
 */
@Slf4j
public class JsonUtils {

    public static ObjectMapper objectMapper() {
        return SpringUtils.getBean(ObjectMapper.class);
    }

    /**
     * 将对象序列化为 JSON 字符串
     *
     * @param object 对象
     * @return JSON 字符串
     */
    @SneakyThrows
    public static String toJsonString(Object object) {
        return objectMapper().writeValueAsString(object);
    }

    /**
     * 将对象序列化为 JSON 字节数组
     *
     * @param object 对象
     * @return JSON 字节数组
     */
    @SneakyThrows
    public static byte[] toJsonByte(Object object) {
        return objectMapper().writeValueAsBytes(object);
    }

    /**
     * 将对象序列化为格式化的 JSON 字符串（美化输出）
     *
     * @param object 对象
     * @return 格式化的 JSON 字符串
     */
    @SneakyThrows
    public static String toJsonPrettyString(Object object) {
        return objectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(object);
    }

    /**
     * 将 JSON 字符串解析为指定类型的对象
     *
     * @param text  JSON 字符串
     * @param clazz 目标类型
     * @return 指定类型的对象
     */
    public static <T> T parseObject(String text, Class<T> clazz) {
        if (StrUtil.isEmpty(text)) {
            return null;
        }
        try {
            return objectMapper().readValue(text, clazz);
        } catch (IOException e) {
            log.error("json parse err,json:{}", text, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 从 JSON 字符串中按路径提取并解析为指定类型的对象
     *
     * @param text  JSON 字符串
     * @param path  JSON 路径表达式（如：user.name）
     * @param clazz 目标类型
     * @return 指定类型的对象
     */
    public static <T> T parseObject(String text, String path, Class<T> clazz) {
        if (StrUtil.isEmpty(text)) {
            return null;
        }
        try {
            JsonNode treeNode = objectMapper().readTree(text);
            JsonNode pathNode = treeNode.path(path);
            return objectMapper().readValue(pathNode.toString(), clazz);
        } catch (IOException e) {
            log.error("json parse err,json:{}", text, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 将 JSON 字符串解析为指定类型的对象（支持泛型）
     *
     * @param text JSON 字符串
     * @param type 目标类型
     * @return 指定类型的对象
     */
    public static <T> T parseObject(String text, Type type) {
        if (StrUtil.isEmpty(text)) {
            return null;
        }
        try {
            return objectMapper().readValue(text, objectMapper().getTypeFactory().constructType(type));
        } catch (IOException e) {
            log.error("json parse err,json:{}", text, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 将 JSON 字节数组解析为指定类型的对象（支持泛型）
     *
     * @param text JSON 字节数组
     * @param type 目标类型
     * @return 指定类型的对象
     */
    public static <T> T parseObject(byte[] text, Type type) {
        if (ArrayUtil.isEmpty(text)) {
            return null;
        }
        try {
            return objectMapper().readValue(text, objectMapper().getTypeFactory().constructType(type));
        } catch (IOException e) {
            log.error("json parse err,json:{}", text, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 将字符串解析成指定类型的对象
     * 使用 {@link #parseObject(String, Class)} 时，在@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS) 的场景下，
     * 如果 text 没有 class 属性，则会报错。此时，使用这个方法，可以解决。
     *
     * @param text  字符串
     * @param clazz 类型
     * @return 对象
     */
    public static <T> T parseObject2(String text, Class<T> clazz) {
        if (StrUtil.isEmpty(text)) {
            return null;
        }
        return JSONUtil.toBean(text, clazz);
    }

    /**
     * 将 JSON 字节数组解析为指定类型的对象
     *
     * @param bytes JSON 字节数组
     * @param clazz 目标类型
     * @return 指定类型的对象
     */
    public static <T> T parseObject(byte[] bytes, Class<T> clazz) {
        if (ArrayUtil.isEmpty(bytes)) {
            return null;
        }
        try {
            return objectMapper().readValue(bytes, clazz);
        } catch (IOException e) {
            log.error("json parse err,json:{}", bytes, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 将 JSON 字符串解析为指定类型的对象（使用 TypeReference）
     *
     * @param text          JSON 字符串
     * @param typeReference 类型引用
     * @return 指定类型的对象
     */
    public static <T> T parseObject(String text, TypeReference<T> typeReference) {
        try {
            return objectMapper().readValue(text, typeReference);
        } catch (IOException e) {
            log.error("json parse err,json:{}", text, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 解析 JSON 字符串成指定类型的对象，如果解析失败，则返回 null
     *
     * @param text          字符串
     * @param typeReference 类型引用
     * @return 指定类型的对象
     */
    public static <T> T parseObjectQuietly(String text, TypeReference<T> typeReference) {
        try {
            return objectMapper().readValue(text, typeReference);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 将 JSON 字符串解析为指定类型的列表
     *
     * @param text  JSON 字符串
     * @param clazz 列表元素类型
     * @return 指定类型的列表
     */
    public static <T> List<T> parseArray(String text, Class<T> clazz) {
        if (StrUtil.isEmpty(text)) {
            return new ArrayList<>();
        }
        try {
            return objectMapper().readValue(text, objectMapper().getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (IOException e) {
            log.error("json parse err,json:{}", text, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 从 JSON 字符串中按路径提取并解析为指定类型的列表
     *
     * @param text  JSON 字符串
     * @param path  JSON 路径表达式
     * @param clazz 列表元素类型
     * @return 指定类型的列表
     */
    public static <T> List<T> parseArray(String text, String path, Class<T> clazz) {
        if (StrUtil.isEmpty(text)) {
            return null;
        }
        try {
            JsonNode treeNode = objectMapper().readTree(text);
            JsonNode pathNode = treeNode.path(path);
            return objectMapper().readValue(pathNode.toString(), objectMapper().getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (IOException e) {
            log.error("json parse err,json:{}", text, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 将 JSON 字符串解析为 JsonNode 树节点
     *
     * @param text JSON 字符串
     * @return JsonNode 树节点
     */
    public static JsonNode parseTree(String text) {
        try {
            return objectMapper().readTree(text);
        } catch (IOException e) {
            log.error("json parse err,json:{}", text, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 将 JSON 字节数组解析为 JsonNode 树节点
     *
     * @param text JSON 字节数组
     * @return JsonNode 树节点
     */
    public static JsonNode parseTree(byte[] text) {
        try {
            return objectMapper().readTree(text);
        } catch (IOException e) {
            log.error("json parse err,json:{}", text, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 判断字符串是否为 JSON 类型的字符串（包括 JSONObject 和 JSONArray）
     *
     * @param text 字符串
     * @return true：是 JSON 字符串，false：不是
     */
    public static boolean isJson(String text) {
        return JSONUtil.isTypeJSON(text);
    }

    /**
     * 判断字符串是否为 JSON 对象类型的字符串（JSONObject）
     *
     * @param str 字符串
     * @return true：是 JSON 对象，false：不是
     */
    public static boolean isJsonObject(String str) {
        return JSONUtil.isTypeJSONObject(str);
    }

}
