package cn.muziseo.common.excel.core;

import cn.idev.excel.write.handler.CellWriteHandler;
import cn.idev.excel.write.metadata.holder.WriteSheetHolder;
import cn.idev.excel.write.metadata.holder.WriteTableHolder;
import cn.idev.excel.metadata.Head;
import cn.idev.excel.metadata.data.WriteCellData;
import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.util.ObjectUtil;

import cn.muziseo.common.core.utils.string.StringUtils;
import cn.muziseo.common.excel.annotation.ExcelNotation;
import cn.muziseo.common.excel.annotation.ExcelRequired;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Excel 写入处理器 (处理注解样式)
 *
 * @author StartAdmin
 */
public class DataWriteHandler implements CellWriteHandler {

    private final Class<?> clazz;

    public DataWriteHandler(Class<?> clazz) {
        this.clazz = clazz;
    }

    @Override
    public void afterCellDispose(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, List<WriteCellData<?>> cellDataList, Cell cell, Head head, Integer relativeRowIndex, Boolean isHead) {
        if (isHead) {
            Field field = getField(head.getFieldName());
            if (field != null) {
                // 处理必填样式
                ExcelRequired required = AnnotationUtil.getAnnotation(field, ExcelRequired.class);
                if (required != null) {
                    Workbook workbook = writeSheetHolder.getSheet().getWorkbook();
                    CellStyle cellStyle = workbook.createCellStyle();
                    cellStyle.cloneStyleFrom(cell.getCellStyle());
                    Font font = workbook.createFont();
                    font.setColor(required.fontColor());
                    cellStyle.setFont(font);
                    cell.setCellStyle(cellStyle);
                }
                // 处理批注
                ExcelNotation notation = AnnotationUtil.getAnnotation(field, ExcelNotation.class);
                if (notation != null && StringUtils.isNotBlank(notation.value())) {
                    Sheet sheet = writeSheetHolder.getSheet();
                    Drawing<?> drawing = sheet.createDrawingPatriarch();
                    Comment comment = drawing.createCellComment(new XSSFClientAnchor(0, 0, 0, 0, (short) 3, 3, (short) 5, 6));
                    comment.setString(new XSSFRichTextString(notation.value()));
                    cell.setCellComment(comment);
                }
            }
        }
    }

    private Field getField(String fieldName) {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            return null;
        }
    }

}
