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
 * 字典数据管理 Manager 层
 * <p>
 * 处理具体字典项（键值对）的持久化逻辑，提供按类型查询、分页过滤及数据统计功能。
 *
 * @author 木子软件
 */
@Service
public class DictManager extends BaseServiceImpl<DictMapper, DictEntity> {

    /**
     * 根据字典类型获取其下所有的有效字典项列表
     * <p>
     * 结果按显示顺序（sort 字段）及 ID 升序排列。
     *
     * @param dictType 字典类型标识（如：sys_user_sex）
     * @return 字典数据实体列表
     */
    public List<DictEntity> listByDictType(String dictType) {
        return list(QueryWrapper.create()
                .where(DictEntity::getDictType).eq(dictType)
                .orderBy(DictEntity::getSort, true)
                .orderBy(DictEntity::getId, true));
    }

    /**
     * 分页查询字典数据列表
     *
     * @param request 分页及筛选条件（支持按标签模糊查询及状态过滤）
     * @return 分页结果对象
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
     * 根据字典类型批量删除其关联的所有字典项
     *
     * @param dictType 字典类型标识
     */
    public void deleteByDictType(String dictType) {
        remove(QueryWrapper.create()
                .where(DictEntity::getDictType).eq(dictType));
    }

    /**
     * 统计指定字典类型下的项总数
     *
     * @param dictType 字典类型标识
     * @return 字典项数量
     */
    public long countByDictType(String dictType) {
        return count(QueryWrapper.create()
                .where(DictEntity::getDictType).eq(dictType));
    }
}
