package cn.muziseo.service.system.module.auth.repository.mapper;

import cn.muziseo.service.system.module.auth.repository.entity.UserEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户表 Mapper 接口
 */
@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {
}
