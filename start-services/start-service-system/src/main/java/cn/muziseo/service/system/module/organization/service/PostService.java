package cn.muziseo.service.system.module.organization.service;

import cn.muziseo.common.db.page.PageResponse;
import cn.muziseo.service.system.module.organization.controller.request.PostCreateRequest;
import cn.muziseo.service.system.module.organization.controller.request.PostPageRequest;
import cn.muziseo.service.system.module.organization.controller.vo.PostVO;

import java.util.List;

/**
 * 岗位业务接口
 *
 * @author 木子软件
 */
public interface PostService {

    List<PostVO> list();

    PageResponse<PostVO> pagePost(PostPageRequest request);

    PostVO getById(Long id);

    void createPost(PostCreateRequest request);

    void updatePost(Long id, PostCreateRequest request);

    void deletePost(Long id);

    void updateStatus(Long id, Integer status);
}
