package cn.muziseo.service.system.module.auth.manager;

import cn.muziseo.common.db.service.impl.BaseServiceImpl;
import cn.muziseo.service.system.module.auth.repository.entity.UserRoleEntity;
import cn.muziseo.service.system.module.auth.repository.mapper.UserRoleMapper;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户角色关联 Manager 层
 * <p>
 * 提供用户角色关联表的数据查询和基础数据库操作
 *
 * @author 木子软件
 * @Date 2026-01-07
 * @Wechat liiayy
 * @Email 773582348@qq.com
 * @Copyright <a href="https://code.muziseo.cn">木子软件</a>
 */
@Service
public class UserRoleManager extends BaseServiceImpl<UserRoleMapper, UserRoleEntity> {

    /**
     * 根据用户ID获取角色ID列表
     *
     * @param userId 用户ID
     * @return 角色ID列表
     */
    public List<Long> getRoleIdsByUserId(Long userId) {
        return queryChain()
                .where(UserRoleEntity::getUserId).eq(userId)
                .list()
                .stream()
                .map(UserRoleEntity::getRoleId)
                .collect(Collectors.toList());
    }

    /**
     * 根据用户ID删除角色关联
     *
     * @param userId 用户ID
     */
    public void deleteByUserId(Long userId) {
        remove(QueryWrapper.create()
                .where(UserRoleEntity::getUserId).eq(userId));
    }

    /**
     * 根据角色ID删除用户关联
     *
     * @param roleId 角色ID
     */
    public void deleteByRoleId(Long roleId) {
        remove(QueryWrapper.create()
                .where(UserRoleEntity::getRoleId).eq(roleId));
    }
}
