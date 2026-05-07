package cn.muziseo.common.db.listener;

import cn.dev33.satoken.stp.StpUtil;
import cn.muziseo.common.core.constant.SaSessionConstants;
import cn.muziseo.common.db.entity.BaseEntity;
import com.mybatisflex.annotation.InsertListener;

import java.time.LocalDateTime;

/**
 * 实体类插入监听器
 * <p>
 * 在实体插入数据库前自动填充公共字段，包括创建时间、更新时间、创建人、更新人
 *
 * @author 木子软件
 */
public class EntityInsertListener implements InsertListener {

    @Override
    public void onInsert(Object entity) {
        if (entity instanceof BaseEntity baseEntity) {
            LocalDateTime now = LocalDateTime.now();
            if (baseEntity.getCreateTime() == null) {
                baseEntity.setCreateTime(now);
            }
            if (baseEntity.getUpdateTime() == null) {
                baseEntity.setUpdateTime(now);
            }
            String userName = getUserName();
            if (baseEntity.getCreator() == null || baseEntity.getCreator().isEmpty()) {
                baseEntity.setCreator(userName);
            }
            if (baseEntity.getUpdater() == null || baseEntity.getUpdater().isEmpty()) {
                baseEntity.setUpdater(userName);
            }
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
