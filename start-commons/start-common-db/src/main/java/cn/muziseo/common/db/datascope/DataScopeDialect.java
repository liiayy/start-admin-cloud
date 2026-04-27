package cn.muziseo.common.db.datascope;
 
import cn.muziseo.common.core.domain.dto.DataScopeInfo;
import cn.muziseo.common.db.annotation.DataColumn;
import com.mybatisflex.core.dialect.KeywordWrap;
import com.mybatisflex.core.dialect.LimitOffsetProcessor;
import com.mybatisflex.core.dialect.OperateType;
import com.mybatisflex.core.dialect.impl.CommonsDialectImpl;
import com.mybatisflex.core.query.CPI;
import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.query.QueryTable;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
 
import java.util.List;
import java.util.Map;
 
/**
 * 数据权限方言
 * <p>
 * 继承 MyBatis-Flex 的 CommonsDialectImpl，重写 prepareAuth 方法，
 * 在 SQL 构建前根据 DataScopeContext 注册的配置动态注入过滤条件。
 *
 * @author 木子软件
 */
@Slf4j
public class DataScopeDialect extends CommonsDialectImpl {
 
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
 
        List<QueryTable> tables = CPI.getQueryTables(queryWrapper);
        if (tables == null || tables.isEmpty()) {
            super.prepareAuth(queryWrapper, operateType);
            return;
        }
 
        // 2. 核心业务逻辑：平铺循环处理查询中的每个表
        for (QueryTable table : tables) {
            String tableName = table.getName();
            Map<DataColumn.DataType, String> config = DataScopeContext.getColumnConfig(tableName);
 
            if (config == null || config.isEmpty()) {
                continue;
            }
 
            // 3. 处理不同维度的过滤
            // 3.1 部门维度过滤
            String deptColName = config.get(DataColumn.DataType.DEPT);
            List<Long> deptIds = scopeInfo.getDeptIds();
            if (deptColName != null && deptIds != null && !deptIds.isEmpty()) {
                queryWrapper.and(new QueryColumn(table, deptColName).in(deptIds));
            }
 
            // 3.2 用户维度过滤
            String userColName = config.get(DataColumn.DataType.USER);
            List<Long> userIds = scopeInfo.getUserIds();
            if (userColName != null && userIds != null && !userIds.isEmpty()) {
                queryWrapper.and(new QueryColumn(table, userColName).in(userIds));
            }
        }
 
        super.prepareAuth(queryWrapper, operateType);
    }
}
