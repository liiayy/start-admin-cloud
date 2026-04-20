package cn.muziseo.service.system.module.monitor.manager;

import cn.muziseo.common.db.service.impl.BaseServiceImpl;
import cn.muziseo.service.system.module.monitor.repository.entity.LoginLogEntity;
import cn.muziseo.service.system.module.monitor.repository.mapper.LoginLogMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Service;

/**
 * 登录日志管理
 */
@Service
public class LoginLogManager extends BaseServiceImpl<LoginLogMapper, LoginLogEntity> {

    /**
     * 分页查询
     */
    public Page<LoginLogEntity> pageLog(int pageNum, int pageSize, LoginLogEntity query) {
        QueryWrapper wrapper = QueryWrapper.create()
                .where(LoginLogEntity::getUsername).like(query.getUsername(), query.getUsername() != null)
                .and(LoginLogEntity::getStatus).eq(query.getStatus(), query.getStatus() != null)
                .and(LoginLogEntity::getLoginIp).like(query.getLoginIp(), query.getLoginIp() != null)
                .orderBy(LoginLogEntity::getId, false);
        return page(new Page<>(pageNum, pageSize), wrapper);
    }
}
