package cn.muziseo.common.excel.core;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 下拉框配置
 *
 * @author StartAdmin
 */
@Data
public class DropDownOptions {

    /**
     * 单级下拉框数据 <列索引, 选项列表>
     */
    private Map<Integer, List<String>> options;

    /**
     * 级联下拉框数据 <列索引, <父项, 子项列表>>
     */
    private Map<Integer, Map<String, List<String>>> cascadeOptions;

}
