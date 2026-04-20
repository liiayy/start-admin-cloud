package cn.muziseo.service.system.module.monitor.service.impl;

import cn.muziseo.common.db.page.PageResponse;
import cn.muziseo.service.system.module.monitor.manager.LoginLogManager;
import cn.muziseo.service.system.module.monitor.repository.entity.LoginLogEntity;
import cn.muziseo.service.system.module.monitor.service.LoginLogService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.row.Db;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * 登录日志服务实现
 */
@Service
@RequiredArgsConstructor
public class LoginLogServiceImpl implements LoginLogService {

    private final LoginLogManager loginLogManager;

    @Override
    public PageResponse<LoginLogEntity> page(int pageNum, int pageSize, LoginLogEntity query) {
        Page<LoginLogEntity> page = loginLogManager.pageLog(pageNum, pageSize, query);
        PageResponse<LoginLogEntity> response = new PageResponse<>();
        response.setList(page.getRecords());
        response.setTotal(page.getTotalRow());
        return response;
    }

    @Override
    public void deleteByIds(Long[] ids) {
        loginLogManager.removeByIds(Arrays.asList(ids));
    }

    @Override
    public void clean() {
        Db.updateBySql("TRUNCATE TABLE system_login_log");
    }
}
