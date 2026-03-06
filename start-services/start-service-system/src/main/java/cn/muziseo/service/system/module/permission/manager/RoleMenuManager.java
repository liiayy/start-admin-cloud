package cn.muziseo.service.system.module.permission.manager;

import cn.muziseo.common.db.service.impl.BaseServiceImpl;
import cn.muziseo.service.system.module.permission.repository.entity.RoleMenuEntity;
import cn.muziseo.service.system.module.permission.repository.mapper.RoleMenuMapper;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色菜单关联 Manager 层
 * <p>
 * 提供角色菜单关联表的数据查询和基础数据库操作
 *
 * @author 木子软件
 * @Date 2026-01-07
 * @Wechat liiayy
 * @Email 773582348@qq.com
 * @Copyright <a href="https://code.muziseo.cn">木子软件</a>
 */
@Service
public class RoleMenuManager extends BaseServiceImpl<RoleMenuMapper, RoleMenuEntity> {

    /**
     * 根据角色ID列表获取菜单ID列表
     *
     * @param roleIds 角色ID列表
     * @return 菜单ID列表
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
     * 根据角色ID删除关联
     *
     * @param roleId 角色ID
     */
    public void deleteByRoleId(Long roleId) {
        remove(QueryWrapper.create()
                .where(RoleMenuEntity::getRoleId).eq(roleId));
    }

    /**
     * 根据菜单ID删除关联
     *
     * @param menuId 菜单ID
     */
    public void deleteByMenuId(Long menuId) {
        remove(QueryWrapper.create()
                .where(RoleMenuEntity::getMenuId).eq(menuId));
    }
}
