package cn.muziseo.service.system.module.organization.manager;

import cn.muziseo.common.db.service.impl.BaseServiceImpl;
import cn.muziseo.service.system.module.organization.repository.entity.DeptEntity;
import cn.muziseo.service.system.module.organization.repository.mapper.DeptMapper;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Service;

import java.util.List;

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
     * 检查部门名称是否已存在
     *
     * @param name      部门名称
     * @param excludeId 排除的部门ID（更新时排除自身）
     * @return 是否存在
     */
    public boolean existsByName(String name, Long excludeId) {
        QueryWrapper qw = QueryWrapper.create().where(DeptEntity::getName).eq(name);
        if (excludeId != null) {
            qw.and(DeptEntity::getId).ne(excludeId);
        }
        return exists(qw);
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
