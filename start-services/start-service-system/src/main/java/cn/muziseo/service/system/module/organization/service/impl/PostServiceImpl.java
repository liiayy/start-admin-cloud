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

    @Override
    public List<PostVO> list() {
        return postManager.listAll().stream()
                .map(this::toPostVO)
                .collect(Collectors.toList());
    }

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
                .map(this::toPostVO)
                .collect(Collectors.toList());

        PageResponse<PostVO> response = new PageResponse<>();
        response.setList(voList);
        response.setTotal(page.getTotalRow());
        return response;
    }

    @Override
    public PostVO getById(Long id) {
        PostEntity post = postManager.getById(id);
        if (post == null) {
            throw new BusinessException(PostErrorCode.POST_NOT_EXISTS);
        }
        return toPostVO(post);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addPost(PostAddRequest request) {
        // 校验编码唯一
        if (postManager.existsByCode(request.getCode(), null)) {
            throw new BusinessException(PostErrorCode.POST_CODE_EXISTS);
        }

        PostEntity entity = BeanUtil.copyProperties(request, PostEntity.class);
        if (entity.getStatus() == null) {
            entity.setStatus(0);
        }
        postManager.save(entity);
        log.info("新增岗位成功: id={}, code={}", entity.getId(), entity.getCode());
    }

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

        PostEntity entity = BeanUtil.copyProperties(request, PostEntity.class);
        entity.setId(id);
        postManager.updateById(entity);
        log.info("更新岗位成功: id={}", id);
    }

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

    /**
     * Entity → PostVO
     */
    private PostVO toPostVO(PostEntity entity) {
        String deptName = null;
        if (entity.getDeptId() != null) {
            DeptEntity dept = deptManager.getById(entity.getDeptId());
            if (dept != null) {
                deptName = dept.getName();
            }
        }
        return PostVO.builder()
                .id(entity.getId())
                .deptId(entity.getDeptId())
                .deptName(deptName)
                .code(entity.getCode())
                .name(entity.getName())
                .sort(entity.getSort())
                .status(entity.getStatus())
                .remark(entity.getRemark())
                .createTime(entity.getCreateTime())
                .build();
    }
}
