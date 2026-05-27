package cn.muziseo.service.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 演示微服务启动类
 *
 * @author Antigravity
 */
@SpringBootApplication(scanBasePackages = {"cn.muziseo.service.demo"})
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "cn.muziseo")
@EnableAsync
@org.mybatis.spring.annotation.MapperScan("cn.muziseo.service.demo.module.**.repository.mapper")
public class DemoServerApplication {
    public static void main(String[] args) {
        try {
            Class.forName("org.postgresql.Driver");
            String sql = "CREATE TABLE IF NOT EXISTS public.undo_log (\n" +
                    "    id            SERIAL       NOT NULL,\n" +
                    "    branch_id     BIGINT       NOT NULL,\n" +
                    "    xid           VARCHAR(128) NOT NULL,\n" +
                    "    context       VARCHAR(128) NOT NULL,\n" +
                    "    rollback_info BYTEA        NOT NULL,\n" +
                    "    log_status    INT          NOT NULL,\n" +
                    "    log_created   timestamp(0) NOT NULL,\n" +
                    "    log_modified  timestamp(0) NOT NULL,\n" +
                    "    CONSTRAINT pk_undo_log PRIMARY KEY (id),\n" +
                    "    CONSTRAINT ux_undo_log UNIQUE (xid, branch_id)\n" +
                    ");\n" +
                    "CREATE INDEX IF NOT EXISTS ix_log_created ON undo_log(log_created);";

            String[] urls = {
                "jdbc:postgresql://192.168.100.20:5432/start-admin-system?characterEncoding=UTF-8",
                "jdbc:postgresql://192.168.100.20:5432/start-admin-demo?characterEncoding=UTF-8"
            };

            for (String url : urls) {
                try (java.sql.Connection conn = java.sql.DriverManager.getConnection(url, "root", "root");
                     java.sql.Statement stmt = conn.createStatement()) {
                    stmt.execute(sql);
                    System.out.println("[UndoLog Init] Successfully checked/created undo_log table for: " + url);
                } catch (Exception e) {
                    System.err.println("[UndoLog Init] Failed for: " + url + ", error: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        SpringApplication.run(DemoServerApplication.class, args);
    }
}
