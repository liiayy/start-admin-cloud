package cn.muziseo.service.system.module.demo.manager;

import cn.muziseo.common.db.service.impl.BaseServiceImpl;
import cn.muziseo.service.system.module.demo.repository.entity.DemoEntity;
import cn.muziseo.service.system.module.demo.repository.mapper.DemoMapper;
import org.springframework.stereotype.Service;

@Service
public class DemoManager extends BaseServiceImpl<DemoMapper, DemoEntity> {

}
