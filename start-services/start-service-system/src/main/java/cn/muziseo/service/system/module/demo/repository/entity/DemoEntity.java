package cn.muziseo.service.system.module.demo.repository.entity;

import cn.muziseo.common.db.entity.BaseEntity;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import lombok.*;

@Table("demo")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DemoEntity extends BaseEntity {

    @Id
    private Long id;

    private String name;
}
