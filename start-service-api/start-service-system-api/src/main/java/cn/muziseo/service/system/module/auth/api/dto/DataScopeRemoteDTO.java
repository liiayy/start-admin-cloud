package cn.muziseo.service.system.module.auth.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 远程调用数据权限 DTO
 *
 * @author 木子软件
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataScopeRemoteDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 是否需要过滤
     */
    private boolean filter;

    /**
     * 允许访问的部门 ID 列表
     */
    private List<Long> deptIds;
}
