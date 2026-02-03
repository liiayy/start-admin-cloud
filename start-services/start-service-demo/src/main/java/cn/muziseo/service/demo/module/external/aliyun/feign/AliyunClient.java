package cn.muziseo.service.demo.module.external.aliyun.feign;

import cn.muziseo.service.demo.module.external.aliyun.config.AliyunFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * 阿里云短信/推送客户端
 */
@FeignClient(name = "aliyun-sms-client", url = "${external.aliyun.url:https://dysmsapi.aliyuncs.com}", configuration = AliyunFeignConfig.class)
public interface AliyunClient {

    @PostMapping("/sendSms")
    String send(@RequestBody Map<String, Object> params);
}
