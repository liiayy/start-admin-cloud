package cn.muziseo.common.core.utils.stream;

import cn.hutool.core.collection.CollUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StreamUtils {

    /**
     * 将collection过滤
     *
     * @param collection 需要转化的集合
     * @param function   过滤方法
     * @return 过滤后的list
     */
    public static <E> List<E> filter(Collection<E> collection, Predicate<E> function) {
        if (CollUtil.isEmpty(collection)) {
            return CollUtil.newArrayList();
        }
        // 注意此处不要使用 .toList() 新语法 因为返回的是不可变List 会导致序列化问题
        return collection.stream().filter(function).collect(Collectors.toList());
    }

    /**
     * 找到流中满足条件的第一个元素
     *
     * @param collection 需要查询的集合
     * @param function   过滤方法
     * @return 找到符合条件的第一个元素，没有则返回null
     */
    public static <E> E findFirst(Collection<E> collection, Predicate<E> function) {
        if (CollUtil.isEmpty(collection)) {
            return null;
        }
        return collection.stream().filter(function).findFirst().orElse(null);
    }
}
