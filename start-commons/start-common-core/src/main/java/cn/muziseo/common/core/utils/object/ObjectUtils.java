package cn.muziseo.common.core.utils.object;

import cn.hutool.core.util.ObjectUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.function.Function;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ObjectUtils extends ObjectUtil {

    /**
     * 如果对象不为空，则获取对象中的某个字段 ObjectUtils.notNullGetter(user, User::getName);
     *
     * @param obj  对象
     * @param func 获取方法
     * @return 对象字段
     */
    public static <T, E> E notNullGetter(T obj, Function<T, E> func) {
        if (isNotNull(obj) && isNotNull(func)) {
            return func.apply(obj);
        }
        return null;
    }
}
