package cn.muziseo.service.system.module.social.manager;

import cn.muziseo.common.db.service.impl.BaseServiceImpl;
import cn.muziseo.service.system.module.social.repository.entity.SocialUserEntity;
import cn.muziseo.service.system.module.social.repository.mapper.SocialUserMapper;
import org.springframework.stereotype.Service;

/**
 * 社交用户绑定 Manager 层
 *
 * @author 木子软件
 */
@Service
public class SocialUserManager extends BaseServiceImpl<SocialUserMapper, SocialUserEntity> {

    /**
     * 根据平台类型和第三方唯一标识获取绑定信息
     *
     * @param source 平台类型
     * @param uuid   第三方唯一标识
     * @return 绑定实体
     */
    public SocialUserEntity getBySourceAndUuid(String source, String uuid) {
        return queryChain()
                .where(SocialUserEntity::getSource).eq(source)
                .and(SocialUserEntity::getUuid).eq(uuid)
                .one();
    }

    /**
     * 根据系统用户ID获取绑定列表
     *
     * @param userId 系统用户ID
     * @return 绑定列表
     */
    public java.util.List<SocialUserEntity> listByUserId(Long userId) {
        return queryChain()
                .where(SocialUserEntity::getUserId).eq(userId)
                .list();
    }
}
