package cn.muziseo.service.demo.module.redis.service;

import cn.muziseo.common.cache.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;

/**
 * RedisUtils 使用案例服务
 */
@Slf4j
@Service
public class RedisTestService {

    /**
     * 场景一：基础 Key-Value 操作 (String)
     * 用途：缓存配置、Token、验证码等
     */
    public void testStringOps() {
        String key = "demo:string:user:1";

        // 1. 设置缓存，有效期 60秒
        RedisUtils.setCacheObject(key, "UserOne", Duration.ofSeconds(60));
        log.info("存入String: {}", key);

        // 2. 获取缓存
        String value = RedisUtils.getCacheObject(key);
        log.info("获取String: {}", value);

        // 3. 判断是否存在
        if (RedisUtils.hasKey(key)) {
            // 4. 获取过期时间 (秒)
            long expire = RedisUtils.getTimeToLive(key);
            log.info("剩余过期时间: {} ms", expire);
        }
    }

    /**
     * 场景二：List 操作 (队列/栈)
     * 用途：消息队列、浏览记录、公告列表
     */
    public void testListOps() {
        String key = "demo:list:history";

        // 1. 存入 List (追加)
        List<String> items = Arrays.asList("item1", "item2", "item3");
        RedisUtils.setCacheList(key, items);

        // 2. 追加单个元素
        RedisUtils.addCacheList(key, "item4");

        // 3. 获取整个 List
        List<String> cacheList = RedisUtils.getCacheList(key);
        log.info("获取List全量: {}", cacheList);

        // 4. 获取部分 (分页场景)
        List<String> rangeList = RedisUtils.getCacheListRange(key, 0, 2);
        log.info("获取List前3个: {}", rangeList);

        // 清理测试数据
        //RedisUtils.deleteObject(key);
    }

    /**
     * 场景三：Map 操作 (Hash)
     * 用途：存储对象属性、购物车、用户偏好设置
     * 优势：可以单独修改 Map 中的某一个字段，而不需要读取整个对象
     */
    public void testMapOps() {
        String key = "demo:map:cart:1001"; // 用户1001的购物车

        // 1. 存入整个 Map
        Map<String, Integer> cart = new HashMap<>();
        cart.put("Apple", 2);
        cart.put("Banana", 5);
        RedisUtils.setCacheMap(key, cart);

        // 2. 修改 Map 中的某一项 (无需重新put整个Map)
        RedisUtils.setCacheMapValue(key, "Apple", 10); // 苹果数量改为10

        // 3. 获取 Map 中的某一项
        Integer appleCount = RedisUtils.getCacheMapValue(key, "Apple");
        log.info("购物车苹果数量: {}", appleCount);

        // 4. 获取整个 Map
        Map<String, Integer> allCart = RedisUtils.getCacheMap(key);
        log.info("购物车详情: {}", allCart);
    }

    /**
     * 场景四：Set 操作 (集合)
     * 用途：点赞用户列表、共同好友、抽奖池
     * 特性：去重、无序
     */
    public void testSetOps() {
        String key = "demo:set:likes:article:1"; // 文章1的点赞列表

        // 1. 添加元素 (自动去重)
        RedisUtils.addCacheSet(key, "user_101");
        RedisUtils.addCacheSet(key, "user_102");
        RedisUtils.addCacheSet(key, "user_101"); // 重复添加无效

        // 2. 获取所有元素
        Set<String> likeUsers = RedisUtils.getCacheSet(key);
        log.info("点赞用户: {}", likeUsers); // 输出 user_101, user_102
    }

    /**
     * 场景五：原子计数器 (AtomicLong)
     * 用途：文章阅读量、全局自增ID、库存扣减
     */
    public void testAtomicOps() {
        String key = "demo:atomic:views:article:1";

        // 1. 初始化
        RedisUtils.setAtomicValue(key, 100);

        // 2. 自增 +1
        long val1 = RedisUtils.incrAtomicValue(key);
        log.info("阅读量+1后: {}", val1); // 101

        // 3. 自减 -1
        long val2 = RedisUtils.decrAtomicValue(key);
        log.info("阅读量-1后: {}", val2); // 100

        // 4. 直接获取值
        long current = RedisUtils.getAtomicValue(key);
        log.info("当前阅读量: {}", current);
    }
}
