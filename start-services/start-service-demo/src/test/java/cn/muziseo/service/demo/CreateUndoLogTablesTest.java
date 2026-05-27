package cn.muziseo.service.demo;

import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class CreateUndoLogTablesTest {

    @Test
    public void createTables() throws Exception {
        String sql = "CREATE TABLE IF NOT EXISTS public.undo_log (\n" +
                "    id            SERIAL       NOT NULL,\n" +
                "    branch_id     BIGINT       NOT NULL,\n" +
                "    xid           VARCHAR(128) NOT NULL,\n" +
                "    context       VARCHAR(128) NOT NULL,\n" +
                "    rollback_info BYTEA        NOT NULL,\n" +
                "    log_status    INT          NOT NULL,\n" +
                "    log_created   TIMESTAMP(6) NOT NULL,\n" +
                "    log_modified  TIMESTAMP(6) NOT NULL,\n" +
                "    CONSTRAINT pk_undo_log PRIMARY KEY (id),\n" +
                "    CONSTRAINT ux_undo_log UNIQUE (xid, branch_id)\n" +
                ");\n" +
                "CREATE INDEX IF NOT EXISTS ix_log_created ON undo_log(log_created);";

        String[] urls = {
            "jdbc:postgresql://192.168.100.20:5432/start-admin-system?characterEncoding=UTF-8",
            "jdbc:postgresql://192.168.100.20:5432/start-admin-demo?characterEncoding=UTF-8"
        };

        Class.forName("org.postgresql.Driver");

        for (String url : urls) {
            System.out.println("Connecting to: " + url);
            try (Connection conn = DriverManager.getConnection(url, "root", "root");
                 Statement stmt = conn.createStatement()) {
                stmt.execute(sql);
                System.out.println("Successfully created undo_log table for: " + url);
            } catch (Exception e) {
                System.err.println("Failed to create table for: " + url);
                e.printStackTrace();
            }
        }
    }
}
