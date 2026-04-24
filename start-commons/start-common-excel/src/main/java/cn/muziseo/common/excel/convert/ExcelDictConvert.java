package cn.muziseo.common.excel.convert;

import cn.idev.excel.converters.Converter;
import cn.idev.excel.enums.CellDataTypeEnum;
import cn.idev.excel.metadata.GlobalConfiguration;
import cn.idev.excel.metadata.data.ReadCellData;
import cn.idev.excel.metadata.data.WriteCellData;
import cn.idev.excel.metadata.property.ExcelContentProperty;
import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.muziseo.common.cache.dict.DictUtils;
import cn.muziseo.common.core.utils.string.StringUtils;
import cn.muziseo.common.excel.annotation.ExcelDictFormat;
import cn.muziseo.common.excel.utils.ExcelUtil;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;

/**
 * 字典格式化转换处理
 *
 * @author StartAdmin
 */
@Slf4j
public class ExcelDictConvert implements Converter<Object> {

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
        ExcelDictFormat anno = getAnnotation(contentProperty.getField());
        String label = cellData.getStringValue();
        String dictType = anno.dictType();

        Object value;
        if (StringUtils.isNotBlank(dictType)) {
            value = DictUtils.getDictValue(dictType, label);
        } else {
            value = ExcelUtil.reverseByExp(label, anno.readConverterExp(), anno.separator());
        }
        return Convert.convert(contentProperty.getField().getType(), value);
    }

    @Override
    public WriteCellData<String> convertToExcelData(Object value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        if (ObjectUtil.isNull(value)) {
            return new WriteCellData<>("");
        }
        ExcelDictFormat anno = getAnnotation(contentProperty.getField());
        String dictType = anno.dictType();

        String label;
        if (StringUtils.isNotBlank(dictType)) {
            label = DictUtils.getDictLabel(dictType, String.valueOf(value));
        } else {
            label = ExcelUtil.convertByExp(String.valueOf(value), anno.readConverterExp(), anno.separator());
        }
        return new WriteCellData<>(label);
    }

    private ExcelDictFormat getAnnotation(Field field) {
        return AnnotationUtil.getAnnotation(field, ExcelDictFormat.class);
    }

}
