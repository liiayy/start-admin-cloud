package cn.muziseo.service.system.module.notice.service;

import cn.muziseo.common.db.page.PageResponse;
import cn.muziseo.service.system.module.notice.controller.request.NoticeCreateRequest;
import cn.muziseo.service.system.module.notice.controller.request.NoticePageRequest;
import cn.muziseo.service.system.module.notice.controller.request.NoticeUpdateRequest;
import cn.muziseo.service.system.module.notice.controller.vo.NoticeVO;

import java.util.List;

/**
 * 通知公告业务接口
 * 
 * @author 木子软件
 */
public interface NoticeService {

    /**
     * 分页查询通知公告
     */
    PageResponse<NoticeVO> pageNotice(NoticePageRequest request);

    /**
     * 查询通知公告详情
     */
    NoticeVO getNotice(Long id);

    /**
     * 创建通知公告
     */
    void createNotice(NoticeCreateRequest request);

    /**
     * 更新通知公告
     */
    void updateNotice(NoticeUpdateRequest request);

    /**
     * 删除通知公告
     */
    void deleteNotice(Long id);

    /**
     * 发布通知公告 (触发 WebSocket 广播)
     */
    void publishNotice(Long id);

    /**
     * 获取当前用户的未读公告列表
     */
    List<NoticeVO> listUnreadNotices();

    /**
     * 标记公告为已读
     */
    void markAsRead(Long noticeId);
}
