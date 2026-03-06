package cn.muziseo.service.system.module.system.manager;

import cn.muziseo.common.db.service.impl.BaseServiceImpl;
import cn.muziseo.service.system.module.system.repository.entity.DictEntity;
import cn.muziseo.service.system.module.system.repository.mapper.DictMapper;
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
     * 根据字典类型编码获取字典数据列表
     */
    public List<DictEntity> listByDictTypeCode(String dictTypeCode) {
        return list(QueryWrapper.create()
                .where(DictEntity::getDictTypeCode).eq(dictTypeCode)
                .orderBy(DictEntity::getSort, true)
                .orderBy(DictEntity::getId, true));
    }

    /**
     * 根据字典类型编码删除字典数据
     */
    public void deleteByDictTypeCode(String dictTypeCode) {
        remove(QueryWrapper.create()
                .where(DictEntity::getDictTypeCode).eq(dictTypeCode));
    }
}
