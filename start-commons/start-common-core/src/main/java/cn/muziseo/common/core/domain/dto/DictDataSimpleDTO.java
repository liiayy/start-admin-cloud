package cn.muziseo.common.core.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 字典数据简版 DTO（跨服务传输 & 缓存存储专用）
 * <p>
 * 相比 DictDataVO，此 DTO 不含 createTime 等审计字段，
 * 仅保留前端渲染和后端翻译所需的核心属性，减小序列化体积。
 * </p>
 *
 * @author 木子软件
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DictDataSimpleDTO implements Serializable {

    /** 字典标签 */
    private String label;

    /** 字典键值 */
    private String value;

    /** 字典类型 */
    private String dictType;

    /** 颜色类型（如 primary, success, danger, info, warning） */
    private String colorType;

    /** CSS 样式 */
    private String cssClass;
}
