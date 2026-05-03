package cn.muziseo.service.system.module.permission.manager;

import cn.muziseo.common.db.service.impl.BaseServiceImpl;
import cn.muziseo.service.system.module.permission.controller.request.RolePageRequest;
import cn.muziseo.service.system.module.permission.repository.entity.RoleEntity;
import cn.muziseo.service.system.module.permission.repository.mapper.RoleMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Service;

/**
 * 角色管理 Manager 层
 * <p>
 * 处理系统角色的持久化逻辑，提供角色查询、代码校验及分页筛选功能。
 *
 * @author 木子软件
 */
@Service
public class RoleManager extends BaseServiceImpl<RoleMapper, RoleEntity> {

    /**
     * 根据角色代码查询角色信息
     *
     * @param code 角色代码（如：admin, common）
     * @return 角色实体信息，如果不存在则返回 null
     */
    public RoleEntity getByCode(String code) {
        return queryChain()
                .where(RoleEntity::getCode).eq(code)
                .one();
    }

    /**
     * 校验角色代码是否已存在
     *
     * @param code      角色代码
     * @param excludeId 需要排除的角色 ID（用于修改时校验）
     * @return true 表示已存在，false 表示不存在
     */
    public boolean existsByCode(String code, Long excludeId) {
        QueryWrapper wrapper = QueryWrapper.create()
                .where(RoleEntity::getCode).eq(code);
        if (excludeId != null) {
            wrapper.and(RoleEntity::getId).ne(excludeId);
        }
        return exists(wrapper);
    }

    /**
     * 分页查询角色列表
     * <p>
     * 支持按角色名称、代码、状态进行模糊/精确匹配，并按显示顺序排序。
     *
     * @param request 分页及筛选条件
     * @return 分页结果对象
     */
    public Page<RoleEntity> pageRole(RolePageRequest request) {
        QueryWrapper wrapper = QueryWrapper.create()
                .where(RoleEntity::getName).like(request.getName(), request.getName() != null)
                .and(RoleEntity::getCode).like(request.getCode(), request.getCode() != null)
                .and(RoleEntity::getStatus).eq(request.getStatus(), request.getStatus() != null)
                .orderBy(RoleEntity::getSort, true)
                .orderBy(RoleEntity::getId, false);
        return page(new Page<>(request.getPageNum(), request.getPageSize()), wrapper);
    }
}
