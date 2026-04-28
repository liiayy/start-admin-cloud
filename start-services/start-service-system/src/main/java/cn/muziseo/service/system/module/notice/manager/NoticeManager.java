package cn.muziseo.service.system.module.notice.manager;

import cn.muziseo.common.db.service.impl.BaseServiceImpl;
import cn.muziseo.service.system.module.notice.controller.request.NoticePageRequest;
import cn.muziseo.service.system.module.notice.repository.entity.NoticeEntity;
import cn.muziseo.service.system.module.notice.repository.mapper.NoticeMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 通知公告 Manager 层
 * 
 * @author 木子软件
 */
@Service
public class NoticeManager extends BaseServiceImpl<NoticeMapper, NoticeEntity> {

    public Page<NoticeEntity> pageNotice(NoticePageRequest request) {
        return page(new Page<>(request.getPageNum(), request.getPageSize()), buildQueryWrapper(request));
    }

    public List<NoticeEntity> listNotice(NoticePageRequest request) {
        return list(buildQueryWrapper(request));
    }

    private QueryWrapper buildQueryWrapper(NoticePageRequest request) {
        return QueryWrapper.create()
                .where(NoticeEntity::getTitle).like(request.getTitle(), request.getTitle() != null && !request.getTitle().isEmpty())
                .and(NoticeEntity::getType).eq(request.getType(), request.getType() != null)
                .and(NoticeEntity::getStatus).eq(request.getStatus(), request.getStatus() != null)
                .orderBy(NoticeEntity::getId, false);
    }
}
