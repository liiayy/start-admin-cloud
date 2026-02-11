package cn.muziseo.service.system.module.auth.manager;

import cn.muziseo.common.db.service.impl.BaseServiceImpl;
import cn.muziseo.service.system.module.auth.repository.entity.DeptEntity;
import cn.muziseo.service.system.module.auth.repository.mapper.DeptMapper;
import org.springframework.stereotype.Service;

/**
 * 部门表 Manager 层
 * <p>
 * 提供部门表的基础数据库操作，继承 MyBatis-Flex 的 BaseServiceImpl
 *
 * @author 木子软件
 * @Date 2026-02-11
 * @Wechat liiayy
 * @Email 773582348@qq.com
 * @Copyright <a href="https://code.muziseo.cn">木子软件</a>
 */
@Service
public class DeptManager extends BaseServiceImpl<DeptMapper, DeptEntity> {
}
