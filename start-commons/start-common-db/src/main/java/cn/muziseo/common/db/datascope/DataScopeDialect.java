package cn.muziseo.common.db.datascope;

import com.mybatisflex.core.dialect.impl.CommonsDialectImpl;
import com.mybatisflex.core.query.CPI;
import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.query.QueryTable;
import com.mybatisflex.core.query.QueryWrapper;

import java.util.List;

/**
 * 数据权限方言
 * <p>
 * 继承 MyBatis-Flex 的 CommonsDialectImpl，重写 forSelectByQuery 方法，
 * 在 SELECT SQL 构建时自动注入 dept_id 过滤条件。
 * <p>
 * 配合 DataScopeContext（ThreadLocal）和 @DataScope 注解使用：
 * - Service 方法上加 @DataScope → AOP 设置上下文
 * - 方言读取上下文 → 自动添加 WHERE dept_id IN (...) 条件
 * - AOP 清除上下文
 *
 * @author 木子软件
 */
public class DataScopeDialect extends CommonsDialectImpl {

    @Override
    public String forSelectByQuery(QueryWrapper queryWrapper) {
        DataScopeInfo scopeInfo = DataScopeContext.get();
        if (scopeInfo != null && scopeInfo.isFilter()) {
            List<Long> deptIds = scopeInfo.getDeptIds();
            if (deptIds != null && !deptIds.isEmpty()) {
                List<QueryTable> tables = CPI.getQueryTables(queryWrapper);
                if (tables != null) {
                    for (QueryTable table : tables) {
                        if (DataScopeContext.isDataScopeTable(table.getName())) {
                            QueryColumn deptIdCol = new QueryColumn("dept_id");
                            queryWrapper.and(deptIdCol.in(deptIds));
                            break;
                        }
                    }
                }
            }
        }
        return super.forSelectByQuery(queryWrapper);
    }
}
