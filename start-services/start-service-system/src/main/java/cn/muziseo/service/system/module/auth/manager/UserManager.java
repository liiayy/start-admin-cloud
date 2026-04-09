package cn.muziseo.service.system.module.auth.manager;

import cn.muziseo.common.db.service.impl.BaseServiceImpl;
import cn.muziseo.service.system.module.auth.controller.request.UserPageRequest;
import cn.muziseo.service.system.module.auth.repository.entity.UserEntity;
import cn.muziseo.service.system.module.auth.repository.mapper.UserMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 用户表 Manager 层
 * <p>
 * 提供用户表的数据查询和基础数据库操作
 *
 * @author 木子软件
 */
@Service
public class UserManager extends BaseServiceImpl<UserMapper, UserEntity> {

    /**
     * 根据用户名获取用户
     */
    public UserEntity getByUsername(String username) {
        return queryChain()
                .where(UserEntity::getUsername).eq(username)
                .one();
    }

    /**
     * 检查用户名是否存在
     */
    public boolean existsByUsername(String username) {
        return exists(QueryWrapper.create()
                .where(UserEntity::getUsername).eq(username));
    }

    /**
     * 检查手机号是否存在（排除指定用户ID）
     */
    public boolean existsByMobile(String mobile, Long excludeUserId) {
        QueryWrapper wrapper = QueryWrapper.create()
                .where(UserEntity::getMobile).eq(mobile);
        if (excludeUserId != null) {
            wrapper.and(UserEntity::getId).ne(excludeUserId);
        }
        return exists(wrapper);
    }

    /**
     * 根据部门ID获取用户列表
     */
    public java.util.List<UserEntity> listByDeptId(Long deptId) {
        return list(QueryWrapper.create()
                .where(UserEntity::getDeptId).eq(deptId));
    }

    /**
     * 更新最后登录信息
     */
    public void updateLoginInfo(Long userId, String loginIp) {
        UserEntity entity = new UserEntity();
        entity.setId(userId);
        entity.setLoginIp(loginIp);
        entity.setLoginDate(LocalDateTime.now());
        updateById(entity);
    }

    /**
     * 分页查询用户
     */
    public Page<UserEntity> pageUser(UserPageRequest request) {
        QueryWrapper wrapper = QueryWrapper.create()
                .where(UserEntity::getUsername).like(request.getUsername(), request.getUsername() != null)
                .and(UserEntity::getMobile).like(request.getMobile(), request.getMobile() != null)
                .and(UserEntity::getStatus).eq(request.getStatus(), request.getStatus() != null)
                .and(UserEntity::getDeptId).eq(request.getDeptId(), request.getDeptId() != null)
                .orderBy(UserEntity::getId, false);

        return page(new Page<>(request.getPageNum(), request.getPageSize()), wrapper);
    }
}
