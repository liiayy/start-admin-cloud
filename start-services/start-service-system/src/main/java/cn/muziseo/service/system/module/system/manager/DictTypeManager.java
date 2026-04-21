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
 * 字典类型 Manager 层
 *
 * @author 木子软件
 * @Date 2026-02-27
 */
@Service
public class DictTypeManager extends BaseServiceImpl<DictTypeMapper, DictTypeEntity> {

    /**
     * 获取所有字典类型列表
     */
    public List<DictTypeEntity> listAll() {
        return list(QueryWrapper.create()
                .orderBy(DictTypeEntity::getId, true));
    }

    /**
     * 分页查询字典类型
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
     * 根据字典类型获取字典类型实体
     */
    public DictTypeEntity getByType(String type) {
        return queryChain()
                .where(DictTypeEntity::getType).eq(type)
                .one();
    }

    /**
     * 检查字典类型是否存在（支持排除指定ID）
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
