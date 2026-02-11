package cn.muziseo.service.system.module.auth.manager;

import cn.muziseo.common.db.service.impl.BaseServiceImpl;
import cn.muziseo.service.system.module.auth.repository.entity.PostEntity;
import cn.muziseo.service.system.module.auth.repository.mapper.PostMapper;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 岗位表 Manager 层
 * <p>
 * 提供岗位表的数据查询和基础数据库操作
 *
 * @author 木子软件
 * @Date 2026-02-11
 * @Wechat liiayy
 * @Email 773582348@qq.com
 * @Copyright <a href="https://code.muziseo.cn">木子软件</a>
 */
@Service
public class PostManager extends BaseServiceImpl<PostMapper, PostEntity> {

    /**
     * 获取所有岗位列表（按排序）
     *
     * @return 岗位列表
     */
    public List<PostEntity> listAll() {
        return list(QueryWrapper.create()
                .orderBy(PostEntity::getSort, true)
                .orderBy(PostEntity::getId, true));
    }

    /**
     * 根据岗位编码获取岗位
     *
     * @param code 岗位编码
     * @return 岗位实体
     */
    public PostEntity getByCode(String code) {
        return queryChain()
                .where(PostEntity::getCode).eq(code)
                .one();
    }

    /**
     * 检查岗位编码是否存在
     *
     * @param code 岗位编码
     * @return 是否存在
     */
    public boolean existsByCode(String code) {
        return exists(QueryWrapper.create()
                .where(PostEntity::getCode).eq(code));
    }
}
