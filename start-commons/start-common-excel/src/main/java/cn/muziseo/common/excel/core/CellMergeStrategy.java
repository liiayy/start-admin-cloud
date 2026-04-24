package cn.muziseo.common.excel.core;

import cn.idev.excel.write.handler.RowWriteHandler;
import cn.idev.excel.write.metadata.holder.WriteSheetHolder;
import cn.idev.excel.write.metadata.holder.WriteTableHolder;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.muziseo.common.excel.annotation.CellMerge;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 列单元格合并策略
 *
 * @author StartAdmin
 */
public class CellMergeStrategy implements RowWriteHandler {

    private final List<?> list;
    private final Class<?> clazz;

    public CellMergeStrategy(List<?> list, Class<?> clazz) {
        this.list = list;
        this.clazz = clazz;
    }

    @Override
    public void afterRowDispose(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, Row row, Integer relativeRowIndex, Boolean isHead) {
        if (isHead || relativeRowIndex == null || relativeRowIndex == 0) {
            return;
        }
        // 处理最后一行
        if (relativeRowIndex == list.size() - 1) {
            Sheet sheet = writeSheetHolder.getSheet();
            handleMerge(sheet);
        }
    }

    private void handleMerge(Sheet sheet) {
        Field[] fields = clazz.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            CellMerge anno = field.getAnnotation(CellMerge.class);
            if (anno != null) {
                int colIndex = anno.index() >= 0 ? anno.index() : i;
                doMerge(sheet, field, colIndex, anno.mergeBy());
            }
        }
    }

    private void doMerge(Sheet sheet, Field field, int colIndex, String[] mergeBy) {
        int startRow = 1; // 假设第一行是头
        int listSize = list.size();
        for (int i = 0; i < listSize; i++) {
            int currentRow = startRow + i;
            int lastRow = currentRow;
            Object currentVal = getFieldValue(list.get(i), field);

            for (int j = i + 1; j < listSize; j++) {
                Object nextVal = getFieldValue(list.get(j), field);
                if (isSame(i, j, currentVal, nextVal, mergeBy)) {
                    lastRow++;
                    i++;
                } else {
                    break;
                }
            }

            if (lastRow > currentRow) {
                sheet.addMergedRegion(new CellRangeAddress(currentRow, lastRow, colIndex, colIndex));
            }
        }
    }

    private Object getFieldValue(Object obj, Field field) {
        return ReflectUtil.getFieldValue(obj, field);
    }

    private boolean isSame(int i, int j, Object v1, Object v2, String[] mergeBy) {
        if (!Objects.equals(v1, v2)) {
            return false;
        }
        if (mergeBy.length == 0) {
            return true;
        }
        for (String fieldName : mergeBy) {
            Object val1 = ReflectUtil.getFieldValue(list.get(i), fieldName);
            Object val2 = ReflectUtil.getFieldValue(list.get(j), fieldName);
            if (!Objects.equals(val1, val2)) {
                return false;
            }
        }
        return true;
    }

}
