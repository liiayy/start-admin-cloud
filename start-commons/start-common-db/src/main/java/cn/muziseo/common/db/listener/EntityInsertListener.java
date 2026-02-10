package cn.muziseo.common.db.listener;

import cn.muziseo.common.db.entity.BaseEntity;
import com.mybatisflex.annotation.InsertListener;

import java.time.LocalDateTime;

/**
 * 实体类插入监听器
 * <p>
 * 在实体插入数据库前自动填充公共字段，包括创建时间、更新时间、创建人、更新人
 * 继承 MyBatis-Flex 的 InsertListener 接口实现自动填充功能
 * </p>
 *
 * @author dataprince数据小王子
 * @Date 2026-01-15
 * @Wechat liiayy
 * @Email 773582348@qq.com
 * @Copyright <a href="https://code.muziseo.cn">木子软件</a>
 */
public class EntityInsertListener implements InsertListener {

    /**
     * 实体插入前的回调方法
     * <p>
     * 自动填充 BaseEntity 的公共字段：
     * - createTime：创建时间（如果为空）
     * - updateTime：更新时间（如果为空）
     * - creator：创建人 ID（待实现，需要获取当前登录用户）
     * - updater：更新人 ID（待实现，需要获取当前登录用户）
     * </p>
     *
     * @param entity 待插入的实体对象
     */
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
            // TODO: 获取当前登录用户ID并设置
            // if (baseEntity.getCreator() == null) {
            // baseEntity.setCreator(LoginHelper.getUserId());
            // }
            // if (baseEntity.getUpdater() == null) {
            // baseEntity.setUpdater(LoginHelper.getUserId());
            // }
        }
    }
}
