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
import cn.muziseo.service.system.enums.NoticeErrorCode;
import cn.muziseo.service.system.module.notice.repository.entity.NoticeEntity;
import cn.muziseo.service.system.module.notice.repository.entity.NoticeUserEntity;
import cn.muziseo.service.system.module.notice.service.NoticeService;
import com.mybatisflex.core.paginate.Page;
import cn.muziseo.service.system.module.auth.manager.UserManager;
import cn.muziseo.service.system.module.auth.manager.UserRoleManager;
import cn.muziseo.service.system.module.notice.service.WebSocketSendService;
import cn.muziseo.service.system.module.notice.convert.NoticeConverter;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.PostConstruct;
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

    @Resource
    private UserManager userManager;

    @Resource
    private UserRoleManager userRoleManager;

    @Resource
    private WebSocketSendService webSocketSendService;

    @Resource
    private NoticeConverter noticeConverter;


    /**
     * 分页查询通知公告
     *
     * @param request 分页查询请求参数
     * @return 分页结果
     */
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

    /**
     * 获取通知公告详情
     *
     * @param id 公告 ID
     * @return 公告视图对象
     */
    @Override
    public NoticeVO getNotice(Long id) {
        NoticeEntity entity = noticeManager.getById(id);
        if (entity == null) {
            throw new BusinessException(NoticeErrorCode.NOTICE_NOT_EXISTS);
        }
        return toNoticeVO(entity);
    }

    /**
     * 创建通知公告
     *
     * @param request 新增公告请求参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createNotice(NoticeAddRequest request) {
        NoticeEntity entity = noticeConverter.toEntity(request);
        if (entity.getStatus() == null) {
            entity.setStatus(0); // 默认正常发布
        }
        noticeManager.save(entity);
        log.info("创建公告成功: id={}, title={}", entity.getId(), entity.getTitle());
    }

    /**
     * 修改通知公告
     *
     * @param request 修改公告请求参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateNotice(NoticeUpdateRequest request) {
        NoticeEntity existing = noticeManager.getById(request.getId());
        if (existing == null) {
            throw new BusinessException(NoticeErrorCode.NOTICE_NOT_EXISTS);
        }
        NoticeEntity entity = noticeConverter.toEntity(request);
        noticeManager.updateById(entity);
        log.info("更新公告成功: id={}", request.getId());
    }

    /**
     * 删除通知公告
     *
     * @param id 公告 ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteNotice(Long id) {
        noticeManager.removeById(id);
        log.info("删除公告成功: id={}", id);
    }

    /**
     * 发布通知公告
     * <p>
     * 支持全量广播或基于部门、角色、岗位的灰度定向推送（WebSocket）
     *
     * @param id 公告 ID
     */
    @Override
    public void publishNotice(Long id) {
        NoticeEntity notice = noticeManager.getById(id);
        if (notice == null) {
            throw new BusinessException(NoticeErrorCode.NOTICE_NOT_EXISTS);
        }
        if (notice.getStatus() != 0) {
            throw new BusinessException(NoticeErrorCode.NOTICE_STATUS_ERROR);
        }

        // 构造前端适配的消息载荷
        String payload = cn.hutool.json.JSONUtil.toJsonStr(
            cn.hutool.core.map.MapUtil.builder()
                .put("type", "system_notice")
                .put("title", "系统通知")
                .put("data", toNoticeVO(notice))
                .build()
        );

        // 灰度发布范围鉴别
        if (notice.getTargetType() != null && notice.getTargetType() == 1) {
            List<Long> deptIds = new ArrayList<>();
            if (cn.hutool.core.util.StrUtil.isNotBlank(notice.getTargetDepts())) {
                for (String idStr : notice.getTargetDepts().split(",")) {
                    try { deptIds.add(Long.parseLong(idStr)); } catch (Exception ignored) {}
                }
            }

            List<Long> roleIds = new ArrayList<>();
            if (cn.hutool.core.util.StrUtil.isNotBlank(notice.getTargetRoles())) {
                for (String idStr : notice.getTargetRoles().split(",")) {
                    try { roleIds.add(Long.parseLong(idStr)); } catch (Exception ignored) {}
                }
            }

            List<Long> postIds = new ArrayList<>();
            if (cn.hutool.core.util.StrUtil.isNotBlank(notice.getTargetPosts())) {
                for (String idStr : notice.getTargetPosts().split(",")) {
                    try { postIds.add(Long.parseLong(idStr)); } catch (Exception ignored) {}
                }
            }

            webSocketSendService.sendToScope(deptIds, roleIds, postIds, payload);
            log.info("触发公告指定范围路由推送: id={}, targetDepts={}, targetRoles={}, targetPosts={}", 
                id, notice.getTargetDepts(), notice.getTargetRoles(), notice.getTargetPosts());
        } else {
            // 默认全局广播给所有在线用户
            WebSocketUtils.publishMessage(WebSocketMessageDTO.broadcast(payload));
            log.info("触发公告全局广播推送: id={}, title={}", id, notice.getTitle());
        }
    }

    /**
     * 获取当前用户未读的通知公告列表
     * <p>
     * 过滤符合当前用户灰度范围的已发布公告
     *
     * @return 未读公告视图对象列表
     */
    @Override
    public List<NoticeVO> listUnreadNotices() {
        Long userId = StpUtil.getLoginIdAsLong();
        
        // 1. 查询所有发布的公告
        List<NoticeEntity> publishedNotices = noticeManager.list(QueryWrapper.create()
                .where(NoticeEntity::getStatus).eq(0));
        
        if (publishedNotices.isEmpty()) {
            return new ArrayList<>();
        }

        // 提取当前用户的身份标签
        Long userDeptId = null;
        List<Long> userPostIds = new ArrayList<>();
        try {
            cn.muziseo.service.system.module.auth.repository.entity.UserEntity user = userManager.getById(userId);
            if (user != null) {
                userDeptId = user.getDeptId();
                if (user.getPostIds() != null) {
                    userPostIds.addAll(user.getPostIds());
                }
            }
        } catch (Exception ignored) {}

        List<Long> userRoleIds = new ArrayList<>();
        try {
            List<cn.muziseo.service.system.module.auth.repository.entity.UserRoleEntity> relations = userRoleManager.list(
                com.mybatisflex.core.query.QueryWrapper.create()
                    .where(cn.muziseo.service.system.module.auth.repository.entity.UserRoleEntity::getUserId).eq(userId)
            );
            if (relations != null) {
                relations.forEach(r -> userRoleIds.add(r.getRoleId()));
            }
        } catch (Exception ignored) {}

        // 2. 依次校验映射状态与灰度覆盖范围
        List<NoticeVO> unreadList = new ArrayList<>();
        for (NoticeEntity notice : publishedNotices) {
            if (notice.getTargetType() != null && notice.getTargetType() == 1) {
                boolean matched = false;

                // 部门匹配
                if (userDeptId != null && cn.hutool.core.util.StrUtil.isNotBlank(notice.getTargetDepts())) {
                    String[] targetDepts = notice.getTargetDepts().split(",");
                    if (java.util.Arrays.asList(targetDepts).contains(userDeptId.toString())) {
                        matched = true;
                    }
                }

                // 角色匹配
                if (!matched && !userRoleIds.isEmpty() && cn.hutool.core.util.StrUtil.isNotBlank(notice.getTargetRoles())) {
                    String[] targetRoles = notice.getTargetRoles().split(",");
                    for (String roleIdStr : targetRoles) {
                        if (userRoleIds.contains(Long.parseLong(roleIdStr))) {
                            matched = true;
                            break;
                        }
                    }
                }

                // 岗位匹配
                if (!matched && !userPostIds.isEmpty() && cn.hutool.core.util.StrUtil.isNotBlank(notice.getTargetPosts())) {
                    String[] targetPosts = notice.getTargetPosts().split(",");
                    for (String postIdStr : targetPosts) {
                        if (userPostIds.contains(Long.parseLong(postIdStr))) {
                            matched = true;
                            break;
                        }
                    }
                }

                // 如果不符合当前用户身份灰度范围，直接跳过
                if (!matched) {
                    continue;
                }
            }

            NoticeUserEntity mapping = noticeUserManager.getByNoticeAndUser(notice.getId(), userId);
            if (mapping == null || !Boolean.TRUE.equals(mapping.getIsRead())) {
                NoticeVO vo = toNoticeVO(notice);
                vo.setIsRead(false);
                unreadList.add(vo);
            }
        }

        return unreadList;
    }

    /**
     * 标记通知公告为已读
     *
     * @param noticeId 公告 ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAsRead(Long noticeId) {
        Long userId = StpUtil.getLoginIdAsLong();
        noticeUserManager.markRead(noticeId, userId);
        log.info("用户标记公告为已读: noticeId={}, userId={}", noticeId, userId);
    }

    private NoticeVO toNoticeVO(NoticeEntity entity) {
        NoticeVO vo = noticeConverter.toVO(entity);
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
