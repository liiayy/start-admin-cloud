package cn.muziseo.common.log.utils;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import cn.muziseo.common.core.constant.SaSessionConstants;
import cn.muziseo.common.core.datatracer.DataTracerTypeEnum;
import cn.muziseo.common.core.event.DataTracerEvent;
import cn.muziseo.common.core.utils.servlet.ServletUtils;
import cn.muziseo.common.core.utils.spring.SpringUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
public class DataTracerUtils {

    /**
     * 记录数据新增
     */
    public static void insert(Long dataId, DataTracerTypeEnum type) {
        publish(dataId, type, "新增" + type.getDesc(), null, null);
    }

    /**
     * 自定义记录变更
     */
    public static void addTrace(Long dataId, DataTracerTypeEnum type, String content, String diffOld, String diffNew) {
        publish(dataId, type, content, diffOld, diffNew);
    }

    /**
     * 记录数据修改（自动比对属性差异）
     */
    public static void update(Long dataId, DataTracerTypeEnum type, Object oldBean, Object newBean) {
        String diffOld = DataTracerHelper.getChangeContent(oldBean);
        String diffNew = DataTracerHelper.getChangeContent(newBean);
        if (StrUtil.equals(diffOld, diffNew)) {
            return;
        }
        publish(dataId, type, "修改" + type.getDesc(), diffOld, diffNew);
    }

    /**
     * 记录数据删除
     */
    public static void delete(Long dataId, DataTracerTypeEnum type) {
        publish(dataId, type, "删除" + type.getDesc(), null, null);
    }

    /**
     * 批量记录数据删除
     */
    public static void batchDelete(Long[] dataIds, DataTracerTypeEnum type) {
        if (dataIds == null || dataIds.length == 0) {
            return;
        }
        for (Long dataId : dataIds) {
            delete(dataId, type);
        }
    }

    /**
     * 抓取当前主线程上下文属性，并本地发布异步 Spring Event
     */
    private static void publish(Long dataId, DataTracerTypeEnum type, String content, String diffOld, String diffNew) {
        try {
            DataTracerEvent event = new DataTracerEvent();
            event.setDataId(dataId);
            event.setType(type.getValue());
            event.setContent(content);
            event.setDiffOld(diffOld);
            event.setDiffNew(diffNew);
            event.setCreateTime(LocalDateTime.now());

            // 1. 同步抓取 Web 线程上下文的 IP 和 UserAgent
            HttpServletRequest request = ServletUtils.getRequest();
            if (request != null) {
                String ip = ServletUtils.getClientIP();
                event.setOperIp(ip);
                try {
                    IpLocationUtils ipLocationUtils = SpringUtils.getBean(IpLocationUtils.class);
                    if (ipLocationUtils != null) {
                        event.setOperLocation(ipLocationUtils.getLocation(ip));
                    }
                } catch (Exception ignored) {}
                String userAgent = request.getHeader("User-Agent");
                event.setUserAgent(StrUtil.sub(userAgent, 0, 500));
            }

            // 2. 同步抓取 Sa-Token 操作人账号
            try {
                if (StpUtil.isLogin()) {
                    String sessionUsername = StpUtil.getSession().getString(SaSessionConstants.USERNAME);
                    event.setOperName(sessionUsername != null ? sessionUsername : StpUtil.getLoginIdAsString());
                }
            } catch (Exception ignored) {
                event.setOperName("system");
            }

            // 3. 发布 Spring 本地事件
            SpringUtils.context().publishEvent(event);
        } catch (Exception e) {
            log.error("发布数据变更记录事件失败", e);
        }
    }
}
