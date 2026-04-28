package cn.muziseo.service.system.module.notice.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.muziseo.common.core.domain.dto.ResponseDTO;
import cn.muziseo.common.db.page.PageResponse;
import cn.muziseo.service.system.module.notice.controller.request.NoticeAddRequest;
import cn.muziseo.service.system.module.notice.controller.request.NoticePageRequest;
import cn.muziseo.service.system.module.notice.controller.request.NoticeUpdateRequest;
import cn.muziseo.service.system.module.notice.controller.vo.NoticeVO;
import cn.muziseo.service.system.module.notice.service.NoticeService;
import cn.muziseo.service.system.module.permission.repository.entity.MenuEntity;
import cn.muziseo.service.system.module.permission.repository.mapper.MenuMapper;
import com.mybatisflex.core.query.QueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 通知公告管理控制器
 * 
 * @author 木子软件
 */
@Tag(name = "通知公告管理")
@RestController
@RequestMapping("/admin/system/notice")
public class SysNoticeController {

    @Resource
    private NoticeService noticeService;



    @Operation(summary = "分页获取公告列表")
    @PostMapping("/page")
    @SaCheckPermission("system:notice:query")
    public ResponseDTO<PageResponse<NoticeVO>> pageNotice(@RequestBody NoticePageRequest request) {
        return ResponseDTO.success(noticeService.pageNotice(request));
    }

    @Operation(summary = "获取公告详情")
    @GetMapping("/{id}")
    @SaCheckPermission("system:notice:query")
    public ResponseDTO<NoticeVO> getNotice(@PathVariable Long id) {
        return ResponseDTO.success(noticeService.getNotice(id));
    }

    @Operation(summary = "新增公告")
    @PostMapping
    @SaCheckPermission("system:notice:add")
    public ResponseDTO<Void> createNotice(@RequestBody NoticeAddRequest request) {
        noticeService.createNotice(request);
        return ResponseDTO.success();
    }

    @Operation(summary = "更新公告")
    @PutMapping
    @SaCheckPermission("system:notice:update")
    public ResponseDTO<Void> updateNotice(@RequestBody NoticeUpdateRequest request) {
        noticeService.updateNotice(request);
        return ResponseDTO.success();
    }

    @Operation(summary = "删除公告")
    @DeleteMapping("/{id}")
    @SaCheckPermission("system:notice:delete")
    public ResponseDTO<Void> deleteNotice(@PathVariable Long id) {
        noticeService.deleteNotice(id);
        return ResponseDTO.success();
    }

    @Operation(summary = "推送发布公告")
    @PostMapping("/publish/{id}")
    @SaCheckPermission("system:notice:publish")
    public ResponseDTO<Void> publishNotice(@PathVariable Long id) {
        noticeService.publishNotice(id);
        return ResponseDTO.success();
    }

    @Operation(summary = "获取个人未读公告")
    @GetMapping("/unread")
    public ResponseDTO<List<NoticeVO>> listUnread() {
        return ResponseDTO.success(noticeService.listUnreadNotices());
    }

    @Operation(summary = "标记公告为已读")
    @PostMapping("/read/{id}")
    public ResponseDTO<Void> markRead(@PathVariable Long id) {
        noticeService.markAsRead(id);
        return ResponseDTO.success();
    }
}
