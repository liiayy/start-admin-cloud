package cn.muziseo.common.db.datascope;

import cn.muziseo.common.core.domain.dto.DataScopeInfo;
import java.util.HashSet;
import java.util.Set;

/**
 * 数据权限上下文（ThreadLocal）
 * <p>
 * 配合 MyBatis-Flex 的 prepareAuth 机制，在方言层自动注入 dept_id 过滤条件。
 * 使用方式：在 Service 方法上加 @DataScope 注解，AOP 切面自动填充上下文。
 *
 * @author 木子软件
 */
public class DataScopeContext {

    private static final ThreadLocal<DataScopeInfo> CONTEXT = new ThreadLocal<>();

    /**
     * 需要进行数据权限过滤的表名集合
     */
    private static final Set<String> DATA_SCOPE_TABLES = new HashSet<>(Set.of(
            "system_user",
            "system_post"
    ));

    public static void set(DataScopeInfo info) {
        CONTEXT.set(info);
    }

    public static DataScopeInfo get() {
        return CONTEXT.get();
    }

    public static void clear() {
        CONTEXT.remove();
    }

    /**
     * 判断表是否需要数据权限过滤
     */
    public static boolean isDataScopeTable(String tableName) {
        return tableName != null && DATA_SCOPE_TABLES.contains(tableName);
    }

    /**
     * 添加需要过滤的表名
     */
    public static void addDataScopeTable(String tableName) {
        DATA_SCOPE_TABLES.add(tableName);
    }
}
