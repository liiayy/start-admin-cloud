package cn.muziseo.system.module.demo.service.impl;

import cn.muziseo.system.module.demo.manager.DemoManager;
import cn.muziseo.system.module.demo.repository.entity.DemoEntity;
import cn.muziseo.system.module.demo.service.DemoService;
import io.swagger.v3.oas.annotations.servers.Server;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DemoServiceImpl implements DemoService {

    @Resource
    DemoManager demoManager;

    @Override
    public List<DemoEntity> getAll() {
        return demoManager.list();
    }
}
