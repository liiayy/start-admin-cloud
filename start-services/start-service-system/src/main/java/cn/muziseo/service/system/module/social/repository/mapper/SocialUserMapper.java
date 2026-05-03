package cn.muziseo.service.system.module.social.repository.mapper;

import cn.muziseo.service.system.module.social.repository.entity.SocialUserEntity;
import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 社交用户绑定 Mapper 接口
 *
 * @author 木子软件
 */
@Mapper
public interface SocialUserMapper extends BaseMapper<SocialUserEntity> {
}
