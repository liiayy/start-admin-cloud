package cn.muziseo.service.demo.constant;

/**
 * Demo 模块分布式锁常量定义
 *
 * @author 木子软件
 */
public final class DemoLockConstants {

    private DemoLockConstants() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Demo 演示产品分布式锁前缀
     */
    public static final String DEMO_LOCK_PREFIX = "demo:lock:";

}
