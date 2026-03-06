package cn.muziseo.service.system.module.organization.service;

import cn.muziseo.service.system.module.organization.controller.request.PostAddRequest;
import cn.muziseo.service.system.module.organization.repository.entity.PostEntity;

import java.util.List;

/**
 * 岗位业务接口
 * <p>
 * 提供岗位的增删改查等功能
 *
 * @author 木子软件
 * @Date 2026-02-11
 * @Wechat liiayy
 * @Email 773582348@qq.com
 * @Copyright <a href="https://code.muziseo.cn">木子软件</a>
 */
public interface PostService {

    /**
     * 获取所有岗位列表
     *
     * @return 岗位列表
     */
    List<PostEntity> list();

    /**
     * 根据ID获取岗位
     *
     * @param id 岗位ID
     * @return 岗位实体
     */
    PostEntity getById(Long id);

    /**
     * 添加岗位
     *
     * @param request 添加请求
     */
    void addPost(PostAddRequest request);

    /**
     * 更新岗位
     *
     * @param id      岗位ID
     * @param request 更新请求
     */
    void updatePost(Long id, PostAddRequest request);

    /**
     * 删除岗位
     *
     * @param id 岗位ID
     */
    void deletePost(Long id);
}
