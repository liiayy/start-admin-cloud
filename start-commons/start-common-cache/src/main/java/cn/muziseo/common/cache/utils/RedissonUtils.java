package cn.muziseo.common.cache.utils;

import cn.muziseo.common.core.utils.spring.SpringUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.redisson.api.*;

import java.util.Collection;

/**
 * Redisson 分布式锁工具类
 *
 * @author 木子软件
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RedissonUtils {

    private static final RedissonClient CLIENT = SpringUtils.getBean(RedissonClient.class);

    /**
     * 获取客户端实例
     */
    public static RedissonClient getClient() {
        return CLIENT;
    }

    /**
     * 获取可重入锁
     *
     * @param lockKey 锁键
     */
    public static RLock getLock(String lockKey) {
        return CLIENT.getLock(lockKey);
    }

    /**
     * 获取公平锁
     *
     * @param lockKey 锁键
     */
    public static RLock getFairLock(String lockKey) {
        return CLIENT.getFairLock(lockKey);
    }

    /**
     * 获取读写锁
     *
     * @param lockKey 锁键
     */
    public static RReadWriteLock getReadWriteLock(String lockKey) {
        return CLIENT.getReadWriteLock(lockKey);
    }

    /**
     * 获取读锁
     *
     * @param lockKey 锁键
     */
    public static RLock getReadLock(String lockKey) {
        return CLIENT.getReadWriteLock(lockKey).readLock();
    }

    /**
     * 获取写锁
     *
     * @param lockKey 锁键
     */
    public static RLock getWriteLock(String lockKey) {
        return CLIENT.getReadWriteLock(lockKey).writeLock();
    }

    /**
     * 获取信号量
     *
     * @param lockKey 锁键
     */
    public static RSemaphore getSemaphore(String lockKey) {
        return CLIENT.getSemaphore(lockKey);
    }

    /**
     * 获取闭锁
     *
     * @param lockKey 锁键
     */
    public static RCountDownLatch getCountDownLatch(String lockKey) {
        return CLIENT.getCountDownLatch(lockKey);
    }

    /**
     * 删除对象
     *
     * @param key 键
     */
    public static boolean deleteObject(String key) {
        return CLIENT.getBucket(key).delete();
    }

    /**
     * 删除集合对象
     *
     * @param collection 多个对象
     */
    public static void deleteObject(Collection<?> collection) {
        RBatch batch = CLIENT.createBatch();
        collection.forEach(t -> batch.getBucket(t.toString()).deleteAsync());
        batch.execute();
    }

}
