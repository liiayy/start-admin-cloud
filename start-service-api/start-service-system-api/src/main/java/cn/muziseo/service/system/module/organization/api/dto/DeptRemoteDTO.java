package cn.muziseo.service.system.module.organization.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 远程调用部门信息 DTO
 *
 * @author 木子软件
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeptRemoteDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 部门ID
     */
    private Long id;

    /**
     * 父部门ID
     */
    private Long parentId;

    /**
     * 部门名称
     */
    private String name;

    /**
     * 负责人用户ID
     */
    private Long leaderUserId;

    /**
     * 状态 (0正常 1停用)
     */
    private Integer status;
}
