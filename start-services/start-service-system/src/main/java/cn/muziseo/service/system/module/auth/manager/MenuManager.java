package cn.muziseo.service.system.module.auth.manager;

import cn.muziseo.common.db.service.impl.BaseServiceImpl;
import cn.muziseo.service.system.module.auth.repository.entity.MenuEntity;
import cn.muziseo.service.system.module.auth.repository.mapper.MenuMapper;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜单表 Manager 层
 * <p>
 * 提供菜单表的数据查询和基础数据库操作
 *
 * @author 木子软件
 * @Date 2026-01-07
 * @Wechat liiayy
 * @Email 773582348@qq.com
 * @Copyright <a href="https://code.muziseo.cn">木子软件</a>
 */
@Service
public class MenuManager extends BaseServiceImpl<MenuMapper, MenuEntity> {

    /**
     * 根据父菜单ID获取子菜单列表
     *
     * @param parentId 父菜单ID
     * @return 子菜单列表
     */
    public List<MenuEntity> listByParentId(Long parentId) {
        return list(QueryWrapper.create()
                .where(MenuEntity::getParentId).eq(parentId)
                .orderBy(MenuEntity::getSort, true));
    }

    /**
     * 获取菜单树形结构
     *
     * @return 菜单树
     */
    public List<MenuEntity> tree() {
        List<MenuEntity> allMenus = list(QueryWrapper.create()
                .orderBy(MenuEntity::getSort, true)
                .orderBy(MenuEntity::getId, true));
        return buildTree(allMenus, 0L);
    }

    /**
     * 构建菜单树
     *
     * @param menus    所有菜单列表
     * @param parentId 父菜单ID
     * @return 树形结构
     */
    private List<MenuEntity> buildTree(List<MenuEntity> menus, Long parentId) {
        return menus.stream()
                .filter(menu -> parentId.equals(menu.getParentId()))
                .peek(menu -> {
                    List<MenuEntity> children = buildTree(menus, menu.getId());
                    // 注意：需要在MenuEntity中添加children字段
                    // menu.setChildren(children);
                })
                .collect(Collectors.toList());
    }
}
