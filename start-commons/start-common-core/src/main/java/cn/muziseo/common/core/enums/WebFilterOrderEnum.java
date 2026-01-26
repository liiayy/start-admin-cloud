package cn.muziseo.common.core.enums;

public interface WebFilterOrderEnum {
    int XSS_FILTER = -102;  // 需要保证在 RequestBodyCacheFilter 后面
}
