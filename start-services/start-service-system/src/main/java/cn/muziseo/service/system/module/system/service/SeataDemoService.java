package cn.muziseo.service.system.module.system.service;

import org.apache.seata.spring.annotation.GlobalTransactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Seata 分布式事务演示代码
 *
 * @author 木子软件
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SeataDemoService {

    /**
     * 演示分布式事务回滚
     * 
     * @param shouldFail 是否触发异常以演示回滚
     */
    @GlobalTransactional(name = "demo-create-order", rollbackFor = Exception.class)
    public void testGlobalTransaction(boolean shouldFail) {
        log.info("[Seata演示] 开始全局事务, XID: {}", org.apache.seata.core.context.RootContext.getXID());

        // 1. 调用本地数据库操作 (例如更新用户信息)
        // sysUserMapper.updateById(user);
        log.info("[Seata演示] 1. 执行本地数据库操作完成");

        // 2. 远程调用另一个微服务 (例如调用 demo-service)
        // demoApi.doSomething();
        log.info("[Seata演示] 2. 远程调用 demo-service 完成");

        // 3. 模拟异常触发回滚
        if (shouldFail) {
            log.error("[Seata演示] 3. 触发人为异常，准备回滚...");
            throw new RuntimeException("演示分布式事务回滚");
        }

        log.info("[Seata演示] 事务即将正常提交");
    }
}
