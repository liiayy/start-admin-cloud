package cn.muziseo.common.core.annotation;
 
import cn.hutool.core.util.DesensitizedUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
 
import java.util.function.Function;
 
/**
 * 脱敏策略枚举
 * <p>
 * 封装了常见的脱敏处理逻辑，底层调用 Hutool 的 DesensitizedUtil。
 *
 * @author 木子软件
 */
@Getter
@AllArgsConstructor
public enum SensitiveStrategy {
 
    /**
     * 姓名（只显示第一个汉字）
     */
    CHINESE_NAME(s -> DesensitizedUtil.chineseName(s)),
 
    /**
     * 身份证号
     */
    ID_CARD(s -> DesensitizedUtil.idCardNum(s, 1, 2)),
 
    /**
     * 固定电话
     */
    FIXED_PHONE(s -> DesensitizedUtil.fixedPhone(s)),
 
    /**
     * 手机号
     */
    MOBILE(s -> DesensitizedUtil.mobilePhone(s)),
 
    /**
     * 地址
     */
    ADDRESS(s -> DesensitizedUtil.address(s, 8)),
 
    /**
     * 电子邮件
     */
    EMAIL(s -> DesensitizedUtil.email(s)),
 
    /**
     * 银行卡
     */
    BANK_CARD(s -> DesensitizedUtil.bankCard(s)),
 
    /**
     * 密码（全部显示为*）
     */
    PASSWORD(s -> "******");
 
    private final Function<String, String> desensitizer;
}
