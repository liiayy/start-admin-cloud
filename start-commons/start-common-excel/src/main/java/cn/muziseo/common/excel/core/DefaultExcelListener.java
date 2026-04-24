package cn.muziseo.common.excel.core;

import cn.idev.excel.context.AnalysisContext;
import cn.idev.excel.event.AnalysisEventListener;
import cn.idev.excel.exception.ExcelAnalysisException;
import cn.hutool.core.util.ObjectUtil;
import cn.muziseo.common.core.utils.validator.ValidatorUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * 默认 Excel 导入监听器
 *
 * @author StartAdmin
 */
@Slf4j
public class DefaultExcelListener<T> extends AnalysisEventListener<T> {

    /**
     * 是否开启校验
     */
    private final boolean isValidate;

    /**
     * 导入结果
     */
    private final ExcelResult<T> excelResult;

    public DefaultExcelListener(boolean isValidate) {
        this.isValidate = isValidate;
        this.excelResult = new ExcelResult<>();
    }

    @Override
    public void invoke(T data, AnalysisContext context) {
        if (ObjectUtil.isNull(data)) {
            throw new ExcelAnalysisException("数据解析异常，请检查表格是否规范");
        }
        if (isValidate) {
            try {
                ValidatorUtils.validate(data);
            } catch (Exception e) {
                excelResult.getErrorList().add("第 " + context.readRowHolder().getRowIndex() + " 行校验失败：" + e.getMessage());
                return;
            }
        }
        excelResult.getList().add(data);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.debug("Excel 解析完成，共解析 {} 条数据", excelResult.getList().size());
    }

    public ExcelResult<T> getExcelResult() {
        return excelResult;
    }

}
