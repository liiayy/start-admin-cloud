package cn.muziseo.service.system.module.organization.manager;

import cn.muziseo.common.db.service.impl.BaseServiceImpl;
import cn.muziseo.service.system.module.organization.repository.entity.DeptEntity;
import cn.muziseo.service.system.module.organization.repository.mapper.DeptMapper;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 部门管理 Manager 层
 * <p>
 * 处理组织架构中部门数据的持久化逻辑，提供层级查询、存在性校验及子部门递归获取功能。
 *
 * @author 木子软件
 */
@Service
public class DeptManager extends BaseServiceImpl<DeptMapper, DeptEntity> {

    /**
     * 获取所有部门列表
     * <p>
     * 结果按显示顺序（sort 字段）及 ID 升序排列。
     *
     * @return 部门实体列表
     */
    public List<DeptEntity> listAll() {
        return list(QueryWrapper.create()
                .orderBy(DeptEntity::getSort, true)
                .orderBy(DeptEntity::getId, true));
    }

    /**
     * 根据父部门 ID 获取直接子部门列表
     *
     * @param parentId 父部门 ID
     * @return 子部门实体列表
     */
    public List<DeptEntity> listByParentId(Long parentId) {
        return list(QueryWrapper.create()
                .where(DeptEntity::getParentId).eq(parentId)
                .orderBy(DeptEntity::getSort, true));
    }

    /**
     * 校验部门名称在该父部门下是否已存在
     *
     * @param name      部门名称
     * @param excludeId 需要排除的部门 ID（用于修改时校验）
     * @return true 表示已存在，false 表示不存在
     */
    public boolean existsByName(String name, Long excludeId) {
        QueryWrapper qw = QueryWrapper.create().where(DeptEntity::getName).eq(name);
        if (excludeId != null) {
            qw.and(DeptEntity::getId).ne(excludeId);
        }
        return exists(qw);
    }

    /**
     * 根据部门名称查询部门信息
     *
     * @param name 部门名称
     * @return 部门实体信息，如果不存在则返回 null
     */
    public DeptEntity getByName(String name) {
        return getOne(QueryWrapper.create().where(DeptEntity::getName).eq(name));
    }

    /**
     * 统计指定父部门下的直接子部门数量
     *
     * @param parentId 父部门 ID
     * @return 子部门数量
     */
    public long countByParentId(Long parentId) {
        return count(QueryWrapper.create()
                .where(DeptEntity::getParentId).eq(parentId));
    }

    /**
     * 获取指定部门及其所有后代子部门的 ID 集合
     * <p>
     * 采用递归方式搜索整个部门树。
     *
     * @param deptId 起始部门 ID
     * @return 包含起始部门及所有后代部门 ID 的列表
     */
    public List<Long> getDeptAndChildIds(Long deptId) {
        if (deptId == null) {
            return List.of();
        }
        List<Long> ids = new ArrayList<>();
        ids.add(deptId);
        collectChildIds(deptId, ids);
        return ids;
    }

    /**
     * 递归搜集子部门 ID
     *
     * @param parentId 父部门 ID
     * @param ids      用于存储结果的 ID 列表
     */
    private void collectChildIds(Long parentId, List<Long> ids) {
        for (DeptEntity child : listByParentId(parentId)) {
            ids.add(child.getId());
            collectChildIds(child.getId(), ids);
        }
    }
}
