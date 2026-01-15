package cn.muziseo.system.module.demo.repository.entity;

import cn.muziseo.common.db.base.BaseDO;
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
public class DemoEntity extends BaseDO {

    @TableId
    private Long id;

    private String name;
}
