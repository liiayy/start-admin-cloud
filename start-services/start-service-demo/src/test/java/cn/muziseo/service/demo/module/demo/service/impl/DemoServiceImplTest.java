package cn.muziseo.service.demo.module.demo.service.impl;

import cn.muziseo.common.core.datatracer.DataTracerTypeEnum;
import cn.muziseo.common.core.exception.BusinessException;
import cn.muziseo.common.db.page.PageResponse;
import cn.muziseo.common.log.utils.DataTracerUtils;
import cn.muziseo.service.demo.enums.DemoErrorCode;
import cn.muziseo.service.demo.module.demo.controller.converter.DemoConverter;
import cn.muziseo.service.demo.module.demo.controller.request.DemoAddRequest;
import cn.muziseo.service.demo.module.demo.controller.request.DemoPageRequest;
import cn.muziseo.service.demo.module.demo.controller.vo.DemoVO;
import cn.muziseo.service.demo.module.demo.manager.DemoManager;
import cn.muziseo.service.demo.module.demo.repository.entity.DemoEntity;
import cn.muziseo.service.system.module.auth.api.UserApi;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * DemoServiceImpl 单元测试
 *
 * @author 木子软件
 */
@Tag("dev")
@Tag("test")
@ExtendWith(MockitoExtension.class)
class DemoServiceImplTest {

    @Mock
    private DemoManager demoManager;

    @Mock
    private DemoConverter demoConverter;

    @Mock
    private RedissonClient redissonClient;

    @Mock
    private UserApi userApi;

    @InjectMocks
    private DemoServiceImpl demoService;

    // ==================== page ====================

    @Nested
    @DisplayName("page - 分页查询")
    class PageTests {

        @Test
        @DisplayName("正常分页查询且名称过滤")
        void page_validRequest_returnsPageResponse() {
            // Given
            DemoPageRequest request = new DemoPageRequest();
            request.setPageNum(1);
            request.setPageSize(10);
            request.setName("test");

            Page<DemoEntity> mockPage = new Page<>(1, 10);
            DemoEntity entity = new DemoEntity();
            entity.setId(1L);
            entity.setName("test_product");
            mockPage.setRecords(List.of(entity));
            mockPage.setTotalRow(1);

            when(demoManager.page(any(Page.class), any(QueryWrapper.class))).thenReturn(mockPage);

            DemoVO vo = new DemoVO();
            vo.setId(1L);
            vo.setName("test_product");
            when(demoConverter.toVO(entity)).thenReturn(vo);

            // When
            PageResponse<DemoVO> result = demoService.page(request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getTotal()).isEqualTo(1);
            assertThat(result.getList()).hasSize(1);
            assertThat(result.getList().get(0).getName()).isEqualTo("test_product");
        }
    }

    // ==================== getById ====================

    @Nested
    @DisplayName("getById - 获取详情")
    class GetByIdTests {

        @Test
        @DisplayName("当记录存在时返回 VO")
        void getById_existingId_returnsDemoVO() {
            // Given
            DemoEntity entity = new DemoEntity();
            entity.setId(1L);
            entity.setName("test");
            when(demoManager.getById(1L)).thenReturn(entity);

            DemoVO vo = new DemoVO();
            vo.setId(1L);
            vo.setName("test");
            when(demoConverter.toVO(entity)).thenReturn(vo);

            // When
            DemoVO result = demoService.getById(1L);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("test");
        }

        @Test
        @DisplayName("当记录不存在时抛出异常")
        void getById_nonexistentId_throwsBusinessException() {
            // Given
            when(demoManager.getById(999L)).thenReturn(null);

            // When & Then
            assertThatThrownBy(() -> demoService.getById(999L))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", DemoErrorCode.DEMO_NOT_EXISTS);
        }
    }

    // ==================== create ====================

    @Nested
    @DisplayName("create - 创建产品")
    class CreateTests {

        @Test
        @DisplayName("当名称冲突时抛出异常")
        void create_duplicateName_throwsBusinessException() {
            // Given
            DemoAddRequest request = new DemoAddRequest();
            request.setName("duplicate");
            when(demoManager.existsByName("duplicate", null)).thenReturn(true);

            // When & Then
            assertThatThrownBy(() -> demoService.create(request))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", DemoErrorCode.DEMO_NAME_EXISTS);

            verify(demoManager, never()).save(any());
        }

        @Test
        @DisplayName("正常创建并记录数据审计日志")
        void create_validRequest_savesProduct() {
            // Given
            DemoAddRequest request = new DemoAddRequest();
            request.setName("new_product");
            when(demoManager.existsByName("new_product", null)).thenReturn(false);

            DemoEntity entity = new DemoEntity();
            entity.setId(10L);
            entity.setName("new_product");
            when(demoConverter.toEntity(request)).thenReturn(entity);

            try (MockedStatic<DataTracerUtils> dataTracerUtilsMock = mockStatic(DataTracerUtils.class)) {
                // When
                demoService.create(request);

                // Then
                verify(demoManager).save(entity);
                dataTracerUtilsMock.verify(() -> DataTracerUtils.insert(10L, DataTracerTypeEnum.DEMO));
            }
        }
    }

    // ==================== update ====================

    @Nested
    @DisplayName("update - 修改产品")
    class UpdateTests {

        @Test
        @DisplayName("当产品不存在时抛出异常")
        void update_nonexistentId_throwsBusinessException() {
            // Given
            DemoAddRequest request = new DemoAddRequest();
            request.setName("test");
            when(demoManager.getById(999L)).thenReturn(null);

            // When & Then
            assertThatThrownBy(() -> demoService.update(999L, request))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", DemoErrorCode.DEMO_NOT_EXISTS);
        }

        @Test
        @DisplayName("当名称与其它产品冲突时抛出异常")
        void update_duplicateName_throwsBusinessException() {
            // Given
            DemoEntity existing = new DemoEntity();
            existing.setId(1L);
            existing.setName("old_name");
            when(demoManager.getById(1L)).thenReturn(existing);
            when(demoManager.existsByName("new_name", 1L)).thenReturn(true);

            DemoAddRequest request = new DemoAddRequest();
            request.setName("new_name");

            // When & Then
            assertThatThrownBy(() -> demoService.update(1L, request))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", DemoErrorCode.DEMO_NAME_EXISTS);
        }

        @Test
        @DisplayName("正常修改产品并记录更新日志")
        void update_validRequest_updatesSuccessfully() {
            // Given
            DemoEntity existing = new DemoEntity();
            existing.setId(1L);
            existing.setName("old_name");
            when(demoManager.getById(1L)).thenReturn(existing);
            when(demoManager.existsByName("new_name", 1L)).thenReturn(false);

            DemoAddRequest request = new DemoAddRequest();
            request.setName("new_name");

            try (MockedStatic<DataTracerUtils> dataTracerUtilsMock = mockStatic(DataTracerUtils.class)) {
                // When
                demoService.update(1L, request);

                // Then
                ArgumentCaptor<DemoEntity> captor = ArgumentCaptor.forClass(DemoEntity.class);
                verify(demoManager).updateById(captor.capture());
                assertThat(captor.getValue().getName()).isEqualTo("new_name");

                dataTracerUtilsMock.verify(() -> DataTracerUtils.update(
                        eq(1L),
                        eq(DataTracerTypeEnum.DEMO),
                        any(DemoEntity.class),
                        any(DemoEntity.class)
                ));
            }
        }
    }

    // ==================== delete ====================

    @Nested
    @DisplayName("delete - 删除产品")
    class DeleteTests {

        @Test
        @DisplayName("当产品不存在时抛出异常")
        void delete_nonexistentId_throwsBusinessException() {
            // Given
            when(demoManager.getById(999L)).thenReturn(null);

            // When & Then
            assertThatThrownBy(() -> demoService.delete(999L))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", DemoErrorCode.DEMO_NOT_EXISTS);
        }

        @Test
        @DisplayName("正常删除产品并记录日志")
        void delete_existingId_removesProduct() {
            // Given
            DemoEntity existing = new DemoEntity();
            existing.setId(1L);
            existing.setName("test");
            when(demoManager.getById(1L)).thenReturn(existing);

            try (MockedStatic<DataTracerUtils> dataTracerUtilsMock = mockStatic(DataTracerUtils.class)) {
                // When
                demoService.delete(1L);

                // Then
                verify(demoManager).removeById(1L);
                dataTracerUtilsMock.verify(() -> DataTracerUtils.delete(1L, DataTracerTypeEnum.DEMO));
            }
        }
    }

    // ==================== executeWithLock ====================

    @Nested
    @DisplayName("executeWithLock - 分布式并发锁测试")
    class LockTests {

        @Test
        @DisplayName("获取锁成功并执行业务")
        void executeWithLock_success_returnsSuccessMessage() throws InterruptedException {
            // Given
            RLock mockLock = mock(RLock.class);
            when(redissonClient.getLock("demo:lock:resource_key")).thenReturn(mockLock);
            when(mockLock.tryLock(0, 5, TimeUnit.SECONDS)).thenReturn(true);
            when(mockLock.isHeldByCurrentThread()).thenReturn(true);

            // When
            // 为了快速测试，我们将 Thread.sleep(3000) 用短时 Mock 或在此测试中直接执行（单测时间限制）
            // 我们可以在 executeWithLock 中缩短时间或利用反射，但在单测中，我们依然可以直接运行（仅需3秒，如果追求快速，可以在实际代码中做模拟）
            // 这是一个简单的单元测试，直接执行即可
            String result = demoService.executeWithLock("resource_key");

            // Then
            assertThat(result).isEqualTo("锁任务执行成功！");
            verify(mockLock).unlock();
        }

        @Test
        @DisplayName("获取锁失败抛出锁失败异常")
        void executeWithLock_failedToAcquire_throwsBusinessException() throws InterruptedException {
            // Given
            RLock mockLock = mock(RLock.class);
            when(redissonClient.getLock("demo:lock:resource_key")).thenReturn(mockLock);
            when(mockLock.tryLock(0, 5, TimeUnit.SECONDS)).thenReturn(false);

            // When & Then
            assertThatThrownBy(() -> demoService.executeWithLock("resource_key"))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", DemoErrorCode.LOCK_FAILED);
        }
    }

    // ==================== testSeata ====================

    @Nested
    @DisplayName("testSeata - Seata 分布式事务测试")
    class SeataTests {

        @Test
        @DisplayName("事务执行成功，无异常")
        void testSeata_noException_executesSuccessfully() {
            // Given
            Long userId = 1L;
            String nickname = "NewNick";
            String demoName = "NewDemo";

            // When
            demoService.testSeata(userId, nickname, demoName, false);

            // Then
            verify(userApi).updateNickname(userId, nickname);
            verify(demoManager).save(any(DemoEntity.class));
        }

        @Test
        @DisplayName("抛出故意报错，触发回滚")
        void testSeata_throwEx_throwsException() {
            // Given
            Long userId = 1L;
            String nickname = "NewNick";
            String demoName = "NewDemo";

            // When & Then
            assertThatThrownBy(() -> demoService.testSeata(userId, nickname, demoName, true))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", DemoErrorCode.CUSTOM_DEMO_ERROR);

            verify(userApi).updateNickname(userId, nickname);
            verify(demoManager).save(any(DemoEntity.class));
        }
    }
}
