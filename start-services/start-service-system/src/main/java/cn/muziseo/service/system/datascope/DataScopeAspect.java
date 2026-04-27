package cn.muziseo.service.system.datascope;

import cn.dev33.satoken.stp.StpUtil;
import cn.muziseo.common.cache.datascope.DataScopeCacheManager;
import cn.muziseo.common.core.domain.dto.DataScopeInfo;
import cn.muziseo.common.db.annotation.DataScope;
import cn.muziseo.common.db.datascope.DataScopeContext;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;



/**
 * 数据权限切面
 * <p>
 * 拦截标注了 @DataScope 的 Service 方法，解析当前用户的数据范围，
 * 将允许的部门 ID 列表放入 DataScopeContext。
 * MyBatis-Flex 的 prepareAuth 方言会自动在 SQL 中注入 dept_id 过滤条件。
 *
 * @author 木子软件
 */
@Aspect
@Component
@Slf4j
public class DataScopeAspect {

    @Resource
    private DataScopeService dataScopeService;

    @Before("@annotation(dataScope)")
    public void doBefore(JoinPoint point, DataScope dataScope) {
        try {
            Long userId = StpUtil.getLoginIdAsLong();
            
            // 使用缓存管理器（Provider端直连 Service）
            DataScopeInfo info = DataScopeCacheManager.getDataScope(userId, id -> {
                log.info("[本地数据权限] 缓存未命中，执行计算: userId={}", id);
                return dataScopeService.getDataScopeInfo(id);
            });
            
            DataScopeContext.set(info);

            log.debug("数据权限过滤: userId={}, filter={}, deptIds={}, userIds={}",
                    userId, info.isFilter(), info.getDeptIds(), info.getUserIds());
        }
        catch (Exception e) {
            log.warn("数据权限解析异常: {}", e.getMessage());
            DataScopeInfo info = new DataScopeInfo();
            info.setFilter(false);
            DataScopeContext.set(info);
        }
    }

    @After("@annotation(cn.muziseo.common.db.annotation.DataScope)")
    public void doAfter() {
        DataScopeContext.clear();
    }
}
