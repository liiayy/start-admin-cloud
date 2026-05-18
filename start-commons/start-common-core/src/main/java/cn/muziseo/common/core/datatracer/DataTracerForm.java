package cn.muziseo.common.core.datatracer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 数据变更记录表单
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataTracerForm implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 业务数据ID
     */
    private Long dataId;

    /**
     * 业务类型
     */
    private DataTracerTypeEnum type;

    /**
     * 操作内容描述
     */
    private String content;

    /**
     * 变更前数据
     */
    private String diffOld;

    /**
     * 变更后数据
     */
    private String diffNew;
}
