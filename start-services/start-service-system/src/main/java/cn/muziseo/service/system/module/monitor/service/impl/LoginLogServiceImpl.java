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

    /**
     * 分页查询登录日志
     *
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @param query    查询条件
     * @return 分页结果
     */
    @Override
    public PageResponse<LoginLogEntity> page(int pageNum, int pageSize, LoginLogEntity query) {
        Page<LoginLogEntity> page = loginLogManager.pageLog(pageNum, pageSize, query);
        PageResponse<LoginLogEntity> response = new PageResponse<>();
        response.setList(page.getRecords());
        response.setTotal(page.getTotalRow());
        return response;
    }

    /**
     * 批量删除登录日志
     *
     * @param ids 日志 ID 数组
     */
    @Override
    public void deleteByIds(Long[] ids) {
        loginLogManager.removeByIds(Arrays.asList(ids));
    }

    /**
     * 清空登录日志
     */
    @Override
    public void clean() {
        Db.updateBySql("TRUNCATE TABLE system_login_log");
    }
}
