package cn.muziseo.service.demo.module.demo.manager;

import cn.muziseo.service.demo.base.BaseIntegrationTest;
import cn.muziseo.service.demo.module.demo.repository.entity.DemoEntity;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * DemoManager 持久层集成测试
 * <p>
 * 验证 MyBatis-Flex 的 SQL 拼接、雪花 ID 生成、数据审计字段填充、逻辑删除和分页查询在真实 H2 内存库上的表现。
 * </p>
 *
 * @author 木子软件
 */
@DisplayName("DemoManager 持久层集成测试")
class DemoManagerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private DemoManager demoManager;

    @Test
    @DisplayName("测试保存并根据主键获取 - 验证雪花主键生成与审计字段填充")
    void testSaveAndGetById() {
        DemoEntity entity = new DemoEntity();
        entity.setName("测试产品1");

        // 1. 保存
        boolean saved = demoManager.save(entity);
        assertThat(saved).isTrue();

        // 2. 校验雪花ID已被自动填充 (ID 应该是一个非零的 Long 值且不是自增的小数)
        Long generatedId = entity.getId();
        assertThat(generatedId).isNotNull();
        assertThat(generatedId).isGreaterThan(1000000L); // 雪花ID通常非常大

        // 3. 查询
        DemoEntity dbEntity = demoManager.getById(generatedId);
        assertThat(dbEntity).isNotNull();
        assertThat(dbEntity.getName()).isEqualTo("测试产品1");

        // 4. 校验审计字段自动填充
        assertThat(dbEntity.getCreateTime()).isNotNull();
        assertThat(dbEntity.getUpdateTime()).isNotNull();
        assertThat(dbEntity.getCreator()).isEqualTo("system");
        assertThat(dbEntity.getUpdater()).isEqualTo("system");
        assertThat(dbEntity.getDeleted()).isFalse();
    }

    @Test
    @DisplayName("测试逻辑删除 - 验证删除后记录仅被标记且查询不到")
    void testLogicDelete() {
        DemoEntity entity = new DemoEntity();
        entity.setName("测试产品2");
        demoManager.save(entity);
        Long id = entity.getId();

        // 1. 执行逻辑删除
        boolean removed = demoManager.removeById(id);
        assertThat(removed).isTrue();

        // 2. 使用默认查询，应该查不到该记录（因为 MyBatis-Flex 自动拼接了 deleted = false 条件）
        DemoEntity dbEntity = demoManager.getById(id);
        assertThat(dbEntity).isNull();

        // 3. 校验 existsByName 应该不存在
        boolean exists = demoManager.existsByName("测试产品2", null);
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("测试名称排重 - 验证 existsByName 的各种条件")
    void testExistsByName() {
        DemoEntity entity1 = new DemoEntity();
        entity1.setName("唯一产品A");
        demoManager.save(entity1);

        DemoEntity entity2 = new DemoEntity();
        entity2.setName("唯一产品B");
        demoManager.save(entity2);

        // 1. 查询已存在的名称，不排除任何 ID
        boolean exists = demoManager.existsByName("唯一产品A", null);
        assertThat(exists).isTrue();

        // 2. 查询已存在的名称，排除它自身的 ID，应该返回 false
        boolean existsExcludeSelf = demoManager.existsByName("唯一产品A", entity1.getId());
        assertThat(existsExcludeSelf).isFalse();

        // 3. 查询已存在的名称，排除其他记录的 ID，应该返回 true
        boolean existsExcludeOther = demoManager.existsByName("唯一产品A", entity2.getId());
        assertThat(existsExcludeOther).isTrue();

        // 4. 查询不存在的名称
        boolean existsNonExistent = demoManager.existsByName("不存在的产品", null);
        assertThat(existsNonExistent).isFalse();
    }

    @Test
    @DisplayName("测试分页查询 - 验证分页拦截与过滤")
    void testPageQuery() {
        // 1. 预置数据
        for (int i = 1; i <= 5; i++) {
            DemoEntity entity = new DemoEntity();
            entity.setName("分页测试产品" + i);
            demoManager.save(entity);
        }

        // 2. 分页查询
        Page<DemoEntity> page = new Page<>(1, 3);
        QueryWrapper qw = QueryWrapper.create()
                .where(DemoEntity::getName).like("分页测试产品");
        Page<DemoEntity> result = demoManager.page(page, qw);

        // 3. 校验分页结果
        assertThat(result).isNotNull();
        assertThat(result.getTotalRow()).isEqualTo(5);
        assertThat(result.getRecords()).hasSize(3);
    }
}
