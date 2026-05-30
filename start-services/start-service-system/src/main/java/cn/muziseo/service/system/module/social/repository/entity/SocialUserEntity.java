package cn.muziseo.service.system.module.social.repository.entity;

import cn.muziseo.common.db.entity.BaseEntity;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.*;

/**
 * 社交用户绑定实体
 * <p>
 * 对应数据库表 system_social_user，用于存储第三方平台用户信息及其与系统本地用户的绑定关系
 *
 * @author 木子软件
 */
@Table("system_social_user")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SocialUserEntity extends BaseEntity {

    /**
     * 主键
     */
    @Id(keyType = KeyType.Generator, value = "snowFlakeId")
    private Long id;

    /**
     * 系统用户ID
     */
    private Long userId;

    /**
     * 社交平台类型 (github, gitee等)
     */
    private String source;

    /**
     * 第三方平台唯一标识
     */
    private String uuid;

    /**
     * 第三方平台用户名
     */
    private String username;

    /**
     * 第三方平台昵称
     */
    private String nickname;

    /**
     * 第三方平台头像
     */
    private String avatar;

    /**
     * 访问令牌
     */
    private String accessToken;

    /**
     * 刷新令牌
     */
    private String refreshToken;

    /**
     * 第三方原始用户信息
     */
    private String rawUserInfo;

}
