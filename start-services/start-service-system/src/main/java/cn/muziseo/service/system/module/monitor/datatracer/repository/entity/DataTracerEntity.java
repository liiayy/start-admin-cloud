package cn.muziseo.service.system.module.monitor.datatracer.repository.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 数据变更记录实体
 */
@Data
@Table("system_data_tracer")
public class DataTracerEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    private Long dataId;
    private Integer type;
    private String content;
    private String diffOld;
    private String diffNew;
    private String operName;
    private String operIp;
    private String operLocation;
    private String userAgent;
    private LocalDateTime createTime;
}
