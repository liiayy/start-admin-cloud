package cn.muziseo.service.system.module.organization.service;

import cn.muziseo.service.system.module.organization.controller.request.PostAddRequest;
import cn.muziseo.service.system.module.organization.controller.vo.PostVO;

import java.util.List;

/**
 * 岗位业务接口
 *
 * @author 木子软件
 */
public interface PostService {

    List<PostVO> list();

    PostVO getById(Long id);

    void addPost(PostAddRequest request);

    void updatePost(Long id, PostAddRequest request);

    void deletePost(Long id);

    void updateStatus(Long id, Integer status);
}
