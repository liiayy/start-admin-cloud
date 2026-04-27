package cn.muziseo.common.db.annotation;
 
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
 
/**
 * 数据权限过滤字段注解
 * <p>
 * 标注在 Entity 类的字段上，用于声明该字段属于哪种数据权限维度。
 * 系统启动时会自动解析该注解并注册到 DataScopeContext。
 *
 * @author 木子软件
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DataColumn {
 
    /**
     * 权限类型
     */
    DataType value() default DataType.DEPT;
 
    /**
     * 字段别名（如果数据库字段名与 Entity 字段名不一致，可在此指定）
     */
    String alias() default "";
 
    /**
     * 维度类型枚举
     */
    enum DataType {
        /**
         * 部门维度（对应 dept_id）
         */
        DEPT,
        /**
         * 用户维度（对应 user_id 或 create_by）
         */
        USER
    }
}
