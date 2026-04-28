package cn.muziseo.service.system.module.notice.manager;

import cn.muziseo.common.db.service.impl.BaseServiceImpl;
import cn.muziseo.service.system.module.notice.repository.entity.NoticeUserEntity;
import cn.muziseo.service.system.module.notice.repository.mapper.NoticeUserMapper;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户通知公告关联 Manager 层
 * 
 * @author 木子软件
 */
@Service
public class NoticeUserManager extends BaseServiceImpl<NoticeUserMapper, NoticeUserEntity> {

    /**
     * 获取用户针对某条公告的阅读记录
     */
    public NoticeUserEntity getByNoticeAndUser(Long noticeId, Long userId) {
        return queryChain()
                .where(NoticeUserEntity::getNoticeId).eq(noticeId)
                .and(NoticeUserEntity::getUserId).eq(userId)
                .one();
    }

    /**
     * 获取用户所有未读记录
     */
    public List<NoticeUserEntity> listUnreadByUser(Long userId) {
        return list(QueryWrapper.create()
                .where(NoticeUserEntity::getUserId).eq(userId)
                .and(NoticeUserEntity::getIsRead).eq(false));
    }

    /**
     * 标记为已读
     */
    public boolean markRead(Long noticeId, Long userId) {
        NoticeUserEntity existing = getByNoticeAndUser(noticeId, userId);
        if (existing != null) {
            if (Boolean.TRUE.equals(existing.getIsRead())) {
                return true; // 已经是已读
            }
            existing.setIsRead(true);
            existing.setReadTime(LocalDateTime.now());
            return updateById(existing);
        } else {
            // 没有映射说明初次查阅，持久化状态机
            NoticeUserEntity entity = NoticeUserEntity.builder()
                    .noticeId(noticeId)
                    .userId(userId)
                    .isRead(true)
                    .readTime(LocalDateTime.now())
                    .build();
            return save(entity);
        }
    }
}
