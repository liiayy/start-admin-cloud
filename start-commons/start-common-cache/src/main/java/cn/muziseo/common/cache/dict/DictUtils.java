package cn.muziseo.common.cache.dict;

import cn.muziseo.common.core.constant.DictConstants;
import cn.muziseo.common.core.domain.dto.DictDataSimpleDTO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 字典工具类
 *
 * <p>
 * 提供字典值 ↔ 标签的互转、批量翻译、分隔符翻译等高频操作。
 * 所有方法都基于 {@link DictCacheManager} 二级缓存，无需担心性能。
 * </p>
 *
 * <h3>使用示例：</h3>
 * <pre>{@code
 * // 1. 值转标签（最常用）
 * String label = DictUtils.getDictLabel(DictConstants.SYS_USER_SEX, "0");
 * // 返回: "男"
 *
 * // 2. 标签转值
 * String value = DictUtils.getDictValue(DictConstants.SYS_USER_SEX, "男");
 * // 返回: "0"
 *
 * // 3. 获取颜色
 * String color = DictUtils.getColorType(DictConstants.SYS_STATUS, "0");
 * // 返回: "success"
 *
 * // 4. 批量翻译（如 "0,1" → "男,女"）
 * String labels = DictUtils.getDictLabels(DictConstants.SYS_USER_SEX, "0,1", ",");
 * // 返回: "男,女"
 * }</pre>
 *
 * @author 木子软件
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DictUtils {

    // ================== 兜底回调（由外部设置） ================== //

    /**
     * 全局 RPC 兜底回调
     * <p>
     * 由各微服务在启动时注入（通过 {@link #setRpcFallback}），
     * 默认返回空列表（纯缓存模式）。
     * </p>
     */
    private static volatile Function<String, List<DictDataSimpleDTO>> globalRpcFallback = k -> Collections.emptyList();

    /**
     * 设置全局 RPC 兜底回调
     *
     * @param fallback RPC 回调函数
     */
    public static void setRpcFallback(Function<String, List<DictDataSimpleDTO>> fallback) {
        globalRpcFallback = fallback;
    }

    /**

     * 根据字典类型和字典值，获取字典标签
     *
     * @param dictType 字典类型
     * @param value    字典值
     * @return 字典标签，未找到则返回原值
     */
    public static String getDictLabel(String dictType, String value) {
        return getDictLabel(dictType, value, null);
    }

    /**
     * 根据字典类型和字典值，获取字典标签（支持自定义兜底回调）
     *
     * @param dictType    字典类型
     * @param value       字典值
     * @param rpcFallback 兜底回调（为 null 时不穿透）
     * @return 字典标签，未找到则返回原值
     */
    public static String getDictLabel(String dictType, String value, Function<String, List<DictDataSimpleDTO>> rpcFallback) {
        if (value == null) {
            return "";
        }
        List<DictDataSimpleDTO> dictList = getDict(dictType, rpcFallback);
        return dictList.stream()
                .filter(d -> Objects.equals(d.getValue(), value))
                .map(DictDataSimpleDTO::getLabel)
                .findFirst()
                .orElse(value);
    }

    /**
     * 根据字典类型和字典标签，获取字典值
     *
     * @param dictType 字典类型
     * @param label    字典标签
     * @return 字典值，未找到则返回原标签
     */
    public static String getDictValue(String dictType, String label) {
        return getDictValue(dictType, label, null);
    }

    /**
     * 根据字典类型和字典标签，获取字典值（支持自定义兜底回调）
     */
    public static String getDictValue(String dictType, String label, Function<String, List<DictDataSimpleDTO>> rpcFallback) {
        if (label == null) {
            return "";
        }
        List<DictDataSimpleDTO> dictList = getDict(dictType, rpcFallback);
        return dictList.stream()
                .filter(d -> Objects.equals(d.getLabel(), label))
                .map(DictDataSimpleDTO::getValue)
                .findFirst()
                .orElse(label);
    }

    /**
     * 获取字典项的颜色类型（如 success, danger, warning 等）
     *
     * @param dictType 字典类型
     * @param value    字典值
     * @return 颜色类型，未找到则返回空字符串
     */
    public static String getColorType(String dictType, String value) {
        return getColorType(dictType, value, null);
    }

    /**
     * 获取字典项的颜色类型（支持自定义兜底回调）
     */
    public static String getColorType(String dictType, String value, Function<String, List<DictDataSimpleDTO>> rpcFallback) {
        if (value == null) {
            return "";
        }
        List<DictDataSimpleDTO> dictList = getDict(dictType, rpcFallback);
        return dictList.stream()
                .filter(d -> Objects.equals(d.getValue(), value))
                .map(DictDataSimpleDTO::getColorType)
                .findFirst()
                .orElse("");
    }

    /**
     * 批量翻译：将用分隔符连接的多个字典值翻译为对应的标签
     * <p>
     * 例如：值 "0,1" → 标签 "男,女"
     * </p>
     *
     * @param dictType  字典类型
     * @param values    用分隔符连接的字典值字符串
     * @param separator 分隔符（如 ","）
     * @return 翻译后的标签字符串
     */
    public static String getDictLabels(String dictType, String values, String separator) {
        return getDictLabels(dictType, values, separator, null);
    }

    /**
     * 批量翻译（支持自定义兜底回调）
     */
    public static String getDictLabels(String dictType, String values, String separator,
                                       Function<String, List<DictDataSimpleDTO>> rpcFallback) {
        if (values == null || values.isEmpty()) {
            return "";
        }
        List<DictDataSimpleDTO> dictList = getDict(dictType, rpcFallback);
        String[] valueArr = values.split(separator);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < valueArr.length; i++) {
            String val = valueArr[i].trim();
            String label = dictList.stream()
                    .filter(d -> Objects.equals(d.getValue(), val))
                    .map(DictDataSimpleDTO::getLabel)
                    .findFirst()
                    .orElse(val);
            if (i > 0) {
                sb.append(separator);
            }
            sb.append(label);
        }
        return sb.toString();
    }

    /**
     * 获取指定字典类型的所有数据项
     *
     * @param dictType    字典类型
     * @param rpcFallback 兜底回调（为 null 时不穿透，仅查缓存）
     * @return 字典数据列表
     */
    public static List<DictDataSimpleDTO> getDict(String dictType, Function<String, List<DictDataSimpleDTO>> rpcFallback) {
        // 如果传入了局部回调，优先用局部的；否则用全局的
        Function<String, List<DictDataSimpleDTO>> fallback = rpcFallback != null ? rpcFallback : globalRpcFallback;
        return DictCacheManager.getDict(dictType, fallback);
    }

    /**
     * 整数值转标签（便捷重载：自动将 Integer 转 String 匹配）
     *
     * @param dictType 字典类型
     * @param value    字典值（整数）
     * @return 字典标签
     */
    public static String getDictLabel(String dictType, Integer value) {
        return value == null ? "" : getDictLabel(dictType, String.valueOf(value));
    }

    /**
     * 获取指定字典类型的映射表 (Value -> Label)
     *
     * @param dictType 字典类型
     * @return Value -> Label 的 Map
     */
    public static java.util.Map<String, String> getDictMap(String dictType) {
        return getDict(dictType, null).stream()
                .collect(Collectors.toMap(DictDataSimpleDTO::getValue, DictDataSimpleDTO::getLabel, (k1, k2) -> k1));
    }
 
    /**
     * 整数值获取颜色（便捷重载）
     */
    public static String getColorType(String dictType, Integer value) {
        return value == null ? "" : getColorType(dictType, String.valueOf(value));
    }
}
