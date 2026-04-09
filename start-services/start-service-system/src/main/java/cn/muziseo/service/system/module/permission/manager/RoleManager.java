package cn.muziseo.service.system.module.permission.manager;

import cn.muziseo.common.db.service.impl.BaseServiceImpl;
import cn.muziseo.service.system.module.permission.controller.request.RolePageRequest;
import cn.muziseo.service.system.module.permission.repository.entity.RoleEntity;
import cn.muziseo.service.system.module.permission.repository.mapper.RoleMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Service;

/**
 * 角色表 Manager 层
 * <p>
 * 提供角色表的数据查询和基础数据库操作
 *
 * @author 木子软件
 * @Date 2026-01-07
 * @Wechat liiayy
 * @Email 773582348@qq.com
 * @Copyright <a href="https://code.muziseo.cn">木子软件</a>
 */
@Service
public class RoleManager extends BaseServiceImpl<RoleMapper, RoleEntity> {

    /**
     * 根据角色代码获取角色
     */
    public RoleEntity getByCode(String code) {
        return queryChain()
                .where(RoleEntity::getCode).eq(code)
                .one();
    }

    /**
     * 检查角色代码是否存在（排除指定ID）
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
     * 分页查询角色
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
