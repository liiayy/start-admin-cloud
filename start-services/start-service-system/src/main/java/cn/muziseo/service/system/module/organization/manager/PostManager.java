package cn.muziseo.service.system.module.organization.manager;

import cn.muziseo.common.db.service.impl.BaseServiceImpl;
import cn.muziseo.service.system.module.organization.controller.request.PostPageRequest;
import cn.muziseo.service.system.module.organization.repository.entity.PostEntity;
import cn.muziseo.service.system.module.organization.repository.mapper.PostMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 岗位管理 Manager 层
 * <p>
 * 处理岗位数据的持久化逻辑，提供岗位查询、编码校验及分页筛选功能。
 *
 * @author 木子软件
 */
@Service
public class PostManager extends BaseServiceImpl<PostMapper, PostEntity> {

    /**
     * 获取所有岗位列表
     * <p>
     * 结果按显示顺序（sort 字段）升序排列。
     *
     * @return 岗位实体列表
     */
    public List<PostEntity> listAll() {
        return list(QueryWrapper.create()
                .orderBy(PostEntity::getSort, true)
                .orderBy(PostEntity::getId, true));
    }

    /**
     * 根据岗位编码查询岗位信息
     *
     * @param code 岗位编码
     * @return 岗位实体信息，如果不存在则返回 null
     */
    public PostEntity getByCode(String code) {
        return queryChain()
                .where(PostEntity::getCode).eq(code)
                .one();
    }

    /**
     * 校验岗位编码是否已存在
     *
     * @param code      岗位编码
     * @param excludeId 需要排除的岗位 ID（用于修改时校验）
     * @return true 表示已存在，false 表示不存在
     */
    public boolean existsByCode(String code, Long excludeId) {
        QueryWrapper qw = QueryWrapper.create().where(PostEntity::getCode).eq(code);
        if (excludeId != null) {
            qw.and(PostEntity::getId).ne(excludeId);
        }
        return exists(qw);
    }

    /**
     * 统计指定部门下的岗位总数
     *
     * @param deptId 部门 ID
     * @return 岗位数量
     */
    public long countByDeptId(Long deptId) {
        return count(QueryWrapper.create()
                .where(PostEntity::getDeptId).eq(deptId));
    }

    /**
     * 分页查询岗位列表
     * <p>
     * 支持所属部门集合过滤及岗位名称模糊查询。
     *
     * @param request 分页及基本筛选条件
     * @param deptIds 所属部门 ID 集合（支持包含子部门的批量过滤）
     * @return 分页结果对象
     */
    public Page<PostEntity> pagePost(PostPageRequest request, List<Long> deptIds) {
        QueryWrapper wrapper = QueryWrapper.create();
        if (deptIds != null && !deptIds.isEmpty()) {
            wrapper.and(PostEntity::getDeptId).in(deptIds);
        }
        wrapper.and(PostEntity::getName).like(request.getName(), request.getName() != null)
               .and(PostEntity::getStatus).eq(request.getStatus(), request.getStatus() != null)
               .orderBy(PostEntity::getSort, true)
               .orderBy(PostEntity::getId, false);
        return page(new Page<>(request.getPageNum(), request.getPageSize()), wrapper);
    }
}
