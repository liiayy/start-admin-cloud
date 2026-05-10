package cn.muziseo.service.system.module.permission.manager;

import cn.muziseo.common.db.service.impl.BaseServiceImpl;
import cn.muziseo.service.system.module.permission.repository.entity.MenuEntity;
import cn.muziseo.service.system.module.permission.repository.mapper.MenuMapper;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜单管理 Manager 层
 * <p>
 * 处理系统菜单及权限项的持久化逻辑，提供层级查询、树形结构构建及权限标识校验功能。
 *
 * @author 木子软件
 */
@Service
public class MenuManager extends BaseServiceImpl<MenuMapper, MenuEntity> {

    /**
     * 根据父菜单 ID 获取直接子菜单列表
     * <p>
     * 结果按显示顺序（sort 字段）升序排列。
     *
     * @param parentId 父菜单 ID
     * @return 子菜单实体列表
     */
    public List<MenuEntity> listByParentId(Long parentId) {
        return list(QueryWrapper.create()
                .where(MenuEntity::getParentId).eq(parentId)
                .orderBy(MenuEntity::getSort, true));
    }

    /**
     * 获取全量菜单并构建树形结构
     * <p>
     * 常用于前端菜单展示或权限树选择。
     *
     * @return 构建好的菜单树列表
     */
    public List<MenuEntity> tree() {
        List<MenuEntity> allMenus = list(QueryWrapper.create()
                .orderBy(MenuEntity::getSort, true)
                .orderBy(MenuEntity::getId, true));
        return buildTree(allMenus, 0L);
    }

    /**
     * 校验权限标识是否已存在
     *
     * @param permission 权限标识串（如：system:user:create）
     * @param excludeId  需要排除的菜单 ID（用于修改时校验）
     * @return true 表示已存在，false 表示不存在
     */
    public boolean existsByPermission(String permission, Long excludeId) {
        if (permission == null || permission.isEmpty()) {
            return false;
        }
        QueryWrapper wrapper = QueryWrapper.create()
                .where(MenuEntity::getPermission).eq(permission);
        if (excludeId != null) {
            wrapper.and(MenuEntity::getId).ne(excludeId);
        }
        return exists(wrapper);
    }

    /**
     * 构建菜单树（递归工具方法）
     *
     * @param menus    待处理的扁平菜单列表
     * @param parentId 当前处理的父节点 ID
     * @return 构建好的子树列表
     */
    private List<MenuEntity> buildTree(List<MenuEntity> menus, Long parentId) {
        return menus.stream()
                .filter(menu -> parentId.equals(menu.getParentId()))
                .peek(menu -> {
                    List<MenuEntity> children = buildTree(menus, menu.getId());
                    // 注意：需在 MenuEntity 中补充子节点列表字段（如 children）并赋值
                    // menu.setChildren(children);
                })
                .collect(Collectors.toList());
    }
}
