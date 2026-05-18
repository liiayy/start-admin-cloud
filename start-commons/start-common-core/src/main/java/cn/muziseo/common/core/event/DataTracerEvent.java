package cn.muziseo.common.core.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataTracerEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 业务数据ID
     */
    private Long dataId;

    /**
     * 业务类型
     */
    private Integer type;

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

    /**
     * 操作人员账号
     */
    private String operName;

    /**
     * 操作IP
     */
    private String operIp;

    /**
     * 操作地点
     */
    private String operLocation;

    /**
     * 浏览器User-Agent
     */
    private String userAgent;

    /**
     * 触发时间
     */
    private LocalDateTime createTime;
}
