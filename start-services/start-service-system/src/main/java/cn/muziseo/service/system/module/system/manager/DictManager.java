package cn.muziseo.service.system.module.system.manager;

import cn.muziseo.common.db.service.impl.BaseServiceImpl;
import cn.muziseo.service.system.module.system.controller.request.DictDataPageRequest;
import cn.muziseo.service.system.module.system.repository.entity.DictEntity;
import cn.muziseo.service.system.module.system.repository.mapper.DictMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 字典数据 Manager 层
 *
 * @author 木子软件
 * @Date 2026-02-27
 */
@Service
public class DictManager extends BaseServiceImpl<DictMapper, DictEntity> {

    /**
     * 根据字典类型获取字典数据列表
     */
    public List<DictEntity> listByDictType(String dictType) {
        return list(QueryWrapper.create()
                .where(DictEntity::getDictType).eq(dictType)
                .orderBy(DictEntity::getSort, true)
                .orderBy(DictEntity::getId, true));
    }

    /**
     * 分页查询字典数据
     */
    public Page<DictEntity> pageDictData(DictDataPageRequest request) {
        QueryWrapper wrapper = QueryWrapper.create()
                .where(DictEntity::getDictType).eq(request.getDictType(), request.getDictType() != null)
                .and(DictEntity::getLabel).like(request.getLabel(), request.getLabel() != null)
                .and(DictEntity::getStatus).eq(request.getStatus(), request.getStatus() != null)
                .orderBy(DictEntity::getId, false);

        return page(new Page<>(request.getPageNum(), request.getPageSize()), wrapper);
    }

    /**
     * 根据字典类型删除字典数据
     */
    public void deleteByDictType(String dictType) {
        remove(QueryWrapper.create()
                .where(DictEntity::getDictType).eq(dictType));
    }

    /**
     * 统计指定类型的字典数据数量
     */
    public long countByDictType(String dictType) {
        return count(QueryWrapper.create()
                .where(DictEntity::getDictType).eq(dictType));
    }
}
