package cn.muziseo.common.db.datascope;

import lombok.Data;

import java.util.List;

/**
 * 数据范围信息
 *
 * @author 木子软件
 */
@Data
public class DataScopeInfo {

    /**
     * 是否需要过滤（false=全部数据，不过滤）
     */
    private boolean filter;

    /**
     * 允许访问的部门 ID 列表
     */
    private List<Long> deptIds;

}
