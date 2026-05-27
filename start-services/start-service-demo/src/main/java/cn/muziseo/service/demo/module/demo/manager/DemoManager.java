package cn.muziseo.service.demo.module.demo.manager;

import cn.muziseo.common.db.service.impl.BaseServiceImpl;
import cn.muziseo.service.demo.module.demo.repository.entity.DemoEntity;
import cn.muziseo.service.demo.module.demo.repository.mapper.DemoMapper;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Service;

/**
 * 演示数据管理层 Manager
 *
 * @author Antigravity
 */
@Service
public class DemoManager extends BaseServiceImpl<DemoMapper, DemoEntity> {

    /**
     * 校验名称是否重复
     *
     * @param name      名称
     * @param excludeId 排除的 ID
     * @return 是否存在
     */
    public boolean existsByName(String name, Long excludeId) {
        QueryWrapper qw = QueryWrapper.create().where(DemoEntity::getName).eq(name);
        if (excludeId != null) {
            qw.and(DemoEntity::getId).ne(excludeId);
        }
        return exists(qw);
    }
}
