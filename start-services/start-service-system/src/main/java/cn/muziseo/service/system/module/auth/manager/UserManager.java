package cn.muziseo.service.system.module.auth.manager;

import cn.muziseo.common.db.service.impl.BaseServiceImpl;
import cn.muziseo.service.system.module.auth.repository.entity.UserEntity;
import cn.muziseo.service.system.module.auth.repository.mapper.UserMapper;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Service;

/**
 * 用户表 Manager 层
 * <p>
 * 提供用户表的数据查询和基础数据库操作
 *
 * @author 木子软件
 * @Date 2026-01-07
 * @Wechat liiayy
 * @Email 773582348@qq.com
 * @Copyright <a href="https://code.muziseo.cn">木子软件</a>
 */
@Service
public class UserManager extends BaseServiceImpl<UserMapper, UserEntity> {

    /**
     * 根据用户名获取用户
     *
     * @param username 用户名
     * @return 用户实体
     */
    public UserEntity getByUsername(String username) {
        return queryChain()
                .where(UserEntity::getUsername).eq(username)
                .one();
    }

    /**
     * 检查用户名是否存在
     *
     * @param username 用户名
     * @return 是否存在
     */
    public boolean existsByUsername(String username) {
        return exists(QueryWrapper.create()
                .where(UserEntity::getUsername).eq(username));
    }

    /**
     * 根据部门ID获取用户列表
     *
     * @param deptId 部门ID
     * @return 用户列表
     */
    public java.util.List<UserEntity> listByDeptId(Long deptId) {
        return list(QueryWrapper.create()
                .where(UserEntity::getDeptId).eq(deptId));
    }
}
