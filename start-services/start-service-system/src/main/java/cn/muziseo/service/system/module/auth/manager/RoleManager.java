package cn.muziseo.service.system.module.auth.manager;

import cn.muziseo.common.db.service.impl.BaseServiceImpl;
import cn.muziseo.service.system.module.auth.repository.entity.RoleEntity;
import cn.muziseo.service.system.module.auth.repository.mapper.RoleMapper;
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
     *
     * @param code 角色代码
     * @return 角色实体
     */
    public RoleEntity getByCode(String code) {
        return queryChain()
                .where(RoleEntity::getCode).eq(code)
                .one();
    }

    /**
     * 检查角色代码是否存在
     *
     * @param code 角色代码
     * @return 是否存在
     */
    public boolean existsByCode(String code) {
        return exists(QueryWrapper.create()
                .where(RoleEntity::getCode).eq(code));
    }
}
