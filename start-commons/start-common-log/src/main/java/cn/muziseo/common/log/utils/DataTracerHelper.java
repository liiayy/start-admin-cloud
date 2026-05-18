package cn.muziseo.common.log.utils;

import cn.hutool.core.util.ReflectUtil;
import cn.muziseo.common.core.datatracer.annotation.DataTracerFieldDict;
import cn.muziseo.common.core.datatracer.annotation.DataTracerFieldEnum;
import cn.muziseo.common.core.datatracer.annotation.DataTracerFieldIgnore;
import cn.muziseo.common.core.datatracer.annotation.DataTracerFieldLabel;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.Objects;

@Slf4j
public class DataTracerHelper {

    /**
     * 获取单个对象的所有被注解标记的字段内容（用于新增、删除时记录整体内容）
     */
    public static String getChangeContent(Object bean) {
        if (bean == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        Field[] fields = ReflectUtil.getFields(bean.getClass());
        for (Field field : fields) {
            if (field.isAnnotationPresent(DataTracerFieldIgnore.class)) {
                continue;
            }
            DataTracerFieldLabel labelAnnotation = field.getAnnotation(DataTracerFieldLabel.class);
            if (labelAnnotation != null) {
                Object value = ReflectUtil.getFieldValue(bean, field);
                String displayValue = getDisplayValue(field, value);
                sb.append(labelAnnotation.value()).append("：").append(displayValue).append("\n");
            }
        }
        return sb.toString().trim();
    }

    /**
     * 对比两个对象，获取有差异的字段内容
     */
    public static String getDiffContent(Object oldBean, Object newBean) {
        if (oldBean == null && newBean == null) {
            return "";
        }
        if (oldBean == null) {
            return getChangeContent(newBean);
        }
        if (newBean == null) {
            return getChangeContent(oldBean);
        }

        StringBuilder sb = new StringBuilder();
        Field[] fields = ReflectUtil.getFields(oldBean.getClass());

        for (Field field : fields) {
            if (field.isAnnotationPresent(DataTracerFieldIgnore.class)) {
                continue;
            }
            DataTracerFieldLabel labelAnnotation = field.getAnnotation(DataTracerFieldLabel.class);
            if (labelAnnotation != null) {
                Object oldValue = ReflectUtil.getFieldValue(oldBean, field);
                Object newValue = ReflectUtil.getFieldValue(newBean, field);

                if (!Objects.equals(oldValue, newValue)) {
                    String oldDisplay = getDisplayValue(field, oldValue);
                    String newDisplay = getDisplayValue(field, newValue);
                    sb.append(labelAnnotation.value()).append("：[").append(oldDisplay).append("] -> [").append(newDisplay).append("]\n");
                }
            }
        }
        return sb.toString().trim();
    }

    /**
     * 根据字段类型和注解，将真实值转换为展示值
     */
    private static String getDisplayValue(Field field, Object value) {
        if (value == null) {
            return "空";
        }

        // 字典翻译（通过反射动态调用，避免强物理依赖 start-common-cache）
        DataTracerFieldDict dictAnnotation = field.getAnnotation(DataTracerFieldDict.class);
        if (dictAnnotation != null) {
            try {
                Class<?> dictUtilsClass = Class.forName("cn.muziseo.common.cache.dict.DictUtils");
                java.lang.reflect.Method getDictLabelMethod = ReflectUtil.getMethod(dictUtilsClass, "getDictLabel", String.class, String.class);
                if (getDictLabelMethod != null) {
                    Object dictLabel = ReflectUtil.invokeStatic(getDictLabelMethod, dictAnnotation.dictType(), String.valueOf(value));
                    if (dictLabel != null) {
                        return String.valueOf(dictLabel);
                    }
                }
            } catch (Exception ignored) {
                // 如果当前服务没有加载缓存包，则直接输出原始字符串
            }
        }

        // 枚举翻译
        DataTracerFieldEnum enumAnnotation = field.getAnnotation(DataTracerFieldEnum.class);
        if (enumAnnotation != null) {
            Class<? extends Enum<?>> enumClass = enumAnnotation.enumClass();
            Enum<?>[] enumConstants = enumClass.getEnumConstants();
            for (Enum<?> enumConstant : enumConstants) {
                try {
                    Object enumValue = null;
                    if (ReflectUtil.getMethod(enumClass, "getValue") != null) {
                        enumValue = ReflectUtil.invoke(enumConstant, "getValue");
                    } else if (ReflectUtil.getMethod(enumClass, "getCode") != null) {
                        enumValue = ReflectUtil.invoke(enumConstant, "getCode");
                    }

                    if (Objects.equals(String.valueOf(enumValue), String.valueOf(value)) || 
                        enumConstant.name().equals(String.valueOf(value))) {
                        
                        if (ReflectUtil.getMethod(enumClass, "getDesc") != null) {
                            return String.valueOf(ReflectUtil.invoke(enumConstant, "getDesc"));
                        }
                        if (ReflectUtil.getMethod(enumClass, "getDescription") != null) {
                            return String.valueOf(ReflectUtil.invoke(enumConstant, "getDescription"));
                        }
                        return enumConstant.name();
                    }
                } catch (Exception e) {
                    log.warn("枚举翻译失败: {}", field.getName(), e);
                }
            }
        }

        return String.valueOf(value);
    }
}
