package cn.muziseo.common.core.utils.date;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor(access = AccessLevel.PRIVATE) // 工具类
public class DateUtils extends org.apache.commons.lang3.time.DateUtils {

    /**
     * 获取当前日期和时间
     *
     * @return 当前日期和时间的 Date 对象表示
     */
    public static Date getNowDate() {
        return new Date();
    }

}
