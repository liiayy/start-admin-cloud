package cn.muziseo.service.demo.module.redisson.service;

import cn.muziseo.common.cache.utils.RedissonUtils;
import cn.muziseo.common.core.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Redisson 使用案例服务
 */
@Slf4j
@Service
public class RedissonTestService {

    /**
     * 场景一：模拟下单防重 (普通可重入锁)
     * 解决问题：防止用户极短时间内重复点击，或者同一个订单被多次处理
     */
    public void createOrder(Long userId, Long productId) {
        String lockKey = "order:create:" + userId + ":" + productId;
        // 1. 获取锁对象
        RLock lock = RedissonUtils.getLock(lockKey);

        try {
            // 2. 尝试获取锁
            // waitTime=3s: 最多等待3秒，如果3秒内没拿到锁，就放弃（返回false）
            // leaseTime=30s: 锁拿到后，30秒后自动释放（防止死锁）
            boolean acquired = lock.tryLock(3, 30, TimeUnit.SECONDS);

            if (acquired) {
                try {
                    log.info("【下单】获取锁成功，开始处理业务... 用户ID:{}", userId);
                    // 模拟业务处理耗时
                    Thread.sleep(1000);
                    log.info("【下单】业务处理完成");
                } finally {
                    // 3. 释放锁 (一定要在 finally 中)
                    lock.unlock();
                }
            } else {
                log.warn("【下单】系统繁忙，获取锁失败");
                throw new BusinessException("操作太快了，请稍后再试");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException("获取锁被中断");
        }
    }

    /**
     * 场景二：公平锁 (Fair Lock)
     * 解决问题：先到先得，避免线程饥饿。例如抢购、排队业务。
     */
    public void fairLockQueue(Long id) {
        String lockKey = "queue:resource";
        RLock fairLock = RedissonUtils.getFairLock(lockKey);

        try {
            // 公平锁通常会阻塞等待较长时间，保证大家都能排到
            if (fairLock.tryLock(10, 30, TimeUnit.SECONDS)) {
                try {
                    log.info("【公平锁】线程 {} 获得资源", id);
                    Thread.sleep(500);
                } finally {
                    fairLock.unlock();
                }
            } else {
                throw new BusinessException("排队人数过多，请稍后");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException("排队被中断");
        }
    }

    /**
     * 场景三：读写锁 - 读取配置 (Read Lock)
     * 特性：多个线程可以同时加读锁（共享），但在有写锁时会阻塞
     */
    public String getConfig(String key) {
        String lockKey = "config:lock:" + key;
        RLock readLock = RedissonUtils.getReadLock(lockKey);

        try {
            readLock.lock(10, TimeUnit.SECONDS);
            log.info("【读锁】正在读取配置: {}", key);
            // 模拟读取耗时
            Thread.sleep(500);
            return "config-value-" + key;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        } finally {
            readLock.unlock();
        }
    }

    /**
     * 场景三：读写锁 - 修改配置 (Write Lock)
     * 特性：写锁是独占的。一旦加上写锁，其他线程的读锁、写锁都会被阻塞，直到写锁释放。
     * 保证读取到的数据永远是最新一致的。
     */
    public void updateConfig(String key, String value) {
        String lockKey = "config:lock:" + key;
        RLock writeLock = RedissonUtils.getWriteLock(lockKey);

        try {
            log.info("【写锁】准备修改配置，正在等待锁...");
            // 写锁通常需要较长的等待时间，因为可能有很多读锁在排队
            if (writeLock.tryLock(10, 30, TimeUnit.SECONDS)) {
                try {
                    log.info("【写锁】获取成功，开始修改数据...");
                    Thread.sleep(2000); // 模拟较长的写入过程
                    log.info("【写锁】配置修改完成");
                } finally {
                    writeLock.unlock();
                }
            } else {
                throw new BusinessException("系统繁忙，正在更新配置");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException("更新中断");
        }
    }
}
