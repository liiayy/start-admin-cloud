package cn.muziseo.common.excel.utils;

import cn.idev.excel.FastExcel;
import cn.idev.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.muziseo.common.core.utils.string.StringUtils;
import cn.muziseo.common.excel.core.CellMergeStrategy;
import cn.muziseo.common.excel.core.DataWriteHandler;
import cn.muziseo.common.excel.core.DefaultExcelListener;
import cn.muziseo.common.excel.core.ExcelResult;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import cn.idev.excel.write.metadata.style.WriteCellStyle;
import cn.idev.excel.write.metadata.style.WriteFont;
import cn.idev.excel.write.style.HorizontalCellStyleStrategy;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

/**
 * Excel 工具类
 *
 * @author StartAdmin
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExcelUtil {

    /**
     * 获取默认样式策略
     */
    public static HorizontalCellStyleStrategy getDefaultStyleStrategy() {
        // 表头策略
        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        // 背景色：浅灰色 (GREY_25_PERCENT)
        headWriteCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headWriteCellStyle.setFillPatternType(FillPatternType.SOLID_FOREGROUND);
        // 字体
        WriteFont headWriteFont = new WriteFont();
        headWriteFont.setFontName("微软雅黑");
        headWriteFont.setFontHeightInPoints((short) 12);
        headWriteFont.setBold(true);
        headWriteCellStyle.setWriteFont(headWriteFont);
        // 居中
        headWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        headWriteCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        // 内容策略
        WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
        WriteFont contentWriteFont = new WriteFont();
        contentWriteFont.setFontName("微软雅黑");
        contentWriteFont.setFontHeightInPoints((short) 11);
        contentWriteCellStyle.setWriteFont(contentWriteFont);
        // 居中 & 边框
        contentWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        contentWriteCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        contentWriteCellStyle.setBorderLeft(org.apache.poi.ss.usermodel.BorderStyle.THIN);
        contentWriteCellStyle.setBorderRight(org.apache.poi.ss.usermodel.BorderStyle.THIN);
        contentWriteCellStyle.setBorderTop(org.apache.poi.ss.usermodel.BorderStyle.THIN);
        contentWriteCellStyle.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN);

        return new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);
    }

    /**
     * 导出 Excel
     *
     * @param list      数据列表
     * @param sheetName Sheet 名称
     * @param clazz     实体类
     * @param response  响应对象
     */
    public static <T> void exportExcel(List<T> list, String sheetName, Class<T> clazz, HttpServletResponse response) {
        try {
            String filename = encodingFilename(sheetName);
            response.reset();
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + filename + ".xlsx");
            FastExcel.write(response.getOutputStream(), clazz)
                .autoCloseStream(false)
                .registerWriteHandler(getDefaultStyleStrategy())
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .registerWriteHandler(new DataWriteHandler(clazz))
                .registerWriteHandler(new CellMergeStrategy(list, clazz))
                .sheet(sheetName)
                .doWrite(list);
        } catch (IOException e) {
            log.error("导出 Excel 异常", e);
        }
    }

    /**
     * 导入 Excel
     *
     * @param is    输入流
     * @param clazz 实体类
     * @return 数据列表
     */
    public static <T> List<T> importExcel(InputStream is, Class<T> clazz) {
        return FastExcel.read(is).head(clazz).sheet().doReadSync();
    }

    /**
     * 导入 Excel (带校验)
     *
     * @param is         输入流
     * @param clazz      实体类
     * @param isValidate 是否开启校验
     * @return 导入结果
     */
    public static <T> ExcelResult<T> importExcel(InputStream is, Class<T> clazz, boolean isValidate) {
        DefaultExcelListener<T> listener = new DefaultExcelListener<>(isValidate);
        FastExcel.read(is, clazz, listener).sheet().doRead();
        return listener.getExcelResult();
    }

    /**
     * 解析导出值 0=男,1=女,2=未知
     *
     * @param propertyValue 参数值
     * @param converterExp  翻译注解
     * @param separator     分隔符
     * @return 解析后值
     */
    public static String convertByExp(String propertyValue, String converterExp, String separator) {
        StringBuilder propertyString = new StringBuilder();
        String[] convertSource = converterExp.split(",");
        for (String item : convertSource) {
            String[] itemArray = item.split("=");
            if (StringUtils.containsAny(propertyValue, separator)) {
                for (String value : propertyValue.split(separator)) {
                    if (itemArray[0].equals(value)) {
                        propertyString.append(itemArray[1]).append(separator);
                        break;
                    }
                }
            } else {
                if (itemArray[0].equals(propertyValue)) {
                    return itemArray[1];
                }
            }
        }
        return StringUtils.stripEnd(propertyString.toString(), separator);
    }

    /**
     * 反向解析值 男=0,女=1,未知=2
     *
     * @param propertyValue 参数值
     * @param converterExp  翻译注解
     * @param separator     分隔符
     * @return 解析后值
     */
    public static String reverseByExp(String propertyValue, String converterExp, String separator) {
        StringBuilder propertyString = new StringBuilder();
        String[] convertSource = converterExp.split(",");
        for (String item : convertSource) {
            String[] itemArray = item.split("=");
            if (StringUtils.containsAny(propertyValue, separator)) {
                for (String value : propertyValue.split(separator)) {
                    if (itemArray[1].equals(value)) {
                        propertyString.append(itemArray[0]).append(separator);
                        break;
                    }
                }
            } else {
                if (itemArray[1].equals(propertyValue)) {
                    return itemArray[0];
                }
            }
        }
        return StringUtils.stripEnd(propertyString.toString(), separator);
    }

    /**
     * 编码文件名
     */
    public static String encodingFilename(String filename) {
        return URLEncoder.encode(filename, StandardCharsets.UTF_8).replace("\\+", "%20");
    }

}
