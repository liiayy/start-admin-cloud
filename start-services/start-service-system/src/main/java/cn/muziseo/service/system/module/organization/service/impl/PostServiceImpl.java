package cn.muziseo.service.system.module.organization.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.muziseo.service.system.module.organization.controller.request.PostAddRequest;
import cn.muziseo.service.system.module.organization.manager.PostManager;
import cn.muziseo.service.system.module.organization.repository.entity.PostEntity;
import cn.muziseo.service.system.module.organization.service.PostService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 岗位业务实现
 * <p>
 * 实现岗位的增删改查等功能
 *
 * @author 木子软件
 * @Date 2026-02-11
 * @Wechat liiayy
 * @Email 773582348@qq.com
 * @Copyright <a href="https://code.muziseo.cn">木子软件</a>
 */
@Service
@Slf4j
public class PostServiceImpl implements PostService {

    @Resource
    private PostManager postManager;

    @Override
    public List<PostEntity> list() {
        // 调用Manager层查询
        return postManager.listAll();
    }

    @Override
    public PostEntity getById(Long id) {
        return postManager.getById(id);
    }

    @Override
    public void addPost(PostAddRequest request) {
        log.info("新增岗位: code={}, name={}", request.getCode(), request.getName());
        PostEntity postEntity = BeanUtil.copyProperties(request, PostEntity.class);
        if (postEntity.getStatus() == null) {
            postEntity.setStatus(0);
        }
        postManager.save(postEntity);
        log.info("新增岗位成功: id={}, code={}", postEntity.getId(), postEntity.getCode());
    }

    @Override
    public void updatePost(Long id, PostAddRequest request) {
        log.info("更新岗位: id={}", id);
        PostEntity postEntity = BeanUtil.copyProperties(request, PostEntity.class);
        postEntity.setId(id);
        postManager.updateById(postEntity);
        log.info("更新岗位成功: id={}", id);
    }

    @Override
    public void deletePost(Long id) {
        log.info("删除岗位: id={}", id);

        // 检查是否有用户关联
        // TODO: 添加用户检查逻辑

        postManager.removeById(id);
        log.info("删除岗位成功: id={}", id);
    }
}
