package cn.muziseo.common.log.utils;

import cn.hutool.core.util.StrUtil;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.lionsoul.ip2region.xdb.Searcher;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import java.io.InputStream;

/**
 * IP 归属地查询组件 (基于 ip2region)
 */
@Slf4j
@Component
public class IpLocationUtils {

    private Searcher searcher;

    /**
     * 初始化加载 ip2region.xdb
     */
    @PostConstruct
    public void init() {
        try {
            ClassPathResource resource = new ClassPathResource("ip2region/ip2region.xdb");
            if (resource.exists()) {
                InputStream inputStream = resource.getInputStream();
                byte[] cBuff = FileCopyUtils.copyToByteArray(inputStream);
                searcher = Searcher.newWithBuffer(cBuff);
                log.info("ip2region.xdb 加载成功");
            } else {
                log.warn("未找到 ip2region/ip2region.xdb 文件，IP归属地查询功能将失效");
            }
        } catch (Exception e) {
            log.error("初始化 ip2region 失败", e);
        }
    }

    /**
     * 根据 IP 获取地理位置
     */
    public String getLocation(String ip) {
        if (searcher == null || StrUtil.isBlank(ip) || "127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip)) {
            return "内网IP";
        }
        try {
            String region = searcher.search(ip);
            // 返回格式：国家|区域|省份|城市|网络
            if (StrUtil.isNotBlank(region)) {
                String[] parts = region.split("\\|");
                if (parts.length >= 4) {
                    String province = parts[2];
                    String city = parts[3];
                    if ("0".equals(province)) return "未知";
                    return province.equals(city) ? province : province + city;
                }
            }
            return region;
        } catch (Exception e) {
            log.error("IP解析失败: {}", ip);
            return "未知";
        }
    }

    @PreDestroy
    public void destroy() {
        if (searcher != null) {
            try {
                searcher.close();
                log.info("ip2region 搜索器已关闭");
            } catch (Exception e) {
                log.error("关闭 ip2region 搜索器失败", e);
            }
        }
    }
}
