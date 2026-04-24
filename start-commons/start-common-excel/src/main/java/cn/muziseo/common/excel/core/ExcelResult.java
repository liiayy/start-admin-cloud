package cn.muziseo.common.excel.core;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Excel 导入结果
 *
 * @author StartAdmin
 */
@Getter
@Setter
public class ExcelResult<T> {

    /**
     * 数据列表
     */
    private List<T> list;

    /**
     * 错误列表
     */
    private List<String> errorList;

    public ExcelResult() {
        this.list = new ArrayList<>();
        this.errorList = new ArrayList<>();
    }

    public ExcelResult(List<T> list, List<String> errorList) {
        this.list = list;
        this.errorList = errorList;
    }

    /**
     * 获取分析报表
     */
    public String getAnalysis() {
        return "共导入 " + list.size() + " 条数据，失败 " + errorList.size() + " 条数据。";
    }

}
