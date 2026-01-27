package cn.muziseo.service.system.module.demo.manager;

import cn.muziseo.service.system.module.demo.repository.entity.DemoEntity;
import cn.muziseo.service.system.module.demo.repository.mapper.DemoMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;


@Service
public class DemoManager extends ServiceImpl<DemoMapper, DemoEntity> {

}
