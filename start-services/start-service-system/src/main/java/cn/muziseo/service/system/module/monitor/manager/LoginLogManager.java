package cn.muziseo.service.system.module.monitor.manager;

import cn.muziseo.common.db.service.impl.BaseServiceImpl;
import cn.muziseo.service.system.module.monitor.repository.entity.LoginLogEntity;
import cn.muziseo.service.system.module.monitor.repository.mapper.LoginLogMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Service;

/**
 * 登录日志管理 Manager 层
 * <p>
 * 处理系统登录日志的持久化，提供分页查询及日志记录功能。
 *
 * @author 木子软件
 */
@Service
public class LoginLogManager extends BaseServiceImpl<LoginLogMapper, LoginLogEntity> {

    /**
     * 分页查询登录日志
     *
     * @param pageNum  当前页码
     * @param pageSize 每页显示数量
     * @param query    筛选条件实体
     * @return 分页结果对象
     */
    public Page<LoginLogEntity> pageLog(int pageNum, int pageSize, LoginLogEntity query) {
        QueryWrapper wrapper = QueryWrapper.create()
                .where(LoginLogEntity::getUsername).like(query.getUsername(), query.getUsername() != null)
                .and(LoginLogEntity::getStatus).eq(query.getStatus(), query.getStatus() != null)
                .and(LoginLogEntity::getLoginIp).like(query.getLoginIp(), query.getLoginIp() != null)
                .orderBy(LoginLogEntity::getId, false);
        return page(new Page<>(pageNum, pageSize), wrapper);
    }

    /**
     * 分页查询个人登录日志（精确匹配用户名）
     *
     * @param pageNum  当前页码
     * @param pageSize 每页显示数量
     * @param username 用户名
     * @return 分页结果对象
     */
    public Page<LoginLogEntity> personalPageLog(int pageNum, int pageSize, String username) {
        QueryWrapper wrapper = QueryWrapper.create()
                .where(LoginLogEntity::getUsername).eq(username)
                .orderBy(LoginLogEntity::getId, false);
        return page(new Page<>(pageNum, pageSize), wrapper);
    }
}
