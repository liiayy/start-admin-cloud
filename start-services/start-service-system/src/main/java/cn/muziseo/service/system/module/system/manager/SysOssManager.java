package cn.muziseo.service.system.module.system.manager;

import cn.muziseo.common.db.service.impl.BaseServiceImpl;
import cn.muziseo.service.system.module.system.repository.entity.SysOssEntity;
import cn.muziseo.service.system.module.system.repository.mapper.SysOssMapper;
import org.springframework.stereotype.Service;

/**
 * OSS 文件元数据管理 Manager 层
 * <p>
 * 处理已上传文件的元数据持久化，包括文件路径、URL、文件类型及关联的配置信息。
 *
 * @author 木子软件
 */
@Service
public class SysOssManager extends BaseServiceImpl<SysOssMapper, SysOssEntity> {
}
