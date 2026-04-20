package cn.muziseo.common.core.event;

import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 登录日志事件
 */
@Getter
@Setter
public class LoginLogEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;
    private String loginIp;
    private String loginLocation;
    private String browser;
    private String os;
    private Integer status;
    private String msg;
    private LocalDateTime createTime;
}
