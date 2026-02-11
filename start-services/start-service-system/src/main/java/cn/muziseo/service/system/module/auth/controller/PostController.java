package cn.muziseo.service.system.module.auth.controller;

import cn.muziseo.common.core.domain.dto.ResponseDTO;
import cn.muziseo.service.system.module.auth.controller.request.PostAddRequest;
import cn.muziseo.service.system.module.auth.repository.entity.PostEntity;
import cn.muziseo.service.system.module.auth.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 岗位管理 Controller
 * <p>
 * 提供岗位的增删改查等功能
 *
 * @author 木子软件
 * @Date 2026-02-11
 * @Wechat liiayy
 * @Email 773582348@qq.com
 * @Copyright <a href="https://code.muziseo.cn">木子软件</a>
 */
@Tag(name = "岗位管理")
@RestController
@Slf4j
@RequestMapping("/auth/post")
public class PostController {

    @Resource
    private PostService postService;

    /**
     * 获取岗位列表
     *
     * @return 岗位列表
     */
    @Operation(summary = "获取岗位列表")
    @GetMapping("/list")
    public ResponseDTO<List<PostEntity>> list() {
        List<PostEntity> list = postService.list();
        return ResponseDTO.success(list);
    }

    /**
     * 根据ID获取岗位
     *
     * @param id 岗位ID
     * @return 岗位信息
     */
    @Operation(summary = "获取岗位详情")
    @GetMapping("/{id}")
    public ResponseDTO<PostEntity> getById(@PathVariable Long id) {
        PostEntity post = postService.getById(id);
        return ResponseDTO.success(post);
    }

    /**
     * 新增岗位
     *
     * @param request 岗位新增请求参数
     * @return 空
     */
    @Operation(summary = "新增岗位")
    @PostMapping("/add")
    public ResponseDTO<Void> add(@Valid @RequestBody PostAddRequest request) {
        log.info("新增岗位: code={}, name={}", request.getCode(), request.getName());
        postService.addPost(request);
        log.info("新增岗位成功: code={}", request.getCode());
        return ResponseDTO.success();
    }

    /**
     * 更新岗位
     *
     * @param id      岗位ID
     * @param request 岗位更新请求参数
     * @return 空
     */
    @Operation(summary = "更新岗位")
    @PutMapping("/{id}")
    public ResponseDTO<Void> update(@PathVariable Long id, @Valid @RequestBody PostAddRequest request) {
        log.info("更新岗位: id={}", id);
        postService.updatePost(id, request);
        log.info("更新岗位成功: id={}", id);
        return ResponseDTO.success();
    }

    /**
     * 删除岗位
     *
     * @param id 岗位ID
     * @return 空
     */
    @Operation(summary = "删除岗位")
    @DeleteMapping("/{id}")
    public ResponseDTO<Void> delete(@PathVariable Long id) {
        log.info("删除岗位: id={}", id);
        postService.deletePost(id);
        log.info("删除岗位成功: id={}", id);
        return ResponseDTO.success();
    }
}
