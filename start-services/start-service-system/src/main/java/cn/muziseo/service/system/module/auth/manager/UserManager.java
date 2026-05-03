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
 * 用户管理 Manager 层
 * <p>
 * 处理用户表的数据持久化逻辑，提供用户查询、状态检查及基础维护功能。
 *
 * @author 木子软件
 */
@Service
public class UserManager extends BaseServiceImpl<UserMapper, UserEntity> {

    /**
     * 根据用户名查询用户信息
     *
     * @param username 用户名
     * @return 用户实体信息，如果不存在则返回 null
     */
    public UserEntity getByUsername(String username) {
        return queryChain()
                .where(UserEntity::getUsername).eq(username)
                .one();
    }

    /**
     * 校验用户名是否已存在
     *
     * @param username      需要校验的用户名
     * @param excludeUserId 需要排除的用户ID（用于修改时校验）
     * @return true 表示已存在，false 表示不存在
     */
    public boolean existsByUsername(String username, Long excludeUserId) {
        QueryWrapper wrapper = QueryWrapper.create()
                .where(UserEntity::getUsername).eq(username);
        if (excludeUserId != null) {
            wrapper.and(UserEntity::getId).ne(excludeUserId);
        }
        return exists(wrapper);
    }

    /**
     * 校验手机号是否已存在
     *
     * @param mobile        需要校验的手机号
     * @param excludeUserId 需要排除的用户ID（用于修改时校验）
     * @return true 表示已存在，false 表示不存在
     */
    public boolean existsByMobile(String mobile, Long excludeUserId) {
        if (mobile == null || mobile.isEmpty()) {
            return false;
        }
        QueryWrapper wrapper = QueryWrapper.create()
                .where(UserEntity::getMobile).eq(mobile);
        if (excludeUserId != null) {
            wrapper.and(UserEntity::getId).ne(excludeUserId);
        }
        return exists(wrapper);
    }

    /**
     * 校验邮箱是否已存在
     *
     * @param email         需要校验的邮箱地址
     * @param excludeUserId 需要排除的用户ID（用于修改时校验）
     * @return true 表示已存在，false 表示不存在
     */
    public boolean existsByEmail(String email, Long excludeUserId) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        QueryWrapper wrapper = QueryWrapper.create()
                .where(UserEntity::getEmail).eq(email);
        if (excludeUserId != null) {
            wrapper.and(UserEntity::getId).ne(excludeUserId);
        }
        return exists(wrapper);
    }

    /**
     * 根据部门ID查询所属用户列表
     *
     * @param deptId 部门ID
     * @return 用户实体列表
     */
    public java.util.List<UserEntity> listByDeptId(Long deptId) {
        return list(QueryWrapper.create()
                .where(UserEntity::getDeptId).eq(deptId));
    }

    /**
     * 统计指定部门下的用户总数
     *
     * @param deptId 部门ID
     * @return 用户数量
     */
    public long countByDeptId(Long deptId) {
        return count(QueryWrapper.create()
                .where(UserEntity::getDeptId).eq(deptId));
    }

    /**
     * 统计持有指定岗位的用户总数
     *
     * @param postId 岗位ID
     * @return 用户数量
     */
    public long countByPostId(Long postId) {
        return count(QueryWrapper.create()
                .where("? = ANY(post_ids)", postId));
    }

    /**
     * 记录用户最后登录信息
     *
     * @param userId  用户ID
     * @param loginIp 登录时的IP地址
     */
    public void updateLoginInfo(Long userId, String loginIp) {
        UserEntity entity = new UserEntity();
        entity.setId(userId);
        entity.setLoginIp(loginIp);
        entity.setLoginDate(LocalDateTime.now());
        updateById(entity);
    }

    /**
     * 分页查询用户信息
     *
     * @param request 分页及筛选条件
     * @return 分页结果对象
     */
    public Page<UserEntity> pageUser(UserPageRequest request) {
        return page(new Page<>(request.getPageNum(), request.getPageSize()), buildQueryWrapper(request));
    }

    /**
     * 根据条件查询用户列表（不分页）
     * <p>
     * 常用于导出或全量数据处理场景。
     *
     * @param request 筛选条件
     * @return 用户实体列表
     */
    public java.util.List<UserEntity> listUser(UserPageRequest request) {
        return list(buildQueryWrapper(request));
    }

    /**
     * 构造通用的用户查询条件
     *
     * @param request 查询请求参数
     * @return QueryWrapper 构造器
     */
    private QueryWrapper buildQueryWrapper(UserPageRequest request) {
        return QueryWrapper.create()
                .where(UserEntity::getUsername).like(request.getUsername(), request.getUsername() != null)
                .and(UserEntity::getMobile).like(request.getMobile(), request.getMobile() != null)
                .and(UserEntity::getStatus).eq(request.getStatus(), request.getStatus() != null)
                .and(UserEntity::getDeptId).eq(request.getDeptId(), request.getDeptId() != null)
                .orderBy(UserEntity::getId, false);
    }
}
