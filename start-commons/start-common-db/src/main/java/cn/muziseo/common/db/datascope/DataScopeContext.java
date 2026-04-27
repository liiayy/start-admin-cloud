package cn.muziseo.common.db.datascope;
 
import cn.muziseo.common.core.domain.dto.DataScopeInfo;
import cn.muziseo.common.db.annotation.DataColumn;
 
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
 
/**
 * 数据权限上下文（ThreadLocal）
 * <p>
 * 配合 MyBatis-Flex 的 prepareAuth 机制，在方言层自动注入过滤条件。
 *
 * @author 木子软件
 */
public class DataScopeContext {
 
    private static final ThreadLocal<DataScopeInfo> CONTEXT = new ThreadLocal<>();
 
    /**
     * 表名与过滤字段的映射关系
     * 结构: { table_name: { DEPT: column_name, USER: column_name } }
     */
    private static final Map<String, Map<DataColumn.DataType, String>> TABLE_COLUMN_MAP = new ConcurrentHashMap<>();
 
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
     * 注册表过滤字段
     *
     * @param tableName 表名
     * @param type      维度类型
     * @param column    字段名
     */
    public static void register(String tableName, DataColumn.DataType type, String column) {
        TABLE_COLUMN_MAP.computeIfAbsent(tableName, k -> new EnumMap<>(DataColumn.DataType.class))
                .put(type, column);
    }
 
    /**
     * 获取表的所有过滤字段配置
     */
    public static Map<DataColumn.DataType, String> getColumnConfig(String tableName) {
        return TABLE_COLUMN_MAP.get(tableName);
    }
 
    /**
     * 获取表的特定维度过滤字段
     */
    public static String getColumn(String tableName, DataColumn.DataType type) {
        Map<DataColumn.DataType, String> config = getColumnConfig(tableName);
        return config != null ? config.get(type) : null;
    }
 
    /**
     * 判断表是否需要数据权限过滤
     */
    public static boolean isDataScopeTable(String tableName) {
        return TABLE_COLUMN_MAP.containsKey(tableName);
    }
}
