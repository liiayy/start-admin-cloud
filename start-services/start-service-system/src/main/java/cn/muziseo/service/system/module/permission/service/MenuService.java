package cn.muziseo.service.system.module.permission.service;

import cn.muziseo.service.system.module.permission.controller.request.MenuAddRequest;
import cn.muziseo.service.system.module.permission.controller.request.MenuUpdateRequest;
import cn.muziseo.service.system.module.permission.controller.vo.MenuTreeVO;
import cn.muziseo.service.system.module.permission.controller.vo.MenuVO;
import cn.muziseo.service.system.module.permission.repository.entity.MenuEntity;

import java.util.List;

/**
 * 菜单业务接口
 *
 * @author 木子软件
 */
public interface MenuService {

    /**
     * 获取角色菜单列表
     */
    List<MenuVO> getMenusByRoleIds(List<Long> roleIds);

    /**
     * 获取用户菜单树
     *
     * @param userId 用户ID
     * @return 菜单树列表
     */
    List<MenuTreeVO> getUserMenuTree(Long userId);

    /**
     * 获取全量菜单树（管理用）
     *
     * @return 菜单树列表
     */
    List<MenuTreeVO> getMenuTree();

    /**
     * 获取菜单详情
     */
    MenuVO getMenu(Long id);

    /**
     * 新增菜单
     */
    void addMenu(MenuAddRequest request);

    /**
     * 修改菜单
     */
    void updateMenu(MenuUpdateRequest request);

    /**
     * 删除菜单
     */
    void deleteMenu(Long id);
}
