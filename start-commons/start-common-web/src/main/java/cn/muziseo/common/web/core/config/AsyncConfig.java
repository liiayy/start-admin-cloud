package cn.muziseo.common.web.core.config;
 
import cn.muziseo.common.core.utils.trace.MdcTaskDecorator;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
 
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
 
/**
 * 异步任务配置
 * <p>
 * 配置自定义线程池，并注入 MdcTaskDecorator 以支持 TraceId 传递。
 *
 * @author 木子软件
 */
@AutoConfiguration
@EnableAsync
public class AsyncConfig {
 
    @Bean("taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 核心线程数
        executor.setCorePoolSize(10);
        // 最大线程数
        executor.setMaxPoolSize(50);
        // 队列容量
        executor.setQueueCapacity(200);
        // 线程前缀
        executor.setThreadNamePrefix("start-async-");
        // 拒绝策略：由调用者所在的线程执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        
        // 【核心】设置任务装饰器
        executor.setTaskDecorator(new MdcTaskDecorator());
        
        executor.initialize();
        return executor;
    }
}
