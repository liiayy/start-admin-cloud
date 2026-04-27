package cn.muziseo.common.core.utils.mask;
 
import cn.hutool.core.util.StrUtil;
import cn.muziseo.common.core.utils.json.JsonUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
 
import java.util.regex.Matcher;
import java.util.regex.Pattern;
 
/**
 * 脱敏工具类
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SensitiveUtils {
 
    /** 默认敏感关键字（不区分大小写） */
    private static final String[] DEFAULT_SENSITIVE_KEYS = { "password", "oldPassword", "newPassword", "confirmPassword", "token", "access_token" };
 
    /**
     * 对对象进行脱敏并转为 JSON 字符串
     * <p>
     * 依赖于对象属性上的 @Sensitive 注解。
     */
    public static String toMaskJson(Object obj) {
        if (obj == null) return null;
        // Jackson 会自动根据 @Sensitive 调用 SensitiveSerializer
        return JsonUtils.toJsonString(obj);
    }
 
    /**
     * 对 JSON 字符串进行关键字脱敏（备用逻辑）
     *
     * @param json JSON 字符串
     * @param extraKeys 额外的敏感关键字
     * @return 脱敏后的 JSON
     */
    public static String maskJsonString(String json, String... extraKeys) {
        if (StrUtil.isBlank(json)) return json;
        
        String result = json;
        // 处理默认关键字和传入的关键字
        String[] keys = DEFAULT_SENSITIVE_KEYS;
        if (extraKeys != null && extraKeys.length > 0) {
            String[] merged = new String[keys.length + extraKeys.length];
            System.arraycopy(keys, 0, merged, 0, keys.length);
            System.arraycopy(extraKeys, 0, merged, keys.length, extraKeys.length);
            keys = merged;
        }
 
        for (String key : keys) {
            // 正则匹配 "key":"value" 或 "key":value
            // 改进正则以支持更多格式
            String regex = "(?i)(\"" + key + "\")\\s*:\\s*(\"[^\"]+\"|[^,}\\]\\s]+)";
            Matcher matcher = Pattern.compile(regex).matcher(result);
            if (matcher.find()) {
                result = matcher.replaceAll("$1:\"******\"");
            }
        }
        return result;
    }
}
