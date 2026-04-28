package cn.muziseo.service.system.module.notice.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.muziseo.common.core.exception.BusinessException;
import cn.dev33.satoken.stp.StpUtil;
import cn.muziseo.common.db.page.PageResponse;
import cn.muziseo.common.websocket.dto.WebSocketMessageDTO;
import cn.muziseo.common.websocket.utils.WebSocketUtils;
import cn.muziseo.service.system.module.notice.controller.request.NoticeAddRequest;
import cn.muziseo.service.system.module.notice.controller.request.NoticePageRequest;
import cn.muziseo.service.system.module.notice.controller.request.NoticeUpdateRequest;
import cn.muziseo.service.system.module.notice.controller.vo.NoticeVO;
import cn.muziseo.service.system.module.notice.manager.NoticeManager;
import cn.muziseo.service.system.module.notice.manager.NoticeUserManager;
import cn.muziseo.service.system.module.notice.repository.entity.NoticeEntity;
import cn.muziseo.service.system.module.notice.repository.entity.NoticeUserEntity;
import cn.muziseo.service.system.module.notice.service.NoticeService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 通知公告业务逻辑实现
 * 
 * @author 木子软件
 */
@Service
@Slf4j
public class NoticeServiceImpl implements NoticeService {

    @Resource
    private NoticeManager noticeManager;

    @Resource
    private NoticeUserManager noticeUserManager;

    @Override
    public PageResponse<NoticeVO> pageNotice(NoticePageRequest request) {
        Page<NoticeEntity> page = noticeManager.pageNotice(request);
        List<NoticeEntity> records = page.getRecords();
        
        List<NoticeVO> voList = records.stream()
                .map(this::toNoticeVO)
                .collect(Collectors.toList());

        PageResponse<NoticeVO> response = new PageResponse<>();
        response.setList(voList);
        response.setTotal(page.getTotalRow());
        return response;
    }

    @Override
    public NoticeVO getNotice(Long id) {
        NoticeEntity entity = noticeManager.getById(id);
        if (entity == null) {
            throw new BusinessException("公告通知不存在");
        }
        return toNoticeVO(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createNotice(NoticeAddRequest request) {
        NoticeEntity entity = BeanUtil.copyProperties(request, NoticeEntity.class);
        if (entity.getStatus() == null) {
            entity.setStatus(0); // 默认正常发布
        }
        noticeManager.save(entity);
        log.info("创建公告成功: id={}, title={}", entity.getId(), entity.getTitle());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateNotice(NoticeUpdateRequest request) {
        NoticeEntity existing = noticeManager.getById(request.getId());
        if (existing == null) {
            throw new BusinessException("公告通知不存在");
        }
        NoticeEntity entity = BeanUtil.copyProperties(request, NoticeEntity.class);
        noticeManager.updateById(entity);
        log.info("更新公告成功: id={}", request.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteNotice(Long id) {
        noticeManager.removeById(id);
        log.info("删除公告成功: id={}", id);
    }

    @Override
    public void publishNotice(Long id) {
        NoticeEntity notice = noticeManager.getById(id);
        if (notice == null) {
            throw new BusinessException("公告通知不存在");
        }
        if (notice.getStatus() != 0) {
            throw new BusinessException("仅正常状态下的公告支持发布");
        }

        // 构造前端适配的消息载荷
        String payload = cn.hutool.json.JSONUtil.toJsonStr(
            cn.hutool.core.map.MapUtil.builder()
                .put("type", "system_notice")
                .put("title", "系统通知")
                .put("data", toNoticeVO(notice))
                .build()
        );

        // 利用工具类广播给所有在线用户
        WebSocketUtils.publishMessage(WebSocketMessageDTO.broadcast(payload));
        log.info("触发公告全局广播推送: id={}, title={}", id, notice.getTitle());
    }

    @Override
    public List<NoticeVO> listUnreadNotices() {
        Long userId = StpUtil.getLoginIdAsLong();
        
        // 1. 查询所有发布的公告
        List<NoticeEntity> publishedNotices = noticeManager.list(QueryWrapper.create()
                .where(NoticeEntity::getStatus).eq(0));
        
        if (publishedNotices.isEmpty()) {
            return new ArrayList<>();
        }

        // 2. 依次校验映射状态
        List<NoticeVO> unreadList = new ArrayList<>();
        for (NoticeEntity notice : publishedNotices) {
            NoticeUserEntity mapping = noticeUserManager.getByNoticeAndUser(notice.getId(), userId);
            if (mapping == null || !Boolean.TRUE.equals(mapping.getIsRead())) {
                NoticeVO vo = toNoticeVO(notice);
                vo.setIsRead(false);
                unreadList.add(vo);
            }
        }

        return unreadList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAsRead(Long noticeId) {
        Long userId = StpUtil.getLoginIdAsLong();
        noticeUserManager.markRead(noticeId, userId);
        log.info("用户标记公告为已读: noticeId={}, userId={}", noticeId, userId);
    }

    private NoticeVO toNoticeVO(NoticeEntity entity) {
        NoticeVO vo = BeanUtil.copyProperties(entity, NoticeVO.class);
        // 判定当前用户读取状态
        try {
            Long userId = StpUtil.getLoginIdAsLong();
            NoticeUserEntity mapping = noticeUserManager.getByNoticeAndUser(entity.getId(), userId);
            vo.setIsRead(mapping != null && Boolean.TRUE.equals(mapping.getIsRead()));
        } catch (Exception e) {
            // 如果没有登录态，例如定时任务广播时忽略
            vo.setIsRead(false);
        }
        return vo;
    }
}
