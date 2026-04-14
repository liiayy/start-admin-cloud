package cn.muziseo.common.db.datascope;

import com.mybatisflex.core.dialect.KeywordWrap;
import com.mybatisflex.core.dialect.LimitOffsetProcessor;
import com.mybatisflex.core.dialect.OperateType;
import com.mybatisflex.core.dialect.impl.CommonsDialectImpl;
import com.mybatisflex.core.query.CPI;
import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.query.QueryTable;
import com.mybatisflex.core.query.QueryWrapper;

import java.util.List;

/**
 * 数据权限方言
 * <p>
 * 继承 MyBatis-Flex 的 CommonsDialectImpl，重写 prepareAuth 方法，
 * 在 SQL 构建前自动注入 dept_id 过滤条件。使用了官方推荐的方式3实现数据权限。
 * <p>
 * 配合 DataScopeContext（ThreadLocal）和 @DataScope 注解使用：
 * - Service 方法上加 @DataScope → AOP 设置上下文
 * - 方言读取上下文 → 自动添加 WHERE dept_id IN (...) 条件
 * - AOP 清除上下文
 *
 * @author 木子软件
 */
public class DataScopeDialect extends CommonsDialectImpl {

    /**
     * 必须显式指定使用 PostgreSQL的限制处理器和双引号包装，
     * 否则 CommonsDialectImpl 会默认使用 MySQL风格的反引号（`），从而导致 PostgreSQL 报错 syntax error。
     */
    public DataScopeDialect() {
        super(KeywordWrap.DOUBLE_QUOTATION, LimitOffsetProcessor.POSTGRESQL);
    }

    @Override
    public void prepareAuth(QueryWrapper queryWrapper, OperateType operateType) {
        DataScopeInfo scopeInfo = DataScopeContext.get();

        // 1. 快速失败：如果没有上下文或不需要过滤，直接执行父类逻辑
        if (scopeInfo == null || !scopeInfo.isFilter()) {
            super.prepareAuth(queryWrapper, operateType);
            return;
        }

        List<Long> deptIds = scopeInfo.getDeptIds();
        List<QueryTable> tables = CPI.getQueryTables(queryWrapper);

        // 2. 快速失败：没有部门数据或没有表信息
        if (deptIds == null || deptIds.isEmpty() || tables == null) {
            super.prepareAuth(queryWrapper, operateType);
            return;
        }

        // 3. 核心业务逻辑：平铺循环
        for (QueryTable table : tables) {
            if (DataScopeContext.isDataScopeTable(table.getName())) {
                QueryColumn deptIdCol = new QueryColumn(table.getName(), "dept_id");
                queryWrapper.and(deptIdCol.in(deptIds));
            }
        }

        super.prepareAuth(queryWrapper, operateType);
    }
}

