package cn.muziseo.service.system.module.permission.manager;

import cn.muziseo.common.db.service.impl.BaseServiceImpl;
import cn.muziseo.service.system.module.permission.repository.entity.RoleMenuEntity;
import cn.muziseo.service.system.module.permission.repository.mapper.RoleMenuMapper;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色菜单关联管理 Manager 层
 * <p>
 * 处理角色与菜单之间的多对多关联关系，提供权限分配、关联查询及清理功能。
 *
 * @author 木子软件
 */
@Service
public class RoleMenuManager extends BaseServiceImpl<RoleMenuMapper, RoleMenuEntity> {

    /**
     * 根据一组角色 ID 获取关联的所有菜单 ID 列表
     * <p>
     * 常用于计算用户最终拥有的权限集合。
     *
     * @param roleIds 角色 ID 列表
     * @return 去重后的菜单 ID 列表
     */
    public List<Long> getMenuIdsByRoleIds(List<Long> roleIds) {
        return queryChain()
                .where(RoleMenuEntity::getRoleId).in(roleIds)
                .list()
                .stream()
                .map(RoleMenuEntity::getMenuId)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 根据角色 ID 删除其所有的菜单关联记录
     *
     * @param roleId 角色 ID
     */
    public void deleteByRoleId(Long roleId) {
        remove(QueryWrapper.create()
                .where(RoleMenuEntity::getRoleId).eq(roleId));
    }

    /**
     * 根据菜单 ID 获取所有关联的角色 ID 列表
     *
     * @param menuId 菜单 ID
     * @return 关联的角色 ID 列表
     */
    public List<Long> getRoleIdsByMenuId(Long menuId) {
        return queryChain()
                .where(RoleMenuEntity::getMenuId).eq(menuId)
                .list()
                .stream()
                .map(RoleMenuEntity::getRoleId)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 根据菜单 ID 删除其所有的角色关联记录
     *
     * @param menuId 菜单 ID
     */
    public void deleteByMenuId(Long menuId) {
        remove(QueryWrapper.create()
                .where(RoleMenuEntity::getMenuId).eq(menuId));
    }
}
