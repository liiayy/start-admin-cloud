package cn.muziseo.common.core.annotation;
 
import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;
 
/**
 * 幂等性注解
 * <p>
 * 用于防止分布式环境下接口的重复提交。
 *
 * @author 木子软件
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Idempotent {
 
    /**
     * 幂等 Key，支持 SpEL 表达式
     */
    String key() default "";
 
    /**
     * 锁定时间，默认 5 秒
     */
    long time() default 5;
 
    /**
     * 时间单位，默认秒
     */
    TimeUnit unit() default TimeUnit.SECONDS;
 
    /**
     * 提示消息
     */
    String message() default "请求处理中，请稍后重试";
 
    /**
     * 是否根据用户 ID 隔离（默认 true，即每个用户独立幂等）
     */
    boolean isUser() default true;
}
