package cn.muziseo.common.core.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 数据范围信息 DTO
 *
 * @author 木子软件
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataScopeInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 是否需要过滤（false=全部数据，不过滤）
     */
    private boolean filter;

    /**
     * 允许访问的部门 ID 列表
     */
    private List<Long> deptIds;

}
