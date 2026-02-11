package cn.muziseo.service.system.module.auth.controller;

import cn.muziseo.common.core.domain.dto.ResponseDTO;
import cn.muziseo.service.system.module.auth.controller.request.DeptAddRequest;
import cn.muziseo.service.system.module.auth.repository.entity.DeptEntity;
import cn.muziseo.service.system.module.auth.service.DeptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部门管理 Controller
 * <p>
 * 提供部门的增删改查、树形结构查询等功能
 *
 * @author 木子软件
 * @Date 2026-02-11
 * @Wechat liiayy
 * @Email 773582348@qq.com
 * @Copyright <a href="https://code.muziseo.cn">木子软件</a>
 */
@Tag(name = "部门管理")
@RestController
@Slf4j
@RequestMapping("/auth/dept")
public class DeptController {

    @Resource
    private DeptService deptService;

    /**
     * 获取部门列表
     *
     * @return 部门列表
     */
    @Operation(summary = "获取部门列表")
    @GetMapping("/list")
    public ResponseDTO<List<DeptEntity>> list() {
        List<DeptEntity> list = deptService.list();
        return ResponseDTO.success(list);
    }

    /**
     * 获取部门树
     *
     * @return 部门树
     */
    @Operation(summary = "获取部门树")
    @GetMapping("/tree")
    public ResponseDTO<List<DeptEntity>> tree() {
        List<DeptEntity> tree = deptService.tree();
        return ResponseDTO.success(tree);
    }

    /**
     * 根据ID获取部门
     *
     * @param id 部门ID
     * @return 部门信息
     */
    @Operation(summary = "获取部门详情")
    @GetMapping("/{id}")
    public ResponseDTO<DeptEntity> getById(@PathVariable Long id) {
        DeptEntity dept = deptService.getById(id);
        return ResponseDTO.success(dept);
    }

    /**
     * 新增部门
     *
     * @param request 部门新增请求参数
     * @return 空
     */
    @Operation(summary = "新增部门")
    @PostMapping("/add")
    public ResponseDTO<Void> add(@Valid @RequestBody DeptAddRequest request) {
        log.info("新增部门: name={}", request.getName());
        deptService.addDept(request);
        log.info("新增部门成功: name={}", request.getName());
        return ResponseDTO.success();
    }

    /**
     * 更新部门
     *
     * @param id      部门ID
     * @param request 部门更新请求参数
     * @return 空
     */
    @Operation(summary = "更新部门")
    @PutMapping("/{id}")
    public ResponseDTO<Void> update(@PathVariable Long id, @Valid @RequestBody DeptAddRequest request) {
        log.info("更新部门: id={}", id);
        deptService.updateDept(id, request);
        log.info("更新部门成功: id={}", id);
        return ResponseDTO.success();
    }

    /**
     * 删除部门
     *
     * @param id 部门ID
     * @return 空
     */
    @Operation(summary = "删除部门")
    @DeleteMapping("/{id}")
    public ResponseDTO<Void> delete(@PathVariable Long id) {
        log.info("删除部门: id={}", id);
        deptService.deleteDept(id);
        log.info("删除部门成功: id={}", id);
        return ResponseDTO.success();
    }
}
