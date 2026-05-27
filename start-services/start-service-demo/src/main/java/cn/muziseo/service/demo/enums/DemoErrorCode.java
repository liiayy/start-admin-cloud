package cn.muziseo.service.demo.enums;

import cn.muziseo.common.core.exception.errorCode.IErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 演示服务错误码定义 (系统编码 102)
 *
 * @author Antigravity
 */
@Getter
@AllArgsConstructor
public enum DemoErrorCode implements IErrorCode {

    DEMO_NOT_EXISTS(10201001, "演示数据不存在"),
    DEMO_NAME_EXISTS(10201002, "名称已存在"),
    LOCK_FAILED(10201003, "获取分布式锁失败，任务正在执行中，请勿重复操作！"),
    LOCK_INTERRUPTED(10201004, "锁任务执行被中断"),
    CUSTOM_DEMO_ERROR(10201005, "这是一条由后端抛出的预期业务异常，已由全局异常处理器拦截，并实现事务回滚。")
    ;

    private final int code;
    private final String message;
}
