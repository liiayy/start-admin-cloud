package cn.muziseo.service.system.module.auth.manager;

import cn.muziseo.common.db.service.impl.BaseServiceImpl;
import cn.muziseo.service.system.module.auth.repository.entity.UserRoleEntity;
import cn.muziseo.service.system.module.auth.repository.mapper.UserRoleMapper;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户角色关联管理 Manager 层
 * <p>
 * 处理用户与角色之间的多对多关联关系，提供查询、维护及批量操作功能。
 *
 * @author 木子软件
 */
@Service
public class UserRoleManager extends BaseServiceImpl<UserRoleMapper, UserRoleEntity> {

    /**
     * 根据用户ID获取其关联的所有角色ID列表
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
     * 根据角色ID获取所有关联的用户ID列表
     *
     * @param roleId 角色ID
     * @return 用户ID列表
     */
    public List<Long> getUserIdsByRoleId(Long roleId) {
        return queryChain()
                .where(UserRoleEntity::getRoleId).eq(roleId)
                .list()
                .stream()
                .map(UserRoleEntity::getUserId)
                .collect(Collectors.toList());
    }

    /**
     * 批量查询多个用户的角色关联信息
     *
     * @param userIds 用户ID列表
     * @return 角色关联实体列表
     */
    public List<UserRoleEntity> listByUserIds(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return List.of();
        }
        return queryChain()
                .where(UserRoleEntity::getUserId).in(userIds)
                .list();
    }


    /**
     * 根据多个角色ID获取去重后的用户ID列表
     *
     * @param roleIds 角色ID列表
     * @return 关联的用户ID列表（已去重）
     */
    public List<Long> getUserIdsByRoleIds(List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return List.of();
        }
        return queryChain()
                .where(UserRoleEntity::getRoleId).in(roleIds)
                .list()
                .stream()
                .map(UserRoleEntity::getUserId)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 根据用户ID删除其所有的角色关联记录
     *
     * @param userId 用户ID
     */
    public void deleteByUserId(Long userId) {
        remove(QueryWrapper.create()
                .where(UserRoleEntity::getUserId).eq(userId));
    }

    /**
     * 根据角色ID删除其所有的用户关联记录
     *
     * @param roleId 角色ID
     */
    public void deleteByRoleId(Long roleId) {
        remove(QueryWrapper.create()
                .where(UserRoleEntity::getRoleId).eq(roleId));
    }

    /**
     * 统计分配了指定角色的用户总数
     *
     * @param roleId 角色ID
     * @return 用户数量
     */
    public long countByRoleId(Long roleId) {
        return count(QueryWrapper.create()
                .where(UserRoleEntity::getRoleId).eq(roleId));
    }

    /**
     * 批量为用户分配多个角色
     * <p>
     * 该方法直接执行批量插入，调用前应确保旧的关联已根据需要清理。
     *
     * @param userId  用户ID
     * @param roleIds 角色ID列表
     */
    public void batchInsert(Long userId, List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return;
        }
        List<UserRoleEntity> entities = roleIds.stream()
                .map(roleId -> {
                    UserRoleEntity entity = new UserRoleEntity();
                    entity.setUserId(userId);
                    entity.setRoleId(roleId);
                    return entity;
                })
                .collect(Collectors.toList());
        saveBatch(entities);
    }
}
