package cn.muziseo.service.system.module.system.service.impl;

import cn.muziseo.common.cache.config.ConfigCacheManager;
import cn.muziseo.common.core.exception.BusinessException;
import cn.muziseo.common.db.page.PageResponse;
import cn.muziseo.common.log.utils.DataTracerUtils;
import cn.muziseo.service.system.enums.SystemErrorCode;
import cn.muziseo.service.system.module.system.controller.request.SystemConfigCreateRequest;
import cn.muziseo.service.system.module.system.controller.request.SystemConfigPageRequest;
import cn.muziseo.service.system.module.system.controller.vo.SystemConfigVO;
import cn.muziseo.service.system.module.system.convert.SystemConfigConverter;
import cn.muziseo.service.system.module.system.manager.SystemConfigManager;
import cn.muziseo.service.system.module.system.repository.entity.SystemConfigEntity;
import com.mybatisflex.core.paginate.Page;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * SystemConfigServiceImpl 单元测试
 *
 * @author 木子软件
 */
@Tag("dev")
@Tag("test")
@ExtendWith(MockitoExtension.class)
class SystemConfigServiceImplTest {

    @Mock
    private SystemConfigManager systemConfigManager;

    @Mock
    private SystemConfigConverter systemConfigConverter;

    @InjectMocks
    private SystemConfigServiceImpl systemConfigService;

    @Nested
    @DisplayName("pageConfig - 分页查询参数")
    class PageConfigTests {
        @Test
        @DisplayName("正常分页查询")
        void pageConfig_normal_returnsPageResponse() {
            // Given
            SystemConfigPageRequest request = new SystemConfigPageRequest();
            Page<SystemConfigEntity> page = new Page<>(1, 10);
            SystemConfigEntity entity = new SystemConfigEntity();
            entity.setId(1L);
            page.setRecords(List.of(entity));
            page.setTotalRow(1);
            when(systemConfigManager.pageConfig(request)).thenReturn(page);

            SystemConfigVO vo = SystemConfigVO.builder().id(1L).build();
            when(systemConfigConverter.toVO(entity)).thenReturn(vo);

            // When
            PageResponse<SystemConfigVO> result = systemConfigService.pageConfig(request);

            // Then
            assertThat(result.getList()).hasSize(1);
            assertThat(result.getTotal()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("getConfigValue - 获取配置值")
    class GetConfigValueTests {
        @Test
        @DisplayName("参数存在且为公开时返回参数值")
        void getConfigValue_public_returnsValue() {
            SystemConfigEntity entity = new SystemConfigEntity();
            entity.setConfigKey("sys.captcha");
            entity.setConfigValue("true");
            entity.setIsPublic("Y");
            when(systemConfigManager.getByConfigKey("sys.captcha")).thenReturn(entity);

            String result = systemConfigService.getConfigValue("sys.captcha");
            assertThat(result).isEqualTo("true");
        }

        @Test
        @DisplayName("参数非公开时返回 null")
        void getConfigValue_private_returnsNull() {
            SystemConfigEntity entity = new SystemConfigEntity();
            entity.setConfigKey("sys.private.key");
            entity.setConfigValue("secret");
            entity.setIsPublic("N");
            when(systemConfigManager.getByConfigKey("sys.private.key")).thenReturn(entity);

            String result = systemConfigService.getConfigValue("sys.private.key");
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("参数不存在时返回 null")
        void getConfigValue_nonexistent_returnsNull() {
            when(systemConfigManager.getByConfigKey("nonexistent")).thenReturn(null);

            String result = systemConfigService.getConfigValue("nonexistent");
            assertThat(result).isNull();
        }
    }

    @Nested
    @DisplayName("getConfigValues - 批量获取公开配置值")
    class GetConfigValuesTests {
        @Test
        @DisplayName("批量获取正常返回 Map")
        void getConfigValues_normal_returnsMap() {
            SystemConfigEntity e1 = new SystemConfigEntity();
            e1.setConfigKey("sys.captcha");
            e1.setConfigValue("true");
            e1.setIsPublic("Y");

            SystemConfigEntity e2 = new SystemConfigEntity();
            e2.setConfigKey("sys.private.key");
            e2.setConfigValue("secret");
            e2.setIsPublic("N");

            when(systemConfigManager.getByConfigKey("sys.captcha")).thenReturn(e1);
            when(systemConfigManager.getByConfigKey("sys.private.key")).thenReturn(e2);

            Map<String, String> result = systemConfigService.getConfigValues(List.of("sys.captcha", "sys.private.key"));
            assertThat(result).hasSize(1);
            assertThat(result).containsEntry("sys.captcha", "true");
        }
    }

    @Nested
    @DisplayName("createConfig - 新增系统参数")
    class CreateConfigTests {
        @Test
        @DisplayName("参数键名已存在时抛出异常")
        void createConfig_keyExists_throwsException() {
            SystemConfigCreateRequest request = new SystemConfigCreateRequest();
            request.setConfigKey("sys.captcha");
            when(systemConfigManager.existsByConfigKey("sys.captcha")).thenReturn(true);

            assertThatThrownBy(() -> systemConfigService.createConfig(request))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", SystemErrorCode.CONFIG_KEY_EXISTS);
        }

        @Test
        @DisplayName("正常创建并清空缓存")
        void createConfig_normal_success() {
            try (MockedStatic<DataTracerUtils> dataTracer = mockStatic(DataTracerUtils.class);
                 MockedStatic<ConfigCacheManager> cacheMock = mockStatic(ConfigCacheManager.class)) {

                SystemConfigCreateRequest request = new SystemConfigCreateRequest();
                request.setConfigKey("sys.captcha");
                when(systemConfigManager.existsByConfigKey("sys.captcha")).thenReturn(false);

                SystemConfigEntity entity = new SystemConfigEntity();
                entity.setId(10L);
                entity.setConfigKey("sys.captcha");
                when(systemConfigConverter.toEntity(request)).thenReturn(entity);

                systemConfigService.createConfig(request);

                verify(systemConfigManager, times(1)).save(entity);
                dataTracer.verify(() -> DataTracerUtils.insert(10L, cn.muziseo.common.core.datatracer.DataTracerTypeEnum.SYSTEM_CONFIG), times(1));
                cacheMock.verify(() -> ConfigCacheManager.evictCache("sys.captcha"), times(1));
            }
        }
    }

    @Nested
    @DisplayName("deleteConfig - 删除系统参数")
    class DeleteConfigTests {
        @Test
        @DisplayName("参数不存在时抛出异常")
        void deleteConfig_nonexistent_throwsException() {
            when(systemConfigManager.getById(99L)).thenReturn(null);

            assertThatThrownBy(() -> systemConfigService.deleteConfig(99L))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", SystemErrorCode.CONFIG_NOT_EXISTS);
        }

        @Test
        @DisplayName("参数为内置参数时禁止删除")
        void deleteConfig_builtin_throwsException() {
            SystemConfigEntity entity = new SystemConfigEntity();
            entity.setId(10L);
            entity.setBuiltin("Y");
            when(systemConfigManager.getById(10L)).thenReturn(entity);

            assertThatThrownBy(() -> systemConfigService.deleteConfig(10L))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", SystemErrorCode.CONFIG_BUILTIN_CANNOT_DELETE);
        }

        @Test
        @DisplayName("正常删除并清除对应缓存")
        void deleteConfig_normal_success() {
            try (MockedStatic<DataTracerUtils> dataTracer = mockStatic(DataTracerUtils.class);
                 MockedStatic<ConfigCacheManager> cacheMock = mockStatic(ConfigCacheManager.class)) {

                SystemConfigEntity entity = new SystemConfigEntity();
                entity.setId(10L);
                entity.setBuiltin("N");
                entity.setConfigKey("sys.captcha");
                when(systemConfigManager.getById(10L)).thenReturn(entity);

                systemConfigService.deleteConfig(10L);

                verify(systemConfigManager, times(1)).removeById(10L);
                dataTracer.verify(() -> DataTracerUtils.delete(10L, cn.muziseo.common.core.datatracer.DataTracerTypeEnum.SYSTEM_CONFIG), times(1));
                cacheMock.verify(() -> ConfigCacheManager.evictCache("sys.captcha"), times(1));
            }
        }
    }
}
