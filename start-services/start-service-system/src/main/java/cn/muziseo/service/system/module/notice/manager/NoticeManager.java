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
 * 通知公告管理 Manager 层
 * <p>
 * 处理系统通知、公告的持久化逻辑，提供分页查询及列表展示功能。
 *
 * @author 木子软件
 */
@Service
public class NoticeManager extends BaseServiceImpl<NoticeMapper, NoticeEntity> {

    /**
     * 分页查询通知公告列表
     *
     * @param request 分页及筛选条件
     * @return 分页结果对象
     */
    public Page<NoticeEntity> pageNotice(NoticePageRequest request) {
        return page(new Page<>(request.getPageNum(), request.getPageSize()), buildQueryWrapper(request));
    }

    /**
     * 根据条件查询通知公告列表
     *
     * @param request 筛选条件
     * @return 通知公告实体列表
     */
    public List<NoticeEntity> listNotice(NoticePageRequest request) {
        return list(buildQueryWrapper(request));
    }

    /**
     * 构造通用的通知公告查询条件
     *
     * @param request 查询请求参数
     * @return QueryWrapper 构造器
     */
    private QueryWrapper buildQueryWrapper(NoticePageRequest request) {
        return QueryWrapper.create()
                .where(NoticeEntity::getTitle).like(request.getTitle(), request.getTitle() != null && !request.getTitle().isEmpty())
                .and(NoticeEntity::getType).eq(request.getType(), request.getType() != null)
                .and(NoticeEntity::getStatus).eq(request.getStatus(), request.getStatus() != null)
                .orderBy(NoticeEntity::getId, false);
    }
}
