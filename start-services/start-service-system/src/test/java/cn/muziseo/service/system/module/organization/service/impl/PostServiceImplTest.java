package cn.muziseo.service.system.module.organization.service.impl;

import cn.muziseo.common.core.exception.BusinessException;
import cn.muziseo.common.db.page.PageResponse;
import cn.muziseo.common.log.utils.DataTracerUtils;
import cn.muziseo.service.system.enums.PostErrorCode;
import cn.muziseo.service.system.module.auth.manager.UserManager;
import cn.muziseo.service.system.module.organization.controller.request.PostCreateRequest;
import cn.muziseo.service.system.module.organization.controller.request.PostPageRequest;
import cn.muziseo.service.system.module.organization.controller.vo.PostVO;
import cn.muziseo.service.system.module.organization.convert.PostConverter;
import cn.muziseo.service.system.module.organization.manager.DeptManager;
import cn.muziseo.service.system.module.organization.manager.PostManager;
import cn.muziseo.service.system.module.organization.repository.entity.DeptEntity;
import cn.muziseo.service.system.module.organization.repository.entity.PostEntity;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * PostServiceImpl 单元测试
 *
 * @author 木子软件
 */
@Tag("dev")
@Tag("test")
@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    @Mock
    private PostManager postManager;

    @Mock
    private DeptManager deptManager;

    @Mock
    private UserManager userManager;

    @Mock
    private PostConverter postConverter;

    @InjectMocks
    private PostServiceImpl postService;

    @Nested
    @DisplayName("list - 获取所有岗位")
    class ListTests {
        @Test
        @DisplayName("正常获取岗位列表")
        void list_normal_returnsPostVOList() {
            // Given
            PostEntity post = new PostEntity();
            post.setId(1L);
            post.setDeptId(10L);
            when(postManager.listAll()).thenReturn(List.of(post));

            PostVO vo = PostVO.builder().id(1L).build();
            when(postConverter.toVO(post)).thenReturn(vo);

            DeptEntity dept = new DeptEntity();
            dept.setId(10L);
            dept.setName("开发部");
            when(deptManager.getById(10L)).thenReturn(dept);

            // When
            List<PostVO> result = postService.list();

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getDeptName()).isEqualTo("开发部");
        }
    }

    @Nested
    @DisplayName("pagePost - 分页查询岗位")
    class PagePostTests {
        @Test
        @DisplayName("正常分页查询")
        void pagePost_normal_returnsPageResponse() {
            // Given
            PostPageRequest request = new PostPageRequest();
            request.setDeptId(10L);
            when(deptManager.getDeptAndChildIds(10L)).thenReturn(List.of(10L, 11L));

            Page<PostEntity> page = new Page<>(1, 10);
            PostEntity post = new PostEntity();
            post.setId(1L);
            post.setDeptId(10L);
            page.setRecords(List.of(post));
            page.setTotalRow(1);
            when(postManager.pagePost(eq(request), any())).thenReturn(page);

            PostVO vo = PostVO.builder().id(1L).build();
            when(postConverter.toVO(post)).thenReturn(vo);

            DeptEntity dept = new DeptEntity();
            dept.setId(10L);
            dept.setName("开发部");
            when(deptManager.getById(10L)).thenReturn(dept);

            // When
            PageResponse<PostVO> result = postService.pagePost(request);

            // Then
            assertThat(result.getList()).hasSize(1);
            assertThat(result.getTotal()).isEqualTo(1);
            assertThat(result.getList().get(0).getDeptName()).isEqualTo("开发部");
        }
    }

    @Nested
    @DisplayName("getById - 获取岗位详情")
    class GetByIdTests {
        @Test
        @DisplayName("岗位存在时返回详情")
        void getById_existing_returnsVO() {
            // Given
            PostEntity post = new PostEntity();
            post.setId(1L);
            post.setDeptId(10L);
            when(postManager.getById(1L)).thenReturn(post);

            PostVO vo = PostVO.builder().id(1L).build();
            when(postConverter.toVO(post)).thenReturn(vo);

            DeptEntity dept = new DeptEntity();
            dept.setId(10L);
            dept.setName("开发部");
            when(deptManager.getById(10L)).thenReturn(dept);

            // When
            PostVO result = postService.getById(1L);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getDeptName()).isEqualTo("开发部");
        }

        @Test
        @DisplayName("岗位不存在时抛出 BusinessException")
        void getById_nonexistent_throwsException() {
            // Given
            when(postManager.getById(99L)).thenReturn(null);

            // When & Then
            assertThatThrownBy(() -> postService.getById(99L))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", PostErrorCode.POST_NOT_EXISTS);
        }
    }

    @Nested
    @DisplayName("createPost - 新增岗位")
    class CreatePostTests {
        @Test
        @DisplayName("编码已存在时抛出异常")
        void createPost_codeExists_throwsException() {
            // Given
            PostCreateRequest request = new PostCreateRequest();
            request.setCode("DEV");
            when(postManager.existsByCode("DEV", null)).thenReturn(true);

            // When & Then
            assertThatThrownBy(() -> postService.createPost(request))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", PostErrorCode.POST_CODE_EXISTS);
        }

        @Test
        @DisplayName("正常创建岗位")
        void createPost_normal_success() {
            try (MockedStatic<DataTracerUtils> dataTracer = mockStatic(DataTracerUtils.class)) {
                // Given
                PostCreateRequest request = new PostCreateRequest();
                request.setCode("DEV");
                when(postManager.existsByCode("DEV", null)).thenReturn(false);

                PostEntity entity = new PostEntity();
                entity.setId(1L);
                entity.setCode("DEV");
                when(postConverter.toEntity(request)).thenReturn(entity);

                // When
                postService.createPost(request);

                // Then
                verify(postManager, times(1)).save(entity);
                dataTracer.verify(() -> DataTracerUtils.insert(1L, cn.muziseo.common.core.datatracer.DataTracerTypeEnum.POST), times(1));
            }
        }
    }

    @Nested
    @DisplayName("updatePost - 修改岗位")
    class UpdatePostTests {
        @Test
        @DisplayName("岗位不存在时抛出异常")
        void updatePost_nonexistent_throwsException() {
            // Given
            when(postManager.getById(99L)).thenReturn(null);

            // When & Then
            assertThatThrownBy(() -> postService.updatePost(99L, new PostCreateRequest()))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", PostErrorCode.POST_NOT_EXISTS);
        }

        @Test
        @DisplayName("岗位编码已被其他岗位占用时抛出异常")
        void updatePost_codeExists_throwsException() {
            // Given
            PostEntity post = new PostEntity();
            post.setId(1L);
            when(postManager.getById(1L)).thenReturn(post);

            PostCreateRequest request = new PostCreateRequest();
            request.setCode("DEV");
            when(postManager.existsByCode("DEV", 1L)).thenReturn(true);

            // When & Then
            assertThatThrownBy(() -> postService.updatePost(1L, request))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", PostErrorCode.POST_CODE_EXISTS);
        }

        @Test
        @DisplayName("正常更新岗位")
        void updatePost_normal_success() {
            try (MockedStatic<DataTracerUtils> dataTracer = mockStatic(DataTracerUtils.class)) {
                // Given
                PostEntity post = new PostEntity();
                post.setId(1L);
                post.setCode("OLD_DEV");
                when(postManager.getById(1L)).thenReturn(post);

                PostCreateRequest request = new PostCreateRequest();
                request.setCode("NEW_DEV");
                when(postManager.existsByCode("NEW_DEV", 1L)).thenReturn(false);

                PostEntity entity = new PostEntity();
                entity.setId(1L);
                entity.setCode("NEW_DEV");
                when(postConverter.toEntity(request)).thenReturn(entity);

                // When
                postService.updatePost(1L, request);

                // Then
                verify(postManager, times(1)).updateById(entity);
                dataTracer.verify(() -> DataTracerUtils.update(eq(1L), eq(cn.muziseo.common.core.datatracer.DataTracerTypeEnum.POST), eq(post), any()), times(1));
            }
        }
    }

    @Nested
    @DisplayName("deletePost - 删除岗位")
    class DeletePostTests {
        @Test
        @DisplayName("岗位不存在时抛出异常")
        void deletePost_nonexistent_throwsException() {
            // Given
            when(postManager.getById(99L)).thenReturn(null);

            // When & Then
            assertThatThrownBy(() -> postService.deletePost(99L))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", PostErrorCode.POST_NOT_EXISTS);
        }

        @Test
        @DisplayName("岗位下有关联用户时抛出异常")
        void deletePost_hasUsers_throwsException() {
            // Given
            PostEntity post = new PostEntity();
            post.setId(1L);
            when(postManager.getById(1L)).thenReturn(post);
            when(userManager.countByPostId(1L)).thenReturn(5L);

            // When & Then
            assertThatThrownBy(() -> postService.deletePost(1L))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", PostErrorCode.POST_HAS_USERS);
        }

        @Test
        @DisplayName("正常删除岗位")
        void deletePost_normal_success() {
            try (MockedStatic<DataTracerUtils> dataTracer = mockStatic(DataTracerUtils.class)) {
                // Given
                PostEntity post = new PostEntity();
                post.setId(1L);
                when(postManager.getById(1L)).thenReturn(post);
                when(userManager.countByPostId(1L)).thenReturn(0L);

                // When
                postService.deletePost(1L);

                // Then
                verify(postManager, times(1)).removeById(1L);
                dataTracer.verify(() -> DataTracerUtils.delete(1L, cn.muziseo.common.core.datatracer.DataTracerTypeEnum.POST), times(1));
            }
        }
    }
}
