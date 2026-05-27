package cn.muziseo.service.demo.module.demo.repository.entity;

import cn.muziseo.common.core.datatracer.annotation.DataTracerFieldLabel;
import cn.muziseo.common.db.entity.BaseEntity;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import lombok.*;

/**
 * 演示产品实体类
 *
 * @author Antigravity
 */
@Table("demo")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DemoEntity extends BaseEntity {

    /**
     * ID 主键
     */
    @Id
    private Long id;

    /**
     * 名称
     */
    @DataTracerFieldLabel("产品名称")
    private String name;

}
