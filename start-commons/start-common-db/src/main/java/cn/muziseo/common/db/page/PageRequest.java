package cn.muziseo.common.db.page;

import cn.hutool.core.convert.Convert;
import cn.muziseo.common.core.utils.servlet.ServletUtils;
import com.mybatisflex.core.paginate.Page;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 分页查询实体类
 * <p>
 * 封装分页查询参数，包括页码、每页大小、排序字段等
 * 支持从 HTTP 请求中自动提取分页参数
 * </p>
 *
 * @author Lion Li
 * @Date 2026-01-15
 * @Wechat liiayy
 * @Email 773582348@qq.com
 * @Copyright <a href="https://code.muziseo.cn">木子软件</a>
 */

@Data
public class PageRequest implements Serializable {

    /**
     * 当前记录起始索引
     */
    public static final String PAGE_NUM = "pageNum";
    /**
     * 每页显示记录数
     */
    public static final String PAGE_SIZE = "pageSize";
    /**
     * 当前记录起始索引 默认值
     */
    public static final int DEFAULT_PAGE_NUM = 1;
    /**
     * 每页显示记录数 默认值 默认查全部
     */
    public static final int DEFAULT_PAGE_SIZE = Integer.MAX_VALUE;
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 分页大小
     */
    private Integer pageSize;
    /**
     * 当前页数
     */
    private Integer pageNum;
    /**
     * 排序列
     */
    private String orderByColumn;
    /**
     * 排序的方向desc或者asc
     */
    private String isAsc;

    /**
     * 构造分页查询参数
     * <p>
     * 从 HTTP 请求中获取 pageNum 和 pageSize 参数，构建 MyBatis-Flex 的 Page 对象
     * </p>
     *
     * @param <T> 实体类型
     * @return MyBatis-Flex 分页对象
     */
    public static <T> Page<T> build() {
        Integer pageNum = Convert.toInt(ServletUtils.getParameter(PAGE_NUM), DEFAULT_PAGE_NUM);
        Integer pageSize = Convert.toInt(ServletUtils.getParameter(PAGE_SIZE), DEFAULT_PAGE_SIZE);
        if (pageNum <= 0) {
            pageNum = DEFAULT_PAGE_NUM;
        }
        Page<T> page = new Page<>(pageNum, pageSize);
        return page;
    }

}

