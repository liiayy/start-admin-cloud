package cn.muziseo.service.demo.base;

import org.junit.jupiter.api.Tag;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

/**
 * 集成测试基类
 * <p>
 * 提供 H2 内存数据库环境、事务自动回滚，以及测试环境配置的加载。
 * 使用 ContextConfiguration 注册 RedisTestInitializer，在 Spring 启动前拉起 Redis 进程。
 * </p>
 *
 * @author 木子软件
 */
@SpringBootTest
@ActiveProfiles("test-h2")
@Tag("dev")
@Tag("test")
@Transactional
@Sql(scripts = "classpath:schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@ContextConfiguration(initializers = RedisTestInitializer.class)
public abstract class BaseIntegrationTest {
}
