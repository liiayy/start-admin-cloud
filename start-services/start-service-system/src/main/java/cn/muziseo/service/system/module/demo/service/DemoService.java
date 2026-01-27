package cn.muziseo.service.system.module.demo.service;

import cn.muziseo.service.system.module.demo.repository.entity.DemoEntity;

import java.util.List;

public interface DemoService {
    /**
     * 获取全部
     *
     * @return Demo列表
     */
    List<DemoEntity> getAll();
}
