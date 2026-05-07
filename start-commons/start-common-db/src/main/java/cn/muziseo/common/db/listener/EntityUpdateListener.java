package cn.muziseo.common.db.listener;

import cn.dev33.satoken.stp.StpUtil;
import cn.muziseo.common.core.constant.SaSessionConstants;
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
            baseEntity.setUpdater(getUserName());
        }
    }

    /**
     * 获取当前登录用户名，未登录返回 "system"
     */
    private String getUserName() {
        try {
            if (StpUtil.isLogin()) {
                // 优先从 Session 获取用户名字符串
                String username = StpUtil.getSession().getString(SaSessionConstants.USERNAME);
                if (username != null && !username.isEmpty()) {
                    return username;
                }
                // 兜底返回登录 ID
                return StpUtil.getLoginIdAsString();
            }
            return "system";
        } catch (Exception e) {
            return "system";
        }
    }
}
