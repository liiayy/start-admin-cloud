package cn.muziseo.service.system.module.monitor.repository.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 登录日志实体类
 */
@Data
@Table("system_login_log")
public class LoginLogEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 访问ID
     */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * 用户账号
     */
    private String username;

    /**
     * 登录IP地址
     */
    private String loginIp;

    /**
     * 登录地点
     */
    private String loginLocation;

    /**
     * 浏览器类型
     */
    private String browser;

    /**
     * 操作系统
     */
    private String os;

    /**
     * 登录状态（0成功 1失败）
     */
    private Integer status;

    /**
     * 提示消息
     */
    private String msg;

    /**
     * 访问时间
     */
    private LocalDateTime createTime;

}
