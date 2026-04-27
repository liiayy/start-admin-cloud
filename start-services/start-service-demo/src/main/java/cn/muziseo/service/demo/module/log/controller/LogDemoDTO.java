package cn.muziseo.service.demo.module.log.controller;
 
import cn.muziseo.common.core.annotation.Sensitive;
import cn.muziseo.common.core.annotation.SensitiveStrategy;
import lombok.Data;
 
/**
 * 日志脱敏演示 DTO
 */
@Data
public class LogDemoDTO {
 
    private String username;
 
    @Sensitive(SensitiveStrategy.MOBILE)
    private String mobile;
 
    @Sensitive(SensitiveStrategy.ID_CARD)
    private String idCard;
 
    @Sensitive(SensitiveStrategy.PASSWORD)
    private String password;
    
    private String remark;
}
