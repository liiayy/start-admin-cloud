package cn.muziseo.system.utils;

import io.micrometer.common.lang.NonNull;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 在应用完全启动后显示 Banner
 * 使用 @Order 确保在其他初始化完成后执行
 * 监听 Spring 应用生命周期中的各种事件
 * 文章: <a href="https://code.muziseo.cn/archives/shi-me-shi-spring-applicationlistener">Spring 应用生命周期事件监听</a>
 */
@Component
@Profile("dev")  // 只在开发环境显示
@Order(Ordered.LOWEST_PRECEDENCE)  // 最后执行
public class StartupBanner implements ApplicationListener<ApplicationReadyEvent> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final long startTime;

    static {
        startTime = System.currentTimeMillis();
    }

    @Override
    public void onApplicationEvent(@NonNull ApplicationReadyEvent event) {
        // 使用虚拟线程异步显示
        Thread.startVirtualThread(() -> {
            try {
                // 确保所有初始化完成
                Thread.sleep(Duration.ofMillis(500));
                displayStartupInfo(event);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    private void displayStartupInfo(ApplicationReadyEvent event) {
        long endTime = System.currentTimeMillis();
        long startupTime = endTime - startTime;

        String border = "✧".repeat(60);

        System.out.println("\n" + border);
        System.out.println("✨ " + LocalDateTime.now().format(FORMATTER) + " | 启动完成 ✨");
        System.out.println(border);

        // 启动信息
        System.out.printf("⏱️  启动耗时: %.2f 秒\n", startupTime / 1000.0);

        // 文档链接
        System.out.println("\n📚 相关文档：");
        System.out.println("   接口文档 → https://cloud.iocoder.cn/api-doc/");


        // 内存信息
        Runtime runtime = Runtime.getRuntime();
        long usedMB = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024;
        long maxMB = runtime.maxMemory() / 1024 / 1024;

        System.out.println("\n💾 系统资源：");
        System.out.printf("   内存: %dMB / %dMB\n", usedMB, maxMB);
        System.out.printf("   处理器: %d 核心\n", runtime.availableProcessors());
        System.out.printf("   JDK版本: %s\n", System.getProperty("java.version"));

        System.out.println(border);
        System.out.println("🚀 服务已就绪，祝您使用愉快！");
        System.out.println(border + "\n");
    }
}