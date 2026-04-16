package cn.muziseo.common.http.client.gaode;


import cn.muziseo.common.core.utils.spring.SpringUtils;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.interceptor.Interceptor;
import lombok.extern.slf4j.Slf4j;

/**
 * 高德地图请求拦截器
 * 自动注入 API Key
 *
 * @author 木子软件
 */
@Slf4j
public class GaodeMapInterceptor implements Interceptor<Object> {

    @Override
    public boolean beforeExecute(ForestRequest request) {
        GaodeMapProperties properties = SpringUtils.getBean(GaodeMapProperties.class);

        if (properties == null || !Boolean.TRUE.equals(properties.getEnabled())) {
            log.warn("[高德地图] 服务未启用，终止请求");
            return false;
        }

        // 统一添加 API Key 参数
        request.addQuery("key", properties.getApiKey());
        return true;
    }
}
