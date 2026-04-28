package cn.muziseo.service.system.module.notice.repository.mapper;

import cn.muziseo.service.system.module.notice.repository.entity.NoticeUserEntity;
import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户通知公告关联 Mapper 接口
 * 
 * @author 木子软件
 */
@Mapper
public interface NoticeUserMapper extends BaseMapper<NoticeUserEntity> {
}
