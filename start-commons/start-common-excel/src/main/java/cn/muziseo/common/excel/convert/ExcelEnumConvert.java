package cn.muziseo.common.excel.convert;

import cn.idev.excel.converters.Converter;
import cn.idev.excel.enums.CellDataTypeEnum;
import cn.idev.excel.metadata.GlobalConfiguration;
import cn.idev.excel.metadata.data.ReadCellData;
import cn.idev.excel.metadata.data.WriteCellData;
import cn.idev.excel.metadata.property.ExcelContentProperty;
import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.muziseo.common.excel.annotation.ExcelEnumFormat;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.Objects;

/**
 * 枚举格式化转换处理
 *
 * @author StartAdmin
 */
@Slf4j
public class ExcelEnumConvert implements Converter<Object> {

    @Override
    public Class<Object> supportJavaTypeKey() {
        return Object.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return null;
    }

    @Override
    public Object convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        ExcelEnumFormat anno = getAnnotation(contentProperty.getField());
        String label = cellData.getStringValue();
        Class<? extends Enum<?>> enumClass = anno.enumClass();
        Enum<?>[] enums = enumClass.getEnumConstants();
        String labelField = anno.labelField();
        String valueField = anno.valueField();

        for (Enum<?> e : enums) {
            Object l = ReflectUtil.getFieldValue(e, labelField);
            if (Objects.equals(label, String.valueOf(l))) {
                return ReflectUtil.getFieldValue(e, valueField);
            }
        }
        return null;
    }

    @Override
    public WriteCellData<String> convertToExcelData(Object value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        if (ObjectUtil.isNull(value)) {
            return new WriteCellData<>("");
        }
        ExcelEnumFormat anno = getAnnotation(contentProperty.getField());
        Class<? extends Enum<?>> enumClass = anno.enumClass();
        Enum<?>[] enums = enumClass.getEnumConstants();
        String labelField = anno.labelField();
        String valueField = anno.valueField();

        for (Enum<?> e : enums) {
            Object v = ReflectUtil.getFieldValue(e, valueField);
            if (Objects.equals(value, v)) {
                return new WriteCellData<>(String.valueOf(ReflectUtil.getFieldValue(e, labelField)));
            }
        }
        return new WriteCellData<>("");
    }

    private ExcelEnumFormat getAnnotation(Field field) {
        return AnnotationUtil.getAnnotation(field, ExcelEnumFormat.class);
    }

}
