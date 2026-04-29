package cn.muziseo.service.system.module.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.muziseo.common.core.exception.BusinessException;
import cn.muziseo.common.db.page.PageResponse;
import cn.muziseo.service.system.enums.DictErrorCode;
import cn.muziseo.service.system.module.system.controller.request.DictTypeAddRequest;
import cn.muziseo.service.system.module.system.controller.request.DictTypePageRequest;
import cn.muziseo.service.system.module.system.controller.vo.DictTypeVO;
import cn.muziseo.service.system.module.system.convert.DictTypeConverter;
import cn.muziseo.service.system.module.system.manager.DictManager;
import cn.muziseo.service.system.module.system.manager.DictTypeManager;
import cn.muziseo.service.system.module.system.repository.entity.DictTypeEntity;
import cn.muziseo.service.system.module.system.service.DictTypeService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 字典类型业务实现
 *
 * @author 木子软件
 * @Date 2026-02-27
 */
@Service
@Slf4j
public class DictTypeServiceImpl implements DictTypeService {

    @Resource
    private DictTypeManager dictTypeManager;

    @Resource
    private DictManager dictManager;

    @Resource
    private DictTypeConverter dictTypeConverter;

    /**
     * 获取所有字典类型列表
     *
     * @return 字典类型视图对象列表
     */
    @Override
    public List<DictTypeVO> list() {
        return dictTypeManager.listAll().stream()
                .map(dictTypeConverter::toVO)
                .toList();
    }

    /**
     * 分页查询字典类型
     *
     * @param request 字典类型分页查询请求
     * @return 字典类型分页结果
     */
    @Override
    public PageResponse<DictTypeVO> pageDictType(DictTypePageRequest request) {
        var page = dictTypeManager.pageDictType(request);
        List<DictTypeVO> voList = page.getRecords().stream()
                .map(dictTypeConverter::toVO)
                .toList();
        return new PageResponse<>(voList, (int) page.getTotalRow());
    }

    /**
     * 根据 ID 获取字典类型详情
     *
     * @param id 字典类型 ID
     * @return 字典类型视图对象
     */
    @Override
    public DictTypeVO getDictTypeById(Long id) {
        DictTypeEntity entity = dictTypeManager.getById(id);
        return dictTypeConverter.toVO(entity);
    }

    /**
     * 新增字典类型
     * <p>
     * 1. 检查字典类型编码是否已存在
     * 2. 保存字典类型
     *
     * @param request 新增字典类型请求参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addDictType(DictTypeAddRequest request) {
        // 检查字典类型是否已存在
        if (dictTypeManager.existsByType(request.getType(), null)) {
            throw new BusinessException(DictErrorCode.DICT_TYPE_CODE_EXISTS);
        }

        DictTypeEntity entity = dictTypeConverter.toEntity(request);
        if (entity.getStatus() == null) {
            entity.setStatus(0);
        }
        dictTypeManager.save(entity);
        log.info("新增字典类型成功: id={}, type={}", entity.getId(), entity.getType());
    }

    /**
     * 修改字典类型
     * <p>
     * 1. 校验字典类型是否存在
     * 2. 检查新编码是否冲突
     *
     * @param id      字典类型 ID
     * @param request 修改字典类型请求参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDictType(Long id, DictTypeAddRequest request) {
        DictTypeEntity existing = dictTypeManager.getById(id);
        if (existing == null) {
            throw new BusinessException(DictErrorCode.DICT_TYPE_NOT_EXISTS);
        }

        // 检查新编码是否已被占用
        if (dictTypeManager.existsByType(request.getType(), id)) {
            throw new BusinessException(DictErrorCode.DICT_TYPE_CODE_EXISTS);
        }

        DictTypeEntity entity = dictTypeConverter.toEntity(request);
        entity.setId(id);
        dictTypeManager.updateById(entity);
        log.info("更新字典类型成功: id={}", id);
    }

    /**
     * 删除字典类型
     * <p>
     * 1. 检查是否包含字典数据
     * 2. 删除字典类型
     *
     * @param id 字典类型 ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDictType(Long id) {
        DictTypeEntity dictType = dictTypeManager.getById(id);
        if (dictType == null) {
            throw new BusinessException(DictErrorCode.DICT_TYPE_NOT_EXISTS);
        }

        // 检查是否有字典数据
        if (dictManager.countByDictType(dictType.getType()) > 0) {
            throw new BusinessException(DictErrorCode.DICT_TYPE_HAS_DATA);
        }

        dictTypeManager.removeById(id);
        log.info("删除字典类型成功: id={}", id);
    }
}
