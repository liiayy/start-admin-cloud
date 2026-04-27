package cn.muziseo.common.core.utils.trace;
 
import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;
 
import java.util.Map;
 
/**
 * MDC 异步线程装饰器
 * <p>
 * 解决 @Async 等异步场景下，MDC 上下文（如 TraceId）丢失的问题。
 *
 * @author 木子软件
 */
public class MdcTaskDecorator implements TaskDecorator {
 
    @Override
    public Runnable decorate(Runnable runnable) {
        // 获取当前线程的 MDC 上下文内容
        Map<String, String> contextMap = MDC.getCopyOfContextMap();
        
        return () -> {
            try {
                // 将上下文内容复制到异步线程
                if (contextMap != null) {
                    MDC.setContextMap(contextMap);
                }
                runnable.run();
            } finally {
                // 清理异步线程的 MDC
                MDC.clear();
            }
        };
    }
}
