package cn.muziseo.service.system.module.auth.api.aspect;

import cn.dev33.satoken.stp.StpUtil;
import cn.muziseo.common.db.annotation.DataScope;
import cn.muziseo.common.db.datascope.DataScopeContext;
import cn.muziseo.common.core.domain.dto.DataScopeInfo;
import cn.muziseo.service.system.module.auth.api.PermissionApi;
import cn.muziseo.service.system.module.auth.api.dto.DataScopeRemoteDTO;
import cn.muziseo.common.cache.datascope.DataScopeCacheManager;
import cn.muziseo.common.core.domain.dto.DataScopeInfo;
import cn.muziseo.service.system.module.auth.api.PermissionApi;
import cn.muziseo.service.system.module.auth.api.dto.DataScopeRemoteDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;

/**
 * 远程数据权限切面
 */
@Aspect
@Slf4j
@ConditionalOnClass({DataScopeContext.class, DataScopeCacheManager.class})
public class RemoteDataScopeAspect {

    @Resource
    private PermissionApi permissionApi;

    @Before("@annotation(dataScope)")
    public void doBefore(DataScope dataScope) {
        try {
            Long userId = StpUtil.getLoginIdAsLong();
            
            // 使用二级缓存管理器包装 RPC 调用
            DataScopeInfo info = DataScopeCacheManager.getDataScope(userId, id -> {
                log.info("[远程数据权限] 缓存未命中，执行 RPC 调用: userId={}", id);
                DataScopeRemoteDTO dto = permissionApi.getDataScope(id);
                if (dto == null) return null;
                
                return DataScopeInfo.builder()
                        .filter(dto.isFilter())
                        .deptIds(dto.getDeptIds())
                        .build();
            });

            if (info != null) {
                DataScopeContext.set(info);
            }
        } catch (Exception e) {
            log.warn("[RemoteDataScope] 获取数据权限异常: {}", e.getMessage());
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
