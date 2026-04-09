package cn.muziseo.common.db.listener;

import cn.dev33.satoken.stp.StpUtil;
import cn.muziseo.common.db.entity.BaseEntity;
import com.mybatisflex.annotation.UpdateListener;

import java.time.LocalDateTime;

/**
 * 实体类更新监听器
 * <p>
 * 在实体更新数据库前自动填充公共字段，包括更新时间和更新人
 *
 * @author 木子软件
 */
public class EntityUpdateListener implements UpdateListener {

    @Override
    public void onUpdate(Object entity) {
        if (entity instanceof BaseEntity baseEntity) {
            baseEntity.setUpdateTime(LocalDateTime.now());
            baseEntity.setUpdater(getUserId());
        }
    }

    /**
     * 获取当前登录用户 ID，未登录返回 "system"
     */
    private String getUserId() {
        try {
            Object loginId = StpUtil.getLoginIdDefaultNull();
            return loginId != null ? loginId.toString() : "system";
        } catch (Exception e) {
            return "system";
        }
    }
}
