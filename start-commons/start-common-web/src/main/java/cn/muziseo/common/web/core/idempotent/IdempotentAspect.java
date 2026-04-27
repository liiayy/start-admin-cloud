package cn.muziseo.common.web.core.idempotent;
 
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import cn.muziseo.common.cache.utils.RedissonUtils;
import cn.muziseo.common.core.annotation.Idempotent;
import cn.muziseo.common.core.exception.ServiceException;
import cn.muziseo.common.core.exception.errorCode.CommonErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
 
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
 
/**
 * 幂等性切面（高性能加固版）
 *
 * @author 木子软件
 */
@Aspect
@Slf4j
public class IdempotentAspect {
 
    private final ExpressionParser parser = new SpelExpressionParser();
    private final DefaultParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();
    
    /**
     * SpEL 表达式缓存，提升重复请求的解析性能
     */
    private final Map<String, Expression> expressionCache = new ConcurrentHashMap<>();
 
    @Around("@annotation(idempotent)")
    public Object around(ProceedingJoinPoint joinPoint, Idempotent idempotent) throws Throwable {
        String key = getLockKey(joinPoint, idempotent);
        RLock lock = RedissonUtils.getLock(key);
 
        // 尝试加锁。waitTime 为 0 表示立即返回。
        // leaseTime 设置为注解指定的防重时间，锁定期间不允许再次提交。
        boolean isLocked = lock.tryLock(0, idempotent.time(), idempotent.unit());
        if (!isLocked) {
            log.warn("[幂等校验] 触发重复请求拦截, key: {}", key);
            throw new ServiceException(CommonErrorCode.OPERATION_TOO_FREQUENT, idempotent.message());
        }
 
        try {
            return joinPoint.proceed();
        } finally {
            // 注意：此处不手动释放锁（lock.unlock()）。
            // 幂等性的核心是在【一定时间内】拒绝重复请求，因此依赖锁的自动过期（Lease Time）来形成幂等窗口。
        }
    }
 
    /**
     * 构建唯一的幂等 Key
     */
    private String getLockKey(ProceedingJoinPoint joinPoint, Idempotent idempotent) {
        StringBuilder key = new StringBuilder("idempotent:");
        
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        
        // 1. 追加类名和方法名（包含参数类型以区分重载）
        key.append(method.getDeclaringClass().getName()).append(":").append(method.getName());
        for (Class<?> type : method.getParameterTypes()) {
            key.append(":").append(type.getSimpleName());
        }
 
        // 2. 追加用户隔离标识（基于 Sa-Token 更加安全）
        if (idempotent.isUser()) {
            Object loginId = StpUtil.getLoginIdDefaultNull();
            if (loginId != null) {
                key.append(":u:").append(loginId);
            }
        }
 
        // 3. 解析并追加 SpEL 动态 Key
        if (StrUtil.isNotBlank(idempotent.key())) {
            String spelValue = parseSpel(idempotent.key(), joinPoint);
            key.append(":k:").append(spelValue);
        }
 
        return key.toString();
    }
 
    /**
     * 解析 SpEL 表达式（带缓存处理）
     */
    private String parseSpel(String spel, ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] args = joinPoint.getArgs();
        String[] parameterNames = nameDiscoverer.getParameterNames(method);
 
        EvaluationContext context = new StandardEvaluationContext();
        if (parameterNames != null) {
            for (int i = 0; i < parameterNames.length; i++) {
                context.setVariable(parameterNames[i], args[i]);
            }
        }
 
        try {
            // 从缓存中获取解析过的表达式对象
            Expression expression = expressionCache.computeIfAbsent(spel, parser::parseExpression);
            return String.valueOf(expression.getValue(context));
        } catch (Exception e) {
            log.error("[幂等校验] SpEL 解析失败: {}", spel, e);
            return spel;
        }
    }
}
