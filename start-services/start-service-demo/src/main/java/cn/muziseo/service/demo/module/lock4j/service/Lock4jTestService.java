package cn.muziseo.service.demo.module.lock4j.service;

import com.baomidou.lock.annotation.Lock4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Lock4j 注解式分布式锁演示服务
 */
@Slf4j
@Service
public class Lock4jTestService {

    /**
     * 场景：用户绑定三方账号防并发
     *
     * @param userId 用户ID
     * @param authId 三方平台唯一ID
     */
    @Lock4j(keys = {"#userId", "#authId"}, // 动态Key：lock4j:bind:1001:WX_xxxxx
            expire = 30000, // 锁过期时间 30s
            acquireTimeout = 5000 // 获取锁超时 5s
    )
    public void bindSocialAccount(Long userId, String authId) {
        log.info("【Lock4j】开始执行绑定逻辑... User: {}, AuthId: {}", userId, authId);

        try {
            // 模拟业务耗时，验证锁的排他性
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        log.info("【Lock4j】绑定成功！");
    }

    /**
     * 场景：自定义锁名称和策略
     * 如果获取锁失败，抛出指定异常或执行自定义策略
     */
    @Lock4j(name = "custom-biz-lock", keys = {"#orderId"}, acquireTimeout = 1000, // 快速失败
            expire = 5000)
    public void processOrder(String orderId) {
        log.info("【Lock4j】正在处理订单: {}", orderId);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
