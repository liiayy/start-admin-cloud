package cn.muziseo.service.demo.module.demo.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.muziseo.common.core.exception.BusinessException;
import cn.muziseo.common.db.page.PageResponse;
import cn.muziseo.service.demo.enums.DemoErrorCode;
import cn.muziseo.service.demo.module.demo.controller.converter.DemoConverter;
import cn.muziseo.service.demo.module.demo.controller.request.DemoAddRequest;
import cn.muziseo.service.demo.module.demo.controller.request.DemoPageRequest;
import cn.muziseo.service.demo.module.demo.controller.vo.DemoVO;
import cn.muziseo.service.demo.module.demo.manager.DemoManager;
import cn.muziseo.service.demo.module.demo.repository.entity.DemoEntity;
import cn.muziseo.service.demo.module.demo.service.DemoService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import cn.muziseo.common.core.datatracer.DataTracerTypeEnum;
import cn.muziseo.common.log.utils.DataTracerUtils;
import cn.muziseo.service.system.module.auth.api.UserApi;
import io.seata.spring.annotation.GlobalTransactional;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 演示产品 Service 实现类
 *
 * @author 木子软件
 */
@Service
@Slf4j
public class DemoServiceImpl implements DemoService {

    @Resource
    private DemoManager demoManager;

    @Resource
    private DemoConverter demoConverter;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private UserApi userApi;

    @Override
    public PageResponse<DemoVO> page(DemoPageRequest request) {
        QueryWrapper qw = QueryWrapper.create()
                .like(DemoEntity::getName, request.getName(), StrUtil.isNotBlank(request.getName()))
                .orderBy(DemoEntity::getId).desc();

        Page<DemoEntity> page = demoManager.page(new Page<>(request.getPageNum(), request.getPageSize()), qw);
        List<DemoVO> list = page.getRecords().stream()
                .map(demoConverter::toVO)
                .toList();

        PageResponse<DemoVO> response = new PageResponse<>();
        response.setList(list);
        response.setTotal(page.getTotalRow());
        return response;
    }

    @Override
    public DemoVO getById(Long id) {
        DemoEntity entity = demoManager.getById(id);
        if (entity == null) {
            throw new BusinessException(DemoErrorCode.DEMO_NOT_EXISTS);
        }
        return demoConverter.toVO(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(DemoAddRequest request) {
        if (demoManager.existsByName(request.getName(), null)) {
            throw new BusinessException(DemoErrorCode.DEMO_NAME_EXISTS);
        }
        DemoEntity entity = demoConverter.toEntity(request);
        demoManager.save(entity);
        
        // 记录数据新增变更记录
        DataTracerUtils.insert(entity.getId(), DataTracerTypeEnum.DEMO);
     }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, DemoAddRequest request) {
        DemoEntity entity = demoManager.getById(id);
        if (entity == null) {
            throw new BusinessException(DemoErrorCode.DEMO_NOT_EXISTS);
        }
        if (demoManager.existsByName(request.getName(), id)) {
            throw new BusinessException(DemoErrorCode.DEMO_NAME_EXISTS);
        }
        
        // 备份旧实体，用于变更差异比对
        DemoEntity oldEntity = cn.hutool.core.bean.BeanUtil.toBean(entity, DemoEntity.class);
        
        entity.setName(request.getName());
        demoManager.updateById(entity);
        
        // 比对并记录数据更新变更记录
        DataTracerUtils.update(id, DataTracerTypeEnum.DEMO, oldEntity, entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        DemoEntity entity = demoManager.getById(id);
        if (entity == null) {
            throw new BusinessException(DemoErrorCode.DEMO_NOT_EXISTS);
        }
        demoManager.removeById(id);
        
        // 记录数据删除变更记录
        DataTracerUtils.delete(id, DataTracerTypeEnum.DEMO);
    }

    @Override
    @Cacheable(cacheNames = "demo:product", key = "#id")
    public DemoVO getCachedProduct(Long id) {
        log.info("[Cache Demo] Query database for ID: {}", id);
        return getById(id);
    }

    @Override
    @CacheEvict(cacheNames = "demo:product", key = "#id")
    public void evictCache(Long id) {
        log.info("[Cache Demo] Cache evicted for ID: {}", id);
    }

    @Override
    public String executeWithLock(String lockKey) {
        String key = "demo:lock:" + lockKey;
        RLock lock = redissonClient.getLock(key);
        try {
            // 尝试获取锁，等待0秒，锁定5秒
            boolean acquired = lock.tryLock(0, 5, TimeUnit.SECONDS);
            if (!acquired) {
                throw new BusinessException(DemoErrorCode.LOCK_FAILED);
            }
            log.info("[Lock Demo] Acquired lock successfully for key: {}", key);
            // 模拟 3 秒耗时业务
            Thread.sleep(3000);
            log.info("[Lock Demo] Task completed for key: {}", key);
            return "锁任务执行成功！";
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException(DemoErrorCode.LOCK_INTERRUPTED);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.info("[Lock Demo] Released lock successfully for key: {}", key);
            }
        }
    }

    @Override
    @GlobalTransactional(rollbackFor = Exception.class)
    public void testSeata(Long userId, String nickname, String demoName, boolean throwEx) {
        log.info("[Seata Demo] Start global transaction...");
        
        // 1. 远程调用 system-service 修改用户昵称
        log.info("[Seata Demo] Step 1: RPC call to update user {} nickname to: {}", userId, nickname);
        userApi.updateNickname(userId, nickname);

        // 2. 本地插入一条 demo 数据
        log.info("[Seata Demo] Step 2: Save local demo record with name: {}", demoName);
        DemoEntity demo = new DemoEntity();
        demo.setName(demoName);
        demoManager.save(demo);

        // 3. 触发异常回滚测试
        if (throwEx) {
            log.warn("[Seata Demo] Step 3: Deliberately throwing business exception to trigger global rollback...");
            throw new BusinessException(DemoErrorCode.CUSTOM_DEMO_ERROR, "故意报错触发 Seata 全局事务回滚！");
        }
        log.info("[Seata Demo] Global transaction committed successfully.");
    }
}
