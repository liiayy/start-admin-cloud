package cn.muziseo.service.system.module.monitor.repository.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 错误日志实体
 */
@Data
@Table("system_error_log")
public class SysErrorLogEntity implements Serializable {

    @Id(keyType = KeyType.Generator, value = "snowFlakeId")
    private Long id;

    /** 异常类名 */
    private String errorType;
    /** 错误消息 */
    private String errorMessage;
    /** 异常堆栈 */
    private String errorStack;
    /** 请求URI */
    private String requestUri;
    /** 请求方法 */
    private String requestMethod;
    /** 请求参数 */
    private String requestParams;
    /** 请求IP */
    private String requestIp;
    /** User-Agent */
    private String userAgent;
    /** 用户ID */
    private Long userId;
    /** 用户名 */
    private String userName;
    /** 模块名称 */
    private String moduleName;
    /** 追踪ID */
    private String traceId;
    /** 服务器名称 */
    private String serverName;
    /** 服务器IP */
    private String serverIp;

    /** 发生次数 */
    private Integer occurrenceCount;
    /** 处理状态（0待处理/1已处理/2已忽略） */
    private Integer handleStatus;
    /** 处理人 */
    private String handleBy;
    /** 处理时间 */
    private LocalDateTime handleTime;
    /** 处理备注 */
    private String handleRemark;

    /** 首次发生时间 */
    private LocalDateTime firstTime;
    /** 最后发生时间 */
    private LocalDateTime lastTime;
    /** 创建时间 */
    private LocalDateTime createTime;
}
