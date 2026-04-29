package cn.muziseo.service.system.module.organization.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.muziseo.common.core.exception.BusinessException;
import cn.muziseo.common.db.annotation.DataScope;
import cn.muziseo.common.db.page.PageResponse;
import cn.muziseo.service.system.enums.PostErrorCode;
import cn.muziseo.service.system.module.organization.controller.request.PostAddRequest;
import cn.muziseo.service.system.module.organization.controller.request.PostPageRequest;
import cn.muziseo.service.system.module.organization.controller.vo.PostVO;
import cn.muziseo.service.system.module.auth.manager.UserManager;
import cn.muziseo.service.system.module.organization.manager.DeptManager;
import cn.muziseo.service.system.module.organization.manager.PostManager;
import cn.muziseo.service.system.module.organization.repository.entity.DeptEntity;
import cn.muziseo.service.system.module.organization.repository.entity.PostEntity;
import cn.muziseo.service.system.module.organization.service.PostService;
import cn.muziseo.service.system.module.organization.convert.PostConverter;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 岗位业务实现
 *
 * @author 木子软件
 */
@Service
@Slf4j
public class PostServiceImpl implements PostService {

    @Resource
    private PostManager postManager;

    @Resource
    private DeptManager deptManager;

    @Resource
    private UserManager userManager;

    @Resource
    private PostConverter postConverter;

    /**
     * 获取所有岗位列表
     *
     * @return 岗位视图对象列表
     */
    @Override
    public List<PostVO> list() {
        return postManager.listAll().stream()
                .map(post -> {
                    PostVO vo = postConverter.toVO(post);
                    if (post.getDeptId() != null) {
                        DeptEntity dept = deptManager.getById(post.getDeptId());
                        if (dept != null) {
                            vo.setDeptName(dept.getName());
                        }
                    }
                    return vo;
                })
                .collect(Collectors.toList());
    }

    /**
     * 分页查询岗位列表
     *
     * @param request 岗位分页查询请求
     * @return 岗位分页结果
     */
    @Override
    @DataScope
    public PageResponse<PostVO> pagePost(PostPageRequest request) {
        // 解析部门过滤（部门+子部门）
        List<Long> deptIds = null;
        if (request.getDeptId() != null) {
            deptIds = deptManager.getDeptAndChildIds(request.getDeptId());
        }

        var page = postManager.pagePost(request, deptIds);
        List<PostVO> voList = page.getRecords().stream()
                .map(post -> {
                    PostVO vo = postConverter.toVO(post);
                    if (post.getDeptId() != null) {
                        DeptEntity dept = deptManager.getById(post.getDeptId());
                        if (dept != null) {
                            vo.setDeptName(dept.getName());
                        }
                    }
                    return vo;
                })
                .collect(Collectors.toList());

        PageResponse<PostVO> response = new PageResponse<>();
        response.setList(voList);
        response.setTotal(page.getTotalRow());
        return response;
    }

    /**
     * 根据 ID 获取岗位详情
     *
     * @param id 岗位 ID
     * @return 岗位视图对象
     */
    @Override
    public PostVO getById(Long id) {
        PostEntity post = postManager.getById(id);
        if (post == null) {
            throw new BusinessException(PostErrorCode.POST_NOT_EXISTS);
        }
        PostVO vo = postConverter.toVO(post);
        if (post.getDeptId() != null) {
            DeptEntity dept = deptManager.getById(post.getDeptId());
            if (dept != null) {
                vo.setDeptName(dept.getName());
            }
        }
        return vo;
    }

    /**
     * 新增岗位
     *
     * @param request 新增岗位请求参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addPost(PostAddRequest request) {
        // 校验编码唯一
        if (postManager.existsByCode(request.getCode(), null)) {
            throw new BusinessException(PostErrorCode.POST_CODE_EXISTS);
        }

        PostEntity entity = postConverter.toEntity(request);
        if (entity.getStatus() == null) {
            entity.setStatus(0);
        }
        postManager.save(entity);
        log.info("新增岗位成功: id={}, code={}", entity.getId(), entity.getCode());
    }

    /**
     * 修改岗位
     *
     * @param id      岗位 ID
     * @param request 修改岗位请求参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePost(Long id, PostAddRequest request) {
        PostEntity post = postManager.getById(id);
        if (post == null) {
            throw new BusinessException(PostErrorCode.POST_NOT_EXISTS);
        }

        // 校验编码唯一（排除自身）
        if (postManager.existsByCode(request.getCode(), id)) {
            throw new BusinessException(PostErrorCode.POST_CODE_EXISTS);
        }

        PostEntity entity = postConverter.toEntity(request);
        entity.setId(id);
        postManager.updateById(entity);
        log.info("更新岗位成功: id={}", id);
    }

    /**
     * 删除岗位
     * <p>
     * 1. 检查是否有关联用户
     * 2. 删除岗位记录
     *
     * @param id 岗位 ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePost(Long id) {
        PostEntity post = postManager.getById(id);
        if (post == null) {
            throw new BusinessException(PostErrorCode.POST_NOT_EXISTS);
        }

        // 检查是否有用户关联
        if (userManager.countByPostId(id) > 0) {
            throw new BusinessException(PostErrorCode.POST_HAS_USERS);
        }

        postManager.removeById(id);
        log.info("删除岗位成功: id={}", id);
    }

    /**
     * 更新岗位状态
     *
     * @param id     岗位 ID
     * @param status 状态值
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Integer status) {
        PostEntity post = postManager.getById(id);
        if (post == null) {
            throw new BusinessException(PostErrorCode.POST_NOT_EXISTS);
        }
        PostEntity entity = new PostEntity();
        entity.setId(id);
        entity.setStatus(status);
        postManager.updateById(entity);
        log.info("更新岗位状态: id={}, status={}", id, status);
    }
}
