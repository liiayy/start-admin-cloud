package cn.muziseo.service.system.module.social.manager;

import cn.muziseo.common.db.service.impl.BaseServiceImpl;
import cn.muziseo.service.system.module.social.repository.entity.SocialUserEntity;
import cn.muziseo.service.system.module.social.repository.mapper.SocialUserMapper;
import org.springframework.stereotype.Service;

/**
 * 社交用户绑定管理 Manager 层
 * <p>
 * 处理第三方社交平台（如 GitHub, WeChat）用户与系统本地账号的绑定关系，提供查询及关联维护功能。
 *
 * @author 木子软件
 */
@Service
public class SocialUserManager extends BaseServiceImpl<SocialUserMapper, SocialUserEntity> {

    /**
     * 根据平台类型和第三方唯一标识获取社交用户绑定信息
     * <p>
     * 该方法通常用于社交登录流程中，通过第三方平台提供的唯一标识匹配系统本地绑定的用户信息。
     *
     * @param source 社交平台标识（如：github, gitee, wechat 等）
     * @param uuid   第三方平台提供的用户唯一标识
     * @return 社交用户绑定实体对象，若该第三方账号未绑定系统用户则返回 null
     */
    public SocialUserEntity getBySourceAndUuid(String source, String uuid) {
        return queryChain()
                .where(SocialUserEntity::getSource).eq(source)
                .and(SocialUserEntity::getUuid).eq(uuid)
                .one();
    }

    /**
     * 根据本地系统用户 ID 获取其所有的社交账号绑定列表
     *
     * @param userId 本地系统用户 ID
     * @return 社交用户绑定实体列表
     */
    public java.util.List<SocialUserEntity> listByUserId(Long userId) {
        return queryChain()
                .where(SocialUserEntity::getUserId).eq(userId)
                .list();
    }
}
