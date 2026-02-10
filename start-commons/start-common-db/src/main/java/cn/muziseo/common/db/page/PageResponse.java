package cn.muziseo.common.db.page;

import com.mybatisflex.core.paginate.Page;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 分页响应数据对象
 * <p>
 * 封装分页查询结果，包括总记录数和当前页数据列表
 * 提供多种便捷方法用于构建分页响应
 * </p>
 *
 * @author ruoyi
 * @Date 2026-01-15
 * @Wechat liiayy
 * @Email 773582348@qq.com
 * @Copyright <a href="https://code.muziseo.cn">木子软件</a>
 */
@Data
@NoArgsConstructor
public class PageResponse<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 总记录数
     */
    private long total;

    /**
     * 列表数据
     */
    private List<T> list;

    /**
     * 构造分页响应对象
     *
     * @param list  列表数据
     * @param total 总记录数
     */
    public PageResponse(List<T> list, int total) {
        this.list = list;
        this.total = total;
    }

    /**
     * 根据列表数据构建分页响应对象
     * <p>
     * 总记录数自动设置为列表大小
     * </p>
     *
     * @param list 列表数据
     * @param <T>  数据类型
     * @return 分页响应对象
     */
    public static <T> PageResponse<T> build(List<T> list) {
        PageResponse<T> rspData = new PageResponse<>();
        rspData.setList(list);
        rspData.setTotal(list.size());
        return rspData;
    }

    /**
     * 构建空的分页响应对象
     *
     * @param <T> 数据类型
     * @return 空的分页响应对象
     */
    public static <T> PageResponse<T> build() {
        PageResponse<T> rspData = new PageResponse<>();

        return rspData;
    }

    /**
     * 根据 MyBatis-Flex 的 Page 对象构建分页响应
     *
     * @param page MyBatis-Flex 分页对象
     * @param <T>  数据类型
     * @return 分页响应对象
     */
    public static <T> PageResponse<T> build(Page<T> page) {
        PageResponse<T> rspData = new PageResponse<>();
        rspData.setList(page.getRecords());
        rspData.setTotal(page.getTotalRow());
        return rspData;
    }
}
