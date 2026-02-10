package cn.muziseo.common.db.listener;

import cn.muziseo.common.db.entity.BaseEntity;
import com.mybatisflex.annotation.UpdateListener;

import java.time.LocalDateTime;

/**
 * 实体类更新监听器
 * <p>
 * 在实体更新数据库前自动填充公共字段，包括更新时间和更新人
 * 继承 MyBatis-Flex 的 UpdateListener 接口实现自动填充功能
 * </p>
 *
 * @author dataprince数据小王子
 * @Date 2026-01-15
 * @Wechat liiayy
 * @Email 773582348@qq.com
 * @Copyright <a href="https://code.muziseo.cn">木子软件</a>
 */
public class EntityUpdateListener implements UpdateListener {

    /**
     * 实体更新前的回调方法
     * <p>
     * 自动填充 BaseEntity 的公共字段：
     * - updateTime：更新时间
     * - updater：更新人 ID（待实现，需要获取当前登录用户）
     * </p>
     *
     * @param entity 待更新的实体对象
     */
    @Override
    public void onUpdate(Object entity) {
        if (entity instanceof BaseEntity baseEntity) {
            LocalDateTime now = LocalDateTime.now();
            baseEntity.setUpdateTime(now);
            // TODO: 获取当前登录用户ID并设置
            // baseEntity.setUpdater(LoginHelper.getUserId());
        }
    }
}
