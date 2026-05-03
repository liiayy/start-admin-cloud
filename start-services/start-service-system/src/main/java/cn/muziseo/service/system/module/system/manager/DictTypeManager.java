package cn.muziseo.service.system.module.system.manager;

import cn.muziseo.common.db.service.impl.BaseServiceImpl;
import cn.muziseo.service.system.module.system.controller.request.DictTypePageRequest;
import cn.muziseo.service.system.module.system.repository.entity.DictTypeEntity;
import cn.muziseo.service.system.module.system.repository.mapper.DictTypeMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 字典类型管理 Manager 层
 * <p>
 * 处理字典分类（类型）的持久化逻辑，提供分类查询、编码唯一性校验及分页功能。
 *
 * @author 木子软件
 */
@Service
public class DictTypeManager extends BaseServiceImpl<DictTypeMapper, DictTypeEntity> {

    /**
     * 获取全量字典类型列表
     *
     * @return 字典类型实体列表
     */
    public List<DictTypeEntity> listAll() {
        return list(QueryWrapper.create()
                .orderBy(DictTypeEntity::getId, true));
    }

    /**
     * 分页查询字典类型列表
     *
     * @param request 分页及筛选条件（支持名称、类型模糊查询）
     * @return 分页结果对象
     */
    public Page<DictTypeEntity> pageDictType(DictTypePageRequest request) {
        QueryWrapper wrapper = QueryWrapper.create()
                .where(DictTypeEntity::getName).like(request.getName(), request.getName() != null)
                .and(DictTypeEntity::getType).like(request.getType(), request.getType() != null)
                .and(DictTypeEntity::getStatus).eq(request.getStatus(), request.getStatus() != null)
                .orderBy(DictTypeEntity::getId, false);

        return page(new Page<>(request.getPageNum(), request.getPageSize()), wrapper);
    }

    /**
     * 根据字典类型标识查询类型详情
     *
     * @param type 字典类型标识
     * @return 字典类型实体，如果不存在则返回 null
     */
    public DictTypeEntity getByType(String type) {
        return queryChain()
                .where(DictTypeEntity::getType).eq(type)
                .one();
    }

    /**
     * 校验字典类型标识是否已存在
     *
     * @param type      字典类型标识
     * @param excludeId 需要排除的字典类型 ID（用于修改时校验）
     * @return true 表示已存在，false 表示不存在
     */
    public boolean existsByType(String type, Long excludeId) {
        QueryWrapper wrapper = QueryWrapper.create()
                .where(DictTypeEntity::getType).eq(type);
        if (excludeId != null) {
            wrapper.and(DictTypeEntity::getId).ne(excludeId);
        }
        return exists(wrapper);
    }
}
