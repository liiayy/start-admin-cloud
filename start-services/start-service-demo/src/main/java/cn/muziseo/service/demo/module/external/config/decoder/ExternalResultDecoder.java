package cn.muziseo.service.demo.module.external.config.decoder;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.muziseo.common.core.exception.BusinessException;
import feign.Response;
import feign.Util;
import feign.codec.Decoder;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * 通用外部接口拆包解码器
 * 假设格式为 { "code": 200, "msg": "ok", "data": { ... } }
 */
public class ExternalResultDecoder implements Decoder {
    private final Decoder delegate;

    public ExternalResultDecoder(Decoder delegate) {
        this.delegate = delegate;
    }

    @Override
    public Object decode(Response response, Type type) throws IOException {
        if (response.body() == null)
            return null;

        String body = Util.toString(response.body().asReader(Util.UTF_8));
        JSONObject jsonObject = JSONUtil.parseObj(body);

        // 逻辑处理：假设 200 或 0 是成功
        int code = jsonObject.getInt("code", 200);
        if (code == 200 || code == 0) {
            Object data = jsonObject.get("data");
            return JSONUtil.toBean(JSONUtil.toJsonStr(data), type, true);
        } else {
            throw new BusinessException("第三方接口返回错误: " + jsonObject.getStr("msg"));
        }
    }
}