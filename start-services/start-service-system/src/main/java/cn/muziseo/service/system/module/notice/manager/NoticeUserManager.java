package cn.muziseo.service.system.module.notice.manager;

import cn.muziseo.common.db.service.impl.BaseServiceImpl;
import cn.muziseo.service.system.module.notice.repository.entity.NoticeUserEntity;
import cn.muziseo.service.system.module.notice.repository.mapper.NoticeUserMapper;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户通知公告关联管理 Manager 层
 * <p>
 * 处理用户与通知公告之间的关联状态（如已读/未读），提供状态维护及关联查询功能。
 *
 * @author 木子软件
 */
@Service
public class NoticeUserManager extends BaseServiceImpl<NoticeUserMapper, NoticeUserEntity> {

    /**
     * 获取用户针对特定通知公告的查阅记录
     *
     * @param noticeId 通知公告ID
     * @param userId   用户ID
     * @return 通知公告用户关联实体，若无记录则返回 null
     */
    public NoticeUserEntity getByNoticeAndUser(Long noticeId, Long userId) {
        return queryChain()
                .where(NoticeUserEntity::getNoticeId).eq(noticeId)
                .and(NoticeUserEntity::getUserId).eq(userId)
                .one();
    }

    /**
     * 获取指定用户的所有未读通知公告记录
     *
     * @param userId 用户ID
     * @return 未读的通知公告用户关联实体列表
     */
    public List<NoticeUserEntity> listUnreadByUser(Long userId) {
        return list(QueryWrapper.create()
                .where(NoticeUserEntity::getUserId).eq(userId)
                .and(NoticeUserEntity::getIsRead).eq(false));
    }

    /**
     * 将指定通知公告标记为用户已读
     * <p>
     * 如果记录已存在且为未读状态，则更新为已读；如果记录不存在，则创建一条已读记录。
     *
     * @param noticeId 通知公告ID
     * @param userId   用户ID
     * @return 操作是否成功
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
