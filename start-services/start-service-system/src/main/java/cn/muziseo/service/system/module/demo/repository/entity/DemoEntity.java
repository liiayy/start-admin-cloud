package cn.muziseo.service.system.module.demo.repository.entity;

import cn.muziseo.common.db.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@TableName("demo")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DemoEntity extends BaseEntity {

    @TableId
    private Long id;

    private String name;
}
