package cn.muziseo.common.core.constant;

/**
 * 分布式锁常量定义
 * <p>
 * 统一管理系统级分布式锁 Key 前缀，规范命名空间。
 * </p>
 *
 * @author 木子软件
 */
public final class LockConstants {

    private LockConstants() {
        throw new IllegalStateException("Utility class");
    }

    // 系统级锁前缀（例如网关限流锁、全局认证锁等）可以定义在此处

}
