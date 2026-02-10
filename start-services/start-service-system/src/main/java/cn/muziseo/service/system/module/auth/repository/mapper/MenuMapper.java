package cn.muziseo.service.system.module.auth.repository.mapper;

import cn.muziseo.service.system.module.auth.repository.entity.MenuEntity;
import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 菜单表 Mapper 接口
 * <p>
 * 继承 MyBatis-Plus 的 BaseMapper，提供菜单表的基础数据库操作
 *
 * @author 木子软件
 * @Date 2026-01-29
 * @Wechat liiayy
 * @Email 773582348@qq.com
 * @Copyright <a href="https://code.muziseo.cn">木子软件</a>
 */
@Mapper
public interface MenuMapper extends BaseMapper<MenuEntity> {
}
