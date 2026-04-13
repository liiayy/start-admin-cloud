package cn.muziseo.service.system.module.organization.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.muziseo.common.core.exception.BusinessException;
import cn.muziseo.service.system.enums.PostErrorCode;
import cn.muziseo.service.system.module.organization.controller.request.PostAddRequest;
import cn.muziseo.service.system.module.organization.controller.vo.PostVO;
import cn.muziseo.service.system.module.organization.manager.PostManager;
import cn.muziseo.service.system.module.organization.repository.entity.PostEntity;
import cn.muziseo.service.system.module.organization.service.PostService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

    @Override
    public List<PostVO> list() {
        return postManager.listAll().stream()
                .map(this::toPostVO)
                .collect(Collectors.toList());
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
    public void deletePost(Long id) {
        PostEntity post = postManager.getById(id);
        if (post == null) {
            throw new BusinessException(PostErrorCode.POST_NOT_EXISTS);
        }

        // TODO: 检查是否有用户关联

        postManager.removeById(id);
        log.info("删除岗位成功: id={}", id);
    }

    @Override
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
        return PostVO.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .name(entity.getName())
                .sort(entity.getSort())
                .status(entity.getStatus())
                .remark(entity.getRemark())
                .createTime(entity.getCreateTime())
                .build();
    }
}
