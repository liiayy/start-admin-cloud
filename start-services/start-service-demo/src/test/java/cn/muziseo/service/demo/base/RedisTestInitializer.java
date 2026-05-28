package cn.muziseo.service.demo.base;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import redis.embedded.RedisServer;

import java.io.IOException;
import java.net.Socket;

/**
 * Spring context initializer to start Embedded Redis before bean creation.
 *
 * @author 木子软件
 */
public class RedisTestInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static RedisServer redisServer;

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        int redisPort = 16379;
        log("=== RedisTestInitializer: Checking port " + redisPort + " ===");
        if (!isPortInUse(redisPort)) {
            try {
                log("=== RedisTestInitializer: Starting Embedded Redis on port " + redisPort + " with password ===");
                redisServer = RedisServer.newRedisServer()
                        .port(redisPort)
                        .setting("requirepass 123456")
                        .build();
                redisServer.start();

                // Wait for port to be ready
                int retries = 30;
                while (retries > 0 && !isPortInUse(redisPort)) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ignored) {
                        Thread.currentThread().interrupt();
                    }
                    retries--;
                }

                if (isPortInUse(redisPort)) {
                    log("=== RedisTestInitializer: Embedded Redis started successfully on port " + redisPort + " ===");
                } else {
                    log("=== RedisTestInitializer: WARNING: Redis process launched but port " + redisPort + " is not listening yet ===");
                }

                // Register shutdown hook
                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    if (redisServer != null) {
                        try {
                            log("=== RedisTestInitializer: Stopping Embedded Redis ===");
                            redisServer.stop();
                            log("=== RedisTestInitializer: Embedded Redis stopped ===");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }));
            } catch (IOException e) {
                log("=== RedisTestInitializer: Failed to start Embedded Redis: " + e.getMessage() + " ===");
                throw new RuntimeException("Failed to start Embedded Redis on port " + redisPort, e);
            }
        } else {
            log("=== RedisTestInitializer: Port " + redisPort + " is already in use. Assuming Redis is already running. ===");
        }
    }

    private static boolean isPortInUse(int port) {
        try (Socket socket = new Socket("127.0.0.1", port)) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private static void log(String message) {
        System.out.println(message);
    }
}
