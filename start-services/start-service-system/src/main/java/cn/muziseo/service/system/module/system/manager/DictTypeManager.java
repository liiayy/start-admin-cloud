package cn.muziseo.service.system.module.system.manager;

import cn.muziseo.common.db.service.impl.BaseServiceImpl;
import cn.muziseo.service.system.module.system.repository.entity.DictTypeEntity;
import cn.muziseo.service.system.module.system.repository.mapper.DictTypeMapper;
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
     * 获取所有字典类型列表（按排序）
     */
    public List<DictTypeEntity> listAll() {
        return list(QueryWrapper.create()
                .orderBy(DictTypeEntity::getSort, true)
                .orderBy(DictTypeEntity::getId, true));
    }

    /**
     * 根据字典类型编码获取字典类型
     */
    public DictTypeEntity getByCode(String code) {
        return queryChain()
                .where(DictTypeEntity::getCode).eq(code)
                .one();
    }

    /**
     * 检查字典类型编码是否存在
     */
    public boolean existsByCode(String code) {
        return exists(QueryWrapper.create()
                .where(DictTypeEntity::getCode).eq(code));
    }
}
