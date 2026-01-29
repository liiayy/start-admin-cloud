package cn.muziseo.service.system.module.auth.manager;

import cn.muziseo.service.system.module.auth.repository.entity.MenuEntity;
import cn.muziseo.service.system.module.auth.repository.mapper.MenuMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 菜单表 Manager 层
 */
@Service
public class MenuManager extends ServiceImpl<MenuMapper, MenuEntity> {
}
