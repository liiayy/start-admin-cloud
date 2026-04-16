package cn.muziseo.common.http.client.gaode;

import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.annotation.Query;
import com.dtflys.forest.http.ForestResponse;

/**
 * 高德地图 API 客户端
 *
 * @author 木子软件
 */
@BaseRequest(
    baseURL = "https://restapi.amap.com",
    interceptor = GaodeMapInterceptor.class
)
public interface GaodeMapClient {

    /**
     * IP 定位
     */
    @Get(url = "/v3/ip")
    ForestResponse<String> getLocationByIp(@Query("ip") String ip);

    /**
     * 天气查询
     */
    @Get(url = "/v3/weather/weatherInfo")
    ForestResponse<String> getWeather(@Query("city") String adcode);
}
