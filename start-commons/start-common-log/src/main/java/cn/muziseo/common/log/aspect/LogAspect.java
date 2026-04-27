package cn.muziseo.common.log.aspect;
 
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.date.StopWatch;
import cn.hutool.core.util.StrUtil;
import cn.muziseo.common.core.event.OperLogEvent;
import cn.muziseo.common.core.utils.json.JsonUtils;
import cn.muziseo.common.core.utils.mask.SensitiveUtils;
import cn.muziseo.common.core.utils.servlet.ServletUtils;
import cn.muziseo.common.log.annotation.Log;
import cn.muziseo.common.log.utils.IpLocationUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
 
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
 
/**
 * 操作日志记录处理
 * <p>
 * 深度优化版：支持注解脱敏、智能截断、异步发布。
 *
 * @author 木子软件
 */
@Aspect
@Slf4j
@Component
public class LogAspect {
 
    private final IpLocationUtils ipLocationUtils;
    private final ApplicationEventPublisher eventPublisher;
 
    /** 日志字段最大长度，防止撑爆数据库 */
    private static final int MAX_LENGTH = 2000;
 
    public LogAspect(IpLocationUtils ipLocationUtils, ApplicationEventPublisher eventPublisher) {
        this.ipLocationUtils = ipLocationUtils;
        this.eventPublisher = eventPublisher;
    }
 
    private static final ThreadLocal<StopWatch> TIME_THREADLOCAL = new ThreadLocal<>();
 
    @Pointcut("@annotation(cn.muziseo.common.log.annotation.Log)")
    public void logPointCut() {
    }
 
    @Before(value = "logPointCut()")
    public void doBefore() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        TIME_THREADLOCAL.set(stopWatch);
    }
 
    @AfterReturning(pointcut = "logPointCut()", returning = "jsonResult")
    public void doAfterReturning(JoinPoint joinPoint, Object jsonResult) {
        handleLog(joinPoint, null, jsonResult);
    }
 
    @AfterThrowing(value = "logPointCut()", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Exception e) {
        handleLog(joinPoint, e, null);
    }
 
    protected void handleLog(final JoinPoint joinPoint, final Exception e, Object jsonResult) {
        try {
            Log controllerLog = getAnnotationLog(joinPoint);
            if (controllerLog == null) {
                return;
            }
 
            StopWatch stopWatch = TIME_THREADLOCAL.get();
            if (stopWatch != null && stopWatch.isRunning()) {
                stopWatch.stop();
            }
 
            String username = "unknown";
            try {
                if (StpUtil.isLogin()) {
                    username = StpUtil.getLoginIdAsString();
                }
            } catch (Exception ignored) {}
 
            OperLogEvent operLog = new OperLogEvent();
            operLog.setStatus(e == null ? 0 : 1);
            operLog.setOperIp(ServletUtils.getClientIP());
            operLog.setOperUrl(ServletUtils.getRequest().getRequestURI());
            operLog.setOperName(username);
            operLog.setCreateTime(LocalDateTime.now());
            operLog.setCostTime(stopWatch != null ? stopWatch.getTotalTimeMillis() : 0L);
            operLog.setOperLocation(ipLocationUtils.getLocation(operLog.getOperIp()));
            operLog.setRequestMethod(ServletUtils.getRequest().getMethod());
            
            if (e != null) {
                operLog.setErrorMsg(StrUtil.sub(e.getMessage(), 0, MAX_LENGTH));
            }
            
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            operLog.setMethod(methodSignature.getDeclaringTypeName() + "." + methodSignature.getName() + "()");
            
            // 填充业务信息（包含脱敏处理）
            fillMethodDescription(joinPoint, controllerLog, operLog, jsonResult);
            
            // 发布事件
            eventPublisher.publishEvent(operLog);
 
        } catch (Exception exp) {
            log.error("[日志切面] 处理异常", exp);
        } finally {
            TIME_THREADLOCAL.remove();
        }
    }
 
    private void fillMethodDescription(JoinPoint joinPoint, Log log, OperLogEvent operLog, Object jsonResult) {
        operLog.setBusinessType(log.businessType().getValue());
        operLog.setTitle(log.title());
        operLog.setOperatorType(log.operatorType().getValue());
 
        // 1. 处理请求参数脱敏
        if (log.isSaveRequestData()) {
            setRequestData(joinPoint, operLog, log.excludeParamNames());
        }
 
        // 2. 处理响应结果脱敏
        if (log.isSaveResponseData() && jsonResult != null) {
            // 使用 SensitiveUtils 进行注解驱动脱敏
            String result = SensitiveUtils.toMaskJson(jsonResult);
            operLog.setJsonResult(StrUtil.sub(result, 0, MAX_LENGTH));
        }
    }
 
    private void setRequestData(JoinPoint joinPoint, OperLogEvent operLog, String[] excludeParamNames) {
        String requestMethod = operLog.getRequestMethod();
        if ("PUT".equals(requestMethod) || "POST".equals(requestMethod)) {
            String params = argsArrayToString(joinPoint.getArgs(), excludeParamNames);
            operLog.setOperParam(StrUtil.sub(params, 0, MAX_LENGTH));
        } else {
            Map<String, String[]> paramsMap = ServletUtils.getParams(ServletUtils.getRequest());
            String params = JsonUtils.toJsonString(paramsMap);
            // URL 参数通常不支持注解，走关键字正则脱敏
            operLog.setOperParam(StrUtil.sub(SensitiveUtils.maskJsonString(params, excludeParamNames), 0, MAX_LENGTH));
        }
    }
 
    private String argsArrayToString(Object[] paramsArray, String[] excludeParamNames) {
        if (paramsArray == null || paramsArray.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (Object o : paramsArray) {
            if (o != null && !isFilterObject(o)) {
                // 对象序列化时会自动触发 @Sensitive 脱敏
                String masked = SensitiveUtils.toMaskJson(o);
                // 再次进行关键字兜底脱敏（防止 DTO 没加注解）
                sb.append(SensitiveUtils.maskJsonString(masked, excludeParamNames)).append(" ");
            }
        }
        return sb.toString().trim();
    }
 
    private Log getAnnotationLog(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        return signature.getMethod().getAnnotation(Log.class);
    }
 
    private boolean isFilterObject(Object o) {
        Class<?> clazz = o.getClass();
        if (clazz.isArray()) {
            return clazz.getComponentType().isAssignableFrom(MultipartFile.class);
        } else if (Collection.class.isAssignableFrom(clazz)) {
            return ((Collection<?>) o).stream().anyMatch(v -> v instanceof MultipartFile);
        } else if (Map.class.isAssignableFrom(clazz)) {
            return ((Map<?, ?>) o).values().stream().anyMatch(v -> v instanceof MultipartFile);
        }
        return o instanceof MultipartFile || o instanceof HttpServletRequest 
            || o instanceof jakarta.servlet.http.HttpServletResponse || o instanceof BindingResult;
    }
}
