package cn.muziseo.service.demo.module.demo.service.impl;

import cn.muziseo.common.core.exception.BusinessException;
import cn.muziseo.service.demo.base.BaseIntegrationTest;
import cn.muziseo.service.demo.enums.DemoErrorCode;
import cn.muziseo.service.demo.module.demo.controller.vo.DemoVO;
import cn.muziseo.service.demo.module.demo.manager.DemoManager;
import cn.muziseo.service.demo.module.demo.repository.entity.DemoEntity;
import cn.muziseo.service.demo.module.demo.service.DemoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * DemoService 缓存与分布式锁集成测试
 * <p>
 * 配合 Embedded Redis 验证 @Cacheable 读缓存、@CacheEvict 清缓存，以及 Redisson 分布式锁的排他逻辑。
 * </p>
 *
 * @author 木子软件
 */
@DisplayName("DemoService 缓存与分布式锁集成测试")
class DemoServiceCacheIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private DemoService demoService;

    @MockitoSpyBean
    private DemoManager demoManager;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private Long savedId;

    @BeforeEach
    void setUp() {
        // 预置一条测试数据
        DemoEntity entity = new DemoEntity();
        entity.setName("缓存测试产品");
        demoManager.save(entity);
        savedId = entity.getId();

        // 显式清理此 ID 的缓存以确保干净环境
        String cacheKey = "demo:product:" + savedId;
        redisTemplate.delete(cacheKey);

        // 重置 Spy 的调用计数
        reset(demoManager);
    }

    @Test
    @DisplayName("测试 Spring Cache 缓存读取与驱逐 - 验证三部曲：首次查询写缓存、二次查询查缓存、驱逐后重新查库")
    void testSpringCacheLifecycle() {
        // 1. 首次查询：应该查数据库，并且把结果写入 Redis 缓存
        DemoVO firstVo = demoService.getCachedProduct(savedId);
        assertThat(firstVo).isNotNull();
        assertThat(firstVo.getName()).isEqualTo("缓存测试产品");
        verify(demoManager, times(1)).getById(savedId);

        // 验证缓存已被填充到 Redis
        String cacheKey = "demo:product:" + savedId;
        assertThat(redisTemplate.hasKey(cacheKey)).isTrue();

        // 2. 二次查询：应该直接命中 Redis 缓存，不再触发数据库查询
        reset(demoManager);
        DemoVO secondVo = demoService.getCachedProduct(savedId);
        assertThat(secondVo).isNotNull();
        assertThat(secondVo.getName()).isEqualTo("缓存测试产品");
        verify(demoManager, never()).getById(savedId);

        // 3. 执行缓存驱逐
        demoService.evictCache(savedId);
        // 验证缓存键已从 Redis 被清除
        assertThat(redisTemplate.hasKey(cacheKey)).isFalse();

        // 4. 再次查询：缓存已失效，应该重新查询数据库并重建缓存
        reset(demoManager);
        DemoVO thirdVo = demoService.getCachedProduct(savedId);
        assertThat(thirdVo).isNotNull();
        assertThat(thirdVo.getName()).isEqualTo("缓存测试产品");
        verify(demoManager, times(1)).getById(savedId);
        assertThat(redisTemplate.hasKey(cacheKey)).isTrue();
    }

    @Test
    @DisplayName("测试 Redisson 分布式并发锁 - 验证排他性与获取失败")
    void testRedissonDistributedLock() throws Exception {
        String lockKey = "testKey";

        // 1. 启动一个异步任务获取锁并持有（executeWithLock 会 Sleep 3 秒）
        CompletableFuture<String> lockFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return demoService.executeWithLock(lockKey);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        // 稍微等待，确保异步线程已经成功获取到了锁并开始 Sleep
        Thread.sleep(500);

        // 2. 在主线程尝试获取同名的锁，因为 tryLock(0, ...) 且锁已被占，应该立刻抛出并发锁失败异常
        assertThatThrownBy(() -> demoService.executeWithLock(lockKey))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", DemoErrorCode.LOCK_FAILED);

        // 3. 等待异步任务执行完成
        String result = lockFuture.get();
        assertThat(result).isEqualTo("锁任务执行成功！");

        // 4. 锁释放后，再次尝试获取锁，应该能够成功获取
        String secondResult = demoService.executeWithLock(lockKey);
        assertThat(secondResult).isEqualTo("锁任务执行成功！");
    }
}
