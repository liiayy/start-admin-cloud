package cn.muziseo.service.system.module.monitor.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.muziseo.common.cache.utils.RedisUtils;
import cn.muziseo.common.core.event.ErrorLogEvent;
import cn.muziseo.common.db.page.PageResponse;
import cn.muziseo.service.system.module.monitor.manager.SysErrorLogManager;
import cn.muziseo.service.system.module.monitor.repository.entity.SysErrorLogEntity;
import cn.muziseo.service.system.module.monitor.service.SysErrorLogService;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.row.Db;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;

@Service
@Slf4j
@RequiredArgsConstructor
public class SysErrorLogServiceImpl implements SysErrorLogService {

    private final SysErrorLogManager errorLogManager;

    private static final String ERROR_LOG_KEY_PREFIX = "sys:error_log:fingerprint:";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveErrorLog(ErrorLogEvent event) {
        // 1. 生成错误指纹 (异常类名 + URI + 错误消息)
        String fingerprint = SecureUtil.md5(event.getErrorType() + event.getRequestUri() + event.getErrorMessage());
        String redisKey = ERROR_LOG_KEY_PREFIX + fingerprint;

        // 2. 检查 Redis 中是否存在该指纹 (1分钟窗口)
        Long existingId = RedisUtils.getCacheObject(redisKey);

        if (existingId != null) {
            // 3. 存在重复错误，增加发生次数
            errorLogManager.updateChain()
                    .setRaw(SysErrorLogEntity::getOccurrenceCount, "occurrence_count + 1")
                    .set(SysErrorLogEntity::getLastTime, LocalDateTime.now())
                    .where(SysErrorLogEntity::getId).eq(existingId)
                    .update();
            log.debug("检测到重复错误日志，增加计数: id={}", existingId);
        } else {
            // 4. 新错误，插入数据库
            SysErrorLogEntity entity = convertToEntity(event);
            entity.setOccurrenceCount(1);
            entity.setHandleStatus(0); // 待处理
            entity.setFirstTime(event.getCreateTime());
            entity.setLastTime(event.getCreateTime());
            entity.setCreateTime(LocalDateTime.now());
            
            errorLogManager.save(entity);
            
            // 5. 将 ID 存入 Redis，有效期 60 秒
            RedisUtils.setCacheObject(redisKey, entity.getId(), Duration.ofSeconds(60));
            log.debug("记录新错误日志: id={}", entity.getId());
        }
    }

    @Override
    public PageResponse<SysErrorLogEntity> page(int pageNum, int pageSize, SysErrorLogEntity query) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where(SysErrorLogEntity::getErrorType).like(query.getErrorType())
                .and(SysErrorLogEntity::getModuleName).eq(query.getModuleName())
                .and(SysErrorLogEntity::getHandleStatus).eq(query.getHandleStatus())
                .orderBy(SysErrorLogEntity::getCreateTime, false);
        return PageResponse.build(errorLogManager.page(new com.mybatisflex.core.paginate.Page<>(pageNum, pageSize), queryWrapper));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByIds(Long[] ids) {
        errorLogManager.removeByIds(Arrays.asList(ids));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clean() {
        Db.updateBySql("TRUNCATE TABLE system_error_log");
    }

    @Override
    public SysErrorLogEntity getById(Long id) {
        return errorLogManager.getById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateHandleStatus(Long id, Integer status, String remark) {
        errorLogManager.updateChain()
                .set(SysErrorLogEntity::getHandleStatus, status)
                .set(SysErrorLogEntity::getHandleRemark, remark)
                .set(SysErrorLogEntity::getHandleBy, StpUtil.getLoginIdAsString())
                .set(SysErrorLogEntity::getHandleTime, LocalDateTime.now())
                .where(SysErrorLogEntity::getId).eq(id)
                .update();
    }

    private SysErrorLogEntity convertToEntity(ErrorLogEvent event) {
        SysErrorLogEntity entity = new SysErrorLogEntity();
        entity.setErrorType(event.getErrorType());
        entity.setErrorMessage(event.getErrorMessage());
        entity.setErrorStack(event.getErrorStack());
        entity.setRequestUri(event.getRequestUri());
        entity.setRequestMethod(event.getRequestMethod());
        entity.setRequestParams(event.getRequestParams());
        entity.setRequestIp(event.getRequestIp());
        entity.setUserAgent(event.getUserAgent());
        entity.setUserId(event.getUserId());
        entity.setUserName(event.getUserName());
        entity.setModuleName(event.getModuleName());
        entity.setTraceId(event.getTraceId());
        entity.setServerName(event.getServerName());
        entity.setServerIp(event.getServerIp());
        return entity;
    }
}
