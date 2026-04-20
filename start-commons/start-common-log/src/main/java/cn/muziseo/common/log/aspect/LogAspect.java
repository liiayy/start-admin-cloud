package cn.muziseo.common.log.aspect;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.date.StopWatch;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.muziseo.common.core.utils.json.JsonUtils;
import cn.muziseo.common.core.utils.servlet.ServletUtils;
import cn.muziseo.common.core.utils.spring.SpringUtils;
import cn.muziseo.common.log.annotation.Log;
import cn.muziseo.common.core.event.OperLogEvent;
import cn.muziseo.common.log.utils.IpLocationUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;

/**
 * 操作日志记录处理
 */
@Aspect
@Slf4j
public class LogAspect {

    private final IpLocationUtils ipLocationUtils;

    public LogAspect(IpLocationUtils ipLocationUtils) {
        this.ipLocationUtils = ipLocationUtils;
    }

    /** 排除敏感属性字段 */
    public static final String[] EXCLUDE_PROPERTIES = { "password", "oldPassword", "newPassword", "confirmPassword" };

    // 统计耗时
    private static final ThreadLocal<StopWatch> TIME_THREADLOCAL = new ThreadLocal<>();

    /**
     * 配置织入点
     */
    @Pointcut("@annotation(cn.muziseo.common.log.annotation.Log)")
    public void logPointCut() {
    }

    /**
     * 处理请求前执行
     */
    @Before(value = "logPointCut()")
    public void doBefore() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        TIME_THREADLOCAL.set(stopWatch);
    }

    /**
     * 处理完请求后执行
     *
     * @param joinPoint 切点
     */
    @AfterReturning(pointcut = "logPointCut()", returning = "jsonResult")
    public void doAfterReturning(JoinPoint joinPoint, Object jsonResult) {
        handleLog(joinPoint, null, jsonResult);
    }

    /**
     * 拦截异常操作
     *
     * @param joinPoint 切点
     * @param e 异常
     */
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
            stopWatch.stop();

            // 获取当前的用户
            String username = "";
            try {
                if (StpUtil.isLogin()) {
                    username = StpUtil.getLoginIdAsString();
                }
            } catch (Exception ex) {
                // 忽略非登录态异常
            }

            // * (核心) 发布日志事件，交给 Service 异步落库
            OperLogEvent operLog = new OperLogEvent();
            operLog.setStatus(0); // 默认正常
            operLog.setOperIp(ServletUtils.getClientIP());
            operLog.setOperUrl(ServletUtils.getRequest().getRequestURI());
            operLog.setOperName(username);
            operLog.setCreateTime(LocalDateTime.now());
            operLog.setCostTime(stopWatch.getTotalTimeMillis());
            operLog.setOperLocation(ipLocationUtils.getLocation(operLog.getOperIp()));
            operLog.setRequestMethod(ServletUtils.getRequest().getMethod());
            
            if (e != null) {
                operLog.setStatus(1); // 状态异常
                operLog.setErrorMsg(StrUtil.sub(e.getMessage(), 0, 2000));
            }
            
            // 设置方法名称
            String className = joinPoint.getTarget().getClass().getName();
            String methodName = joinPoint.getSignature().getName();
            operLog.setMethod(className + "." + methodName + "()");
            
            // 处理注解上的参数
            getControllerMethodDescription(joinPoint, controllerLog, operLog, jsonResult);
            
            // 发布事件
            SpringUtils.getApplicationContext().publishEvent(operLog);

        } catch (Exception exp) {
            log.error("日志切面异常: {}", exp.getMessage());
        } finally {
            TIME_THREADLOCAL.remove();
        }
    }

    /**
     * 获取注解中对方法的描述信息 用于Controller层注解
     *
     * @param log 日志
     * @param operLog 操作日志
     */
    public void getControllerMethodDescription(JoinPoint joinPoint, Log log, OperLogEvent operLog, Object jsonResult) {
        // 设置业务类型
        operLog.setBusinessType(log.businessType().getValue());
        // 设置模块标题
        operLog.setTitle(log.title());
        // 设置操作人类别
        operLog.setOperatorType(log.operatorType().getValue());
        // 是否需要保存request，参数
        if (log.isSaveRequestData()) {
            setRequestData(joinPoint, operLog, log.excludeParamNames());
        }
        // 是否需要保存response，参数
        if (log.isSaveResponseData() && jsonResult != null) {
            operLog.setJsonResult(StrUtil.sub(JsonUtils.toJsonString(jsonResult), 0, 2000));
        }
    }

    /**
     * 获取请求参数，并进行脱敏处理
     */
    private void setRequestData(JoinPoint joinPoint, OperLogEvent operLog, String[] excludeParamNames) {
        String requestMethod = operLog.getRequestMethod();
        if ("PUT".equals(requestMethod) || "POST".equals(requestMethod)) {
            String params = argsArrayToString(joinPoint.getArgs(), excludeParamNames);
            operLog.setOperParam(StrUtil.sub(params, 0, 2000));
        } else {
            Map<String, String[]> paramsMap = ServletUtils.getParams(ServletUtils.getRequest());
            operLog.setOperParam(StrUtil.sub(JsonUtils.toJsonString(paramsMap), 0, 2000));
        }
    }

    /**
     * 参数拼装并脱敏
     */
    private String argsArrayToString(Object[] paramsArray, String[] excludeParamNames) {
        StringBuilder params = new StringBuilder();
        if (paramsArray != null) {
            for (Object o : paramsArray) {
                if (o != null && !isFilterObject(o)) {
                    try {
                        String jsonObj = JsonUtils.toJsonString(o);
                        // 脱敏处理
                        params.append(maskSensitiveData(jsonObj, excludeParamNames)).append(" ");
                    } catch (Exception e) {
                        log.warn("解析方法参数异常");
                    }
                }
            }
        }
        return params.toString().trim();
    }

    /**
     * 简单的 JSON 脱敏逻辑
     */
    private String maskSensitiveData(String json, String[] excludeParamNames) {
        if (StrUtil.isBlank(json)) return json;
        String result = json;
        // 合并通用脱敏字段和注解指定的字段
        String[] allExcludes = ArrayUtil.addAll(EXCLUDE_PROPERTIES, excludeParamNames);
        for (String key : allExcludes) {
            // 简单正则：匹配 "key":"value" 或 "key":value
            String regex = "\"" + key + "\":\\s*\"[^\"]+\"";
            result = result.replaceAll(regex, "\"" + key + "\":\"******\"");
        }
        return result;
    }

    /**
     * 判断是否为日志注解
     */
    private Log getAnnotationLog(JoinPoint joinPoint) {
        try {
            return joinPoint.getTarget().getClass().getMethod(joinPoint.getSignature().getName(), 
                ((org.aspectj.lang.reflect.MethodSignature) joinPoint.getSignature()).getParameterTypes()).getAnnotation(Log.class);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 判断是否需要过滤的对象
     */
    public boolean isFilterObject(final Object o) {
        Class<?> clazz = o.getClass();
        if (clazz.isArray()) {
            return clazz.getComponentType().isAssignableFrom(MultipartFile.class);
        } else if (Collection.class.isAssignableFrom(clazz)) {
            Collection collection = (Collection) o;
            for (Object value : collection) {
                return value instanceof MultipartFile;
            }
        } else if (Map.class.isAssignableFrom(clazz)) {
            Map map = (Map) o;
            for (Object value : map.entrySet()) {
                Map.Entry entry = (Map.Entry) value;
                return entry.getValue() instanceof MultipartFile;
            }
        }
        return o instanceof MultipartFile || o instanceof jakarta.servlet.http.HttpServletRequest 
            || o instanceof jakarta.servlet.http.HttpServletResponse || o instanceof BindingResult;
    }
}
