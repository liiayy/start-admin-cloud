package cn.muziseo.service.system.module.organization.manager;

import cn.muziseo.common.db.service.impl.BaseServiceImpl;
import cn.muziseo.service.system.module.organization.repository.entity.DeptEntity;
import cn.muziseo.service.system.module.organization.repository.mapper.DeptMapper;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 部门表 Manager 层
 * <p>
 * 提供部门表的数据查询和基础数据库操作
 *
 * @author 木子软件
 * @Date 2026-02-11
 * @Wechat liiayy
 * @Email 773582348@qq.com
 * @Copyright <a href="https://code.muziseo.cn">木子软件</a>
 */
@Service
public class DeptManager extends BaseServiceImpl<DeptMapper, DeptEntity> {

    /**
     * 获取所有部门列表（按排序）
     *
     * @return 部门列表
     */
    public List<DeptEntity> listAll() {
        return list(QueryWrapper.create()
                .orderBy(DeptEntity::getSort, true)
                .orderBy(DeptEntity::getId, true));
    }

    /**
     * 根据父部门ID获取子部门列表
     *
     * @param parentId 父部门ID
     * @return 子部门列表
     */
    public List<DeptEntity> listByParentId(Long parentId) {
        return list(QueryWrapper.create()
                .where(DeptEntity::getParentId).eq(parentId)
                .orderBy(DeptEntity::getSort, true));
    }

    /**
     * 获取部门树形结构
     *
     * @return 部门树
     */
    public List<DeptEntity> tree() {
        List<DeptEntity> allDepts = listAll();
        return buildTree(allDepts, 0L);
    }

    /**
     * 构建部门树
     *
     * @param depts    所有部门列表
     * @param parentId 父部门ID
     * @return 树形结构
     */
    private List<DeptEntity> buildTree(List<DeptEntity> depts, Long parentId) {
        return depts.stream()
                .filter(dept -> parentId.equals(dept.getParentId()))
                .peek(dept -> {
                    // 注意：需要在DeptEntity中添加children字段
                    List<DeptEntity> children = buildTree(depts, dept.getId());
                    // dept.setChildren(children);
                })
                .collect(Collectors.toList());
    }

    /**
     * 统计子部门数量
     *
     * @param parentId 父部门ID
     * @return 子部门数量
     */
    public long countByParentId(Long parentId) {
        return count(QueryWrapper.create()
                .where(DeptEntity::getParentId).eq(parentId));
    }
}
