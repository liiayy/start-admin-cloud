package cn.muziseo.service.system.module.monitor.datatracer.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import cn.muziseo.common.core.constant.SaSessionConstants;
import cn.muziseo.common.core.datatracer.DataTracerForm;
import cn.muziseo.common.core.datatracer.DataTracerTypeEnum;
import cn.muziseo.common.core.utils.servlet.ServletUtils;
import cn.muziseo.common.db.page.PageResponse;
import cn.muziseo.common.log.utils.IpLocationUtils;
import cn.muziseo.service.system.module.monitor.datatracer.manager.DataTracerManager;
import cn.muziseo.service.system.module.monitor.datatracer.repository.entity.DataTracerEntity;
import cn.muziseo.service.system.module.monitor.datatracer.service.DataTracerContentService;
import cn.muziseo.service.system.module.monitor.datatracer.service.DataTracerService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 数据变更记录服务实现
 */
@Slf4j
@Service
public class DataTracerServiceImpl implements DataTracerService {

    @Resource
    private DataTracerManager dataTracerManager;

    @Resource
    private DataTracerContentService dataTracerContentService;

    @Resource
    private IpLocationUtils ipLocationUtils;

    @Override
    public void insert(Long dataId, DataTracerTypeEnum type) {
        addTrace(DataTracerForm.builder()
                .dataId(dataId)
                .type(type)
                .content("新增" + type.getDesc())
                .build());
    }

    @Override
    public void update(Long dataId, DataTracerTypeEnum type, Object oldBean, Object newBean) {
        String diffOld = dataTracerContentService.getChangeContent(oldBean);
        String diffNew = dataTracerContentService.getChangeContent(newBean);
        
        // 如果没有差异，不记录
        if (StrUtil.equals(diffOld, diffNew)) {
            return;
        }

        addTrace(DataTracerForm.builder()
                .dataId(dataId)
                .type(type)
                .content("修改" + type.getDesc())
                .diffOld(diffOld)
                .diffNew(diffNew)
                .build());
    }

    @Override
    public void delete(Long dataId, DataTracerTypeEnum type) {
        addTrace(DataTracerForm.builder()
                .dataId(dataId)
                .type(type)
                .content("删除" + type.getDesc())
                .build());
    }

    @Override
    public void batchDelete(Long[] dataIds, DataTracerTypeEnum type) {
        if (dataIds == null || dataIds.length == 0) {
            return;
        }
        for (Long dataId : dataIds) {
            delete(dataId, type);
        }
    }

    @Override
    public void addTrace(DataTracerForm form) {
        try {
            DataTracerEntity entity = new DataTracerEntity();
            entity.setDataId(form.getDataId());
            entity.setType(form.getType().getValue());
            entity.setContent(form.getContent());
            entity.setDiffOld(form.getDiffOld());
            entity.setDiffNew(form.getDiffNew());
            entity.setCreateTime(LocalDateTime.now());

            HttpServletRequest request = ServletUtils.getRequest();
            if (request != null) {
                entity.setOperIp(ServletUtils.getClientIP());
                entity.setOperLocation(ipLocationUtils.getLocation(entity.getOperIp()));
                String userAgent = request.getHeader("User-Agent");
                entity.setUserAgent(StrUtil.sub(userAgent, 0, 500));
            }

            try {
                if (StpUtil.isLogin()) {
                    String sessionUsername = StpUtil.getSession().getString(SaSessionConstants.USERNAME);
                    entity.setOperName(sessionUsername != null ? sessionUsername : StpUtil.getLoginIdAsString());
                }
            } catch (Exception ignored) {
                entity.setOperName("system");
            }

            dataTracerManager.save(entity);
        } catch (Exception e) {
            log.error("保存数据变更记录失败", e);
        }
    }

    @Override
    public void addTrace(cn.muziseo.common.core.event.DataTracerEvent event) {
        try {
            DataTracerEntity entity = new DataTracerEntity();
            entity.setDataId(event.getDataId());
            entity.setType(event.getType());
            entity.setContent(event.getContent());
            entity.setDiffOld(event.getDiffOld());
            entity.setDiffNew(event.getDiffNew());
            entity.setOperName(event.getOperName());
            entity.setOperIp(event.getOperIp());
            entity.setOperLocation(event.getOperLocation());
            entity.setUserAgent(event.getUserAgent());
            entity.setCreateTime(event.getCreateTime() != null ? event.getCreateTime() : LocalDateTime.now());
            dataTracerManager.save(entity);
        } catch (Exception e) {
            log.error("保存远程数据变更记录失败", e);
        }
    }

    @Override
    public PageResponse<DataTracerEntity> page(Long dataId, Integer type, int pageNum, int pageSize) {
        var page = dataTracerManager.pageByDataIdAndType(dataId, type, pageNum, pageSize);
        PageResponse<DataTracerEntity> response = new PageResponse<>();
        response.setList(page.getRecords());
        response.setTotal(page.getTotalRow());
        return response;
    }

    @Resource
    private cn.muziseo.service.system.module.monitor.datatracer.convert.DataTracerConverter dataTracerConverter;

    @Override
    public PageResponse<cn.muziseo.service.system.module.monitor.datatracer.controller.vo.DataTracerVO> pageTracer(cn.muziseo.service.system.module.monitor.datatracer.controller.request.DataTracerPageRequest request) {
        var page = dataTracerManager.pageTracer(request);
        PageResponse<cn.muziseo.service.system.module.monitor.datatracer.controller.vo.DataTracerVO> response = new PageResponse<>();
        response.setList(page.getRecords().stream().map(dataTracerConverter::toVO).collect(java.util.stream.Collectors.toList()));
        response.setTotal(page.getTotalRow());
        return response;
    }

    @Override
    public void deleteByIds(java.util.List<Long> ids) {
        if (cn.hutool.core.collection.CollUtil.isNotEmpty(ids)) {
            dataTracerManager.removeByIds(ids);
        }
    }

    @Override
    public void clean() {
        dataTracerManager.remove(com.mybatisflex.core.query.QueryWrapper.create());
    }
}
