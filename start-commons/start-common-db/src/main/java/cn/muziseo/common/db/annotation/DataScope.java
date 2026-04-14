package cn.muziseo.common.db.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据权限注解
 * <p>
 * 标注在 Service 方法上，AOP 切面会自动解析当前用户的数据范围，
 * 将允许的部门 ID 列表放入 DataScopeContext。
 * MyBatis-Flex 的 prepareAuth 方言会自动在 SQL 中注入 dept_id 过滤条件。
 *
 * @author 木子软件
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DataScope {

    /**
     * 部门 ID 字段别名，默认 "dept_id"
     */
    String deptAlias() default "dept_id";

}
