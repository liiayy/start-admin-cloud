package cn.muziseo.service.system.module.auth.manager;

import cn.muziseo.service.system.module.auth.repository.entity.MenuEntity;
import cn.muziseo.service.system.module.auth.repository.mapper.MenuMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 菜单表 Manager 层
 * <p>
 * 提供菜单表的基础数据库操作，继承 MyBatis-Plus 的 ServiceImpl
 *
 * @author 木子软件
 * @Date 2026-01-07
 * @Wechat liiayy
 * @Email 773582348@qq.com
 * @Copyright <a href="https://code.muziseo.cn">木子软件</a>
 */
@Service
public class MenuManager extends ServiceImpl<MenuMapper, MenuEntity> {
}
