package cn.muziseo.service.system.module.permission.service;

import cn.muziseo.common.db.page.PageResponse;
import cn.muziseo.service.system.module.permission.controller.request.RoleCreateRequest;
import cn.muziseo.service.system.module.permission.controller.request.RolePageRequest;
import cn.muziseo.service.system.module.permission.controller.request.RoleUpdateRequest;
import cn.muziseo.service.system.module.permission.controller.vo.RoleVO;
import cn.muziseo.service.system.module.permission.repository.entity.RoleEntity;

import java.util.List;

/**
 * 角色业务接口
 *
 * @author 木子软件
 */
public interface RoleService {

    /**
     * 获取用户角色列表
     */
    List<RoleEntity> getRolesByUserId(Long userId);

    /**
     * 分页查询角色
     */
    PageResponse<RoleVO> pageRole(RolePageRequest request);

    /**
     * 获取角色详情
     */
    RoleVO getRole(Long id);

    /**
     * 新增角色
     */
    void createRole(RoleCreateRequest request);

    /**
     * 修改角色
     */
    void updateRole(RoleUpdateRequest request);

    /**
     * 删除角色
     */
    void deleteRole(Long id);

    /**
     * 更新角色状态
     */
    void updateStatus(Long id, Integer status);

    /**
     * 给角色分配菜单
     */
    void assignMenus(Long roleId, List<Long> menuIds);
}
