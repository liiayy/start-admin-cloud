package cn.muziseo.service.system.module.system.service.impl;

import cn.muziseo.common.core.datatracer.DataTracerTypeEnum;
import cn.muziseo.common.log.utils.DataTracerUtils;

import cn.hutool.core.bean.BeanUtil;
import cn.muziseo.common.cache.dict.DictCacheManager;
import cn.muziseo.common.core.domain.dto.DictDataSimpleDTO;
import cn.muziseo.common.core.exception.BusinessException;
import cn.muziseo.common.db.page.PageResponse;
import cn.muziseo.service.system.enums.DictErrorCode;
import cn.muziseo.service.system.module.system.controller.request.DictDataCreateRequest;
import cn.muziseo.service.system.module.system.controller.request.DictDataPageRequest;
import cn.muziseo.service.system.module.system.controller.vo.DictDataVO;
import cn.muziseo.service.system.module.system.convert.DictConverter;
import cn.muziseo.service.system.module.system.manager.DictManager;
import cn.muziseo.service.system.module.system.manager.DictTypeManager;
import cn.muziseo.service.system.module.system.repository.entity.DictEntity;
import cn.muziseo.service.system.module.system.service.DictService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 字典数据业务实现
 *
 * @author 木子软件
 * @Date 2026-02-27
 */
@Service
@Slf4j
public class DictServiceImpl implements DictService {

    @Resource
    private DictManager dictManager;

    @Resource
    private DictTypeManager dictTypeManager;

    @Resource
    private DictConverter dictConverter;

    /**
     * 根据字典类型获取字典数据列表
     *
     * @param dictType 字典类型
     * @return 字典数据视图对象列表
     */
    @Override
    public List<DictDataVO> listByDictType(String dictType) {
        return dictManager.listByDictType(dictType).stream()
                .map(dictConverter::toVO)
                .toList();
    }

    /**
     * 根据字典类型获取精简字典数据列表
     *
     * @param dictType 字典类型
     * @return 精简字典数据列表
     */
    @Override
    public List<DictDataSimpleDTO> listSimpleByDictType(String dictType) {
        return dictManager.listByDictType(dictType).stream()
                .map(dictConverter::toSimpleDTO)
                .toList();
    }

    /**
     * 分页查询字典数据
     *
     * @param request 字典数据分页查询请求
     * @return 字典数据分页结果
     */
    @Override
    public PageResponse<DictDataVO> pageDictData(DictDataPageRequest request) {
        var page = dictManager.pageDictData(request);
        List<DictDataVO> voList = page.getRecords().stream()
                .map(dictConverter::toVO)
                .toList();
        return new PageResponse<>(voList, (int) page.getTotalRow());
    }

    /**
     * 根据 ID 获取字典数据详情
     *
     * @param id 字典数据 ID
     * @return 字典数据视图对象
     */
    @Override
    public DictDataVO getDictDataById(Long id) {
        DictEntity entity = dictManager.getById(id);
        return dictConverter.toVO(entity);
    }

    /**
     * 新增字典数据
     * <p>
     * 1. 校验字典类型是否存在
     * 2. 保存字典数据并清除对应缓存
     *
     * @param request 新增字典数据请求参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createDictData(DictDataCreateRequest request) {
        // 检查字典类型是否存在
        if (dictTypeManager.getByType(request.getDictType()) == null) {
            throw new BusinessException(DictErrorCode.DICT_TYPE_NOT_EXISTS);
        }

        DictEntity entity = dictConverter.toEntity(request);
        if (entity.getStatus() == null) {
            entity.setStatus(0);
        }
        dictManager.save(entity);
        DataTracerUtils.insert(entity.getId(), DataTracerTypeEnum.DICT);

        // 清除该字典类型的二级缓存
        DictCacheManager.evictCache(request.getDictType());
        log.info("新增字典数据成功: id={}, type={}", entity.getId(), entity.getDictType());
    }

    /**
     * 修改字典数据
     * <p>
     * 1. 校验字典数据是否存在
     * 2. 更新并清除受影响的缓存
     *
     * @param id      字典数据 ID
     * @param request 修改字典数据请求参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDictData(Long id, DictDataCreateRequest request) {
        DictEntity existing = dictManager.getById(id);
        if (existing == null) {
            throw new BusinessException(DictErrorCode.DICT_DATA_NOT_EXISTS);
        }

        DictEntity entity = dictConverter.toEntity(request);
        entity.setId(id);
        dictManager.updateById(entity);

        // 时光机记录
        DictEntity updated = dictManager.getById(id);
        DataTracerUtils.update(id, DataTracerTypeEnum.DICT, existing, updated);

        // 清除该字典类型的二级缓存（如果类型变了，两个都清）
        DictCacheManager.evictCache(existing.getDictType());
        if (!existing.getDictType().equals(request.getDictType())) {
            DictCacheManager.evictCache(request.getDictType());
        }
        log.info("更新字典数据成功: id={}", id);
    }

    /**
     * 删除字典数据
     * <p>
     * 1. 校验字典数据是否存在
     * 2. 删除并清除缓存
     *
     * @param id 字典数据 ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDictData(Long id) {
        DictEntity existing = dictManager.getById(id);
        if (existing == null) {
            throw new BusinessException(DictErrorCode.DICT_DATA_NOT_EXISTS);
        }

        dictManager.removeById(id);
        DataTracerUtils.delete(id, DataTracerTypeEnum.DICT);

        // 清除该字典类型的二级缓存
        DictCacheManager.evictCache(existing.getDictType());
        log.info("删除字典数据成功: id={}", id);
    }
}
