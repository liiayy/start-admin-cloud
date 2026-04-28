package cn.muziseo.service.system.module.notice.controller.request;

import lombok.Data;

/**
 * 通知公告新增请求
 * 
 * @author 木子软件
 */
@Data
public class NoticeAddRequest {

    /**
     * 公告标题
     */
    private String title;

    /**
     * 公告类型 (1通知 2公告)
     */
    private Integer type;

    /**
     * 公告内容
     */
    private String content;

    /**
     * 状态 (0正常 1关闭)
     */
    private Integer status;
}
