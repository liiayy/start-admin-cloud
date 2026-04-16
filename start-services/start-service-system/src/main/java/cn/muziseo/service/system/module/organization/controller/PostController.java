package cn.muziseo.service.system.module.organization.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.muziseo.common.core.domain.dto.ResponseDTO;
import cn.muziseo.common.db.page.PageResponse;
import cn.muziseo.service.system.module.organization.controller.request.PostAddRequest;
import cn.muziseo.service.system.module.organization.controller.request.PostPageRequest;
import cn.muziseo.service.system.module.organization.controller.vo.PostVO;
import cn.muziseo.service.system.module.organization.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 岗位管理 Controller
 *
 * @author 木子软件
 */
@Tag(name = "岗位管理")
@RestController
@Validated
@Slf4j
@RequestMapping("/organization/post")
public class PostController {

    @Resource
    private PostService postService;

    @Operation(summary = "获取岗位列表")
    @GetMapping("/list")
    public ResponseDTO<List<PostVO>> list() {
        return ResponseDTO.success(postService.list());
    }

    @Operation(summary = "分页查询岗位")
    @GetMapping("/page")
    public ResponseDTO<PageResponse<PostVO>> page(PostPageRequest request) {
        return ResponseDTO.success(postService.pagePost(request));
    }

    @Operation(summary = "获取岗位详情")
    @GetMapping("/{id}")
    public ResponseDTO<PostVO> getById(@PathVariable Long id) {
        return ResponseDTO.success(postService.getById(id));
    }

    @Operation(summary = "新增岗位")
    @PostMapping("/add")
    @SaCheckPermission("system:post:add")
    public ResponseDTO<Void> add(@Valid @RequestBody PostAddRequest request) {
        postService.addPost(request);
        return ResponseDTO.success();
    }

    @Operation(summary = "更新岗位")
    @PutMapping("/{id}")
    @SaCheckPermission("system:post:update")
    public ResponseDTO<Void> update(@PathVariable Long id, @Valid @RequestBody PostAddRequest request) {
        postService.updatePost(id, request);
        return ResponseDTO.success();
    }

    @Operation(summary = "删除岗位")
    @DeleteMapping("/{id}")
    @SaCheckPermission("system:post:delete")
    public ResponseDTO<Void> delete(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseDTO.success();
    }

    @Operation(summary = "更新岗位状态")
    @PutMapping("/update-status")
    @SaCheckPermission("system:post:update")
    public ResponseDTO<Void> updateStatus(@RequestParam Long id, @RequestParam Integer status) {
        postService.updateStatus(id, status);
        return ResponseDTO.success();
    }
}
